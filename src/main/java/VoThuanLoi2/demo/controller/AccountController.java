package VoThuanLoi2.demo.controller;

import VoThuanLoi2.demo.entity.*;
import VoThuanLoi2.demo.models.CartItem;
import VoThuanLoi2.demo.services.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class AccountController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private BookService bookService;
    @Autowired
    private  TypeService typeService;
    @Autowired
    private FogetPasswordService fogetPasswordService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private MailService mailService;

    //Handel forgetPassWord
    private static String emailForgetPassword = null;
    //Handel fake cgv ticket
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    @GetMapping("/error")
    public String index(){
        return "account/error";
    }

    @GetMapping("/logout")
    public String logoutAccount(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        // Delete the JSESSIONID cookie
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/home";
    }

    @PostMapping("/register")
    public String handelRegister(@ModelAttribute("user") User user) throws ParseException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        //Add point to user in first login
        user.addAttendancePoints(2);

        //set default when register account is user
        userService.addUserWithRole(user,"user");

        return "redirect:/login";
    }

    //Function get user login
    private User getUSer(Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                // Retrieve user from the database
                User user = userService.getUser(username);
                return user;
            }
        }
        return null;
    }
    @GetMapping("/account")
    public String handleUser(Model model, Authentication authentication) {
        User user = getUSer(authentication);
        model.addAttribute("user", user);

        List<Favorite> getListFavorite = favoriteService.getFavoriteByUserId(user.getUser_id());
        int countFavorite = (getListFavorite != null) ? getListFavorite.size() : 0;
        model.addAttribute("countFavorite", countFavorite);

        List<Comment> getListComment = commentService.getCommentByUserId(user.getUser_id());
        int countComment= (getListComment != null) ? getListComment.size() : 0;
        model.addAttribute("countComment", countComment);

        List<Orders> getListOrder = orderService.getOrdersByUserName(user.getUsername());
        int countOrder = (getListOrder != null) ? getListOrder.size() : 0;
        model.addAttribute("countOrder", countOrder);

        List<Appointment> getListAppointment = appointmentService.findListWithUserId(Long.valueOf(user.getUser_id()));
        int countAppointment = (getListAppointment != null) ? getListAppointment.size() : 0;
        model.addAttribute("countAppointment", countAppointment);

        model.addAttribute("isFull", userService.isFull(user.getUser_id()));

        model.addAttribute("listHotDeal", bookService.getListTopSale(4));

        return "account/index";
    }

    @GetMapping("/account/comments")
    public String renderComments(Model model, Authentication authentication){
        model.addAttribute("listComment", bookService.getListBookComments(getUSer(authentication).getUser_id()));

        return "account/comments";
    }

    @GetMapping("/account/favorites")
    public String renderFavorites(Model model, Authentication authentication){
        model.addAttribute("listFavorites", bookService.getListBookFavorite(getUSer(authentication).getUser_id()));

        return "account/favorites";
    }

    @GetMapping("/account/orders")
    public String renderOrders(Model model, Authentication authentication){
        User user = getUSer(authentication);
        model.addAttribute("listOrder",orderService.getOrdersByUserName(user.getUsername()));

        return "account/orders";
    }

    @GetMapping("/account/orders/detail/{id}")
    public String renderOrdersDetail(@PathVariable("id") Long id,
                                     Model model,
                                     Authentication authentication){
        model.addAttribute("listOrderDetail", orderService.getListCartWithId(id));
        model.addAttribute("orderId", id);

        Orders orders = orderService.getOrderById(id);
        model.addAttribute("typeID", orders.getType().getId());

        String typeNameCheck = (orders.getType().getId() == 5) ? "Đã hủy" : "Xác nhận";
        model.addAttribute("typeNameCheck", typeNameCheck);

        return "account/order-detail";
    }

    @GetMapping("/account/update")
    public String updateAccount(Model model, Authentication authentication){
        User user = getUSer(authentication);
        if (user != null)
            model.addAttribute("user", user);

        return "account/edit";
    }
    @PostMapping("/account/update/{id}")
    public String updateAccount(
            @PathVariable("id") Integer id,
            @ModelAttribute("user") User updateUser,
            @RequestParam("province") String province,
            @RequestParam("district") String district,
            @RequestParam("ward") String ward,
            Model model){
        User user = userService.getUserById(id);
        if (user == null){
            return "error";
        }
        user.setName(updateUser.getName());
        user.setPhone(updateUser.getPhone());
        user.setAddress(updateUser.getAddress());
        user.setCity(province);
        user.setDistrict(district);
        user.setWard(ward);

        userService.saveUser(user);
        return "redirect:/account";
    }

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }
    @PostMapping("/logout")
    public String logout() {
        return "account/logout";
    }

    @GetMapping("/account/order/{id}")
    public String orderDetailAccount(@PathVariable("id") Long id,
                                     Model model,
                                     Authentication authentication){
        User user = getUSer(authentication);

        model.addAttribute("order",orderService.getOrderById(id));
        model.addAttribute("user", user);
        model.addAttribute("listOrderDetail", orderService.getListCartWithId(id));
        model.addAttribute("pageType","user");

        return "invoice";
    }

    @GetMapping("/order/cancel/{id}")
    public String handelCancelOrder(@PathVariable("id") Long id){
        Orders getOrder = orderService.getOrderById(id);
        Type typeCancel = typeService.getByID(5L);
        getOrder.setType(typeCancel);
        orderService.updateOrder(getOrder);

        return "redirect:/account/orders/detail/"+id;
    }


    //Handel Forget Password
    @GetMapping("/forget-password")
    public String indexForget(){

        return "account/forget";
    }
    @PostMapping("/forget-password/form")
    public String handelForgetForm(@RequestParam("email") String email){
        User user = userService.getUser(email);

        if (user != null){
            fogetPasswordService.sendOTPEmail(email);
            emailForgetPassword = email;
            return "account/otp";
        }

        return "error";
    }
    @GetMapping("/forget-password/otp")
    public String indexOTP(){
        if (emailForgetPassword != null){
            return "account/otp";
        }

        return "error";
    }
    @PostMapping("/forget-password/otp/form")
    public String handelOTP(@RequestParam("otp") String otp){
        boolean checkOTP = fogetPasswordService.checkOTP(emailForgetPassword, otp);
        if (checkOTP == true){
            return "redirect:/forget-password/change-password";
        }

        return "redirect:/forget-password/otp";
    }
    @GetMapping("/forget-password/change-password")
    public String indexChangePassword(){

        return "account/change-password";
    }
    @PostMapping("/forget-password/change-password/form")
    public String handelChangePassword(@RequestParam("password") String password){
        fogetPasswordService.setPassWord(emailForgetPassword, password);
        emailForgetPassword = null;
        return "redirect:/login";
    }

    @GetMapping("/account/invoice/{id}")
    public String handelInvoice(@PathVariable("id") Long id,
                                Model model,
                                Authentication authentication){
        User user = getUSer(authentication);

        model.addAttribute("order",orderService.getOrderById(id));
        model.addAttribute("user", user);
        model.addAttribute("listOrderDetail", orderService.getListCartWithId(id));
        model.addAttribute("pageType","user");

        return "invoice";
    }


    @GetMapping("/account/reward")
    public String handelReward(Model model, Authentication authentication){
        model.addAttribute("listTopSale", bookService.getListTopSale(4));
        model.addAttribute("user",getUSer(authentication));

        return "account/reward";
    }

    @GetMapping("/account/reward/check-in")
    public String handelCheckin(Authentication authentication) throws ParseException {
        User getUserLogin = getUSer(authentication);
        getUserLogin.addAttendancePoints(2);
        userService.saveUser(getUserLogin);

        return "redirect:/account/reward";
    }

    public static String generateRandomKey() {
        Random random = new Random();
        StringBuilder keyBuilder = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            keyBuilder.append(randomChar);
        }

        return keyBuilder.toString();
    }

    @GetMapping("/account/reward/gift")
    public String handelCGV(Authentication authentication, Model model){
        User getUserLogin = getUSer(authentication);
        Integer getTotalPoints = getUserLogin.getTotalPoints();

        if (getTotalPoints >= 62){
            List<Attendance> getListAttendance = attendanceService.getListWithUserID(getUserLogin.getUser_id());

            for (Attendance attendance : getListAttendance) {
                attendanceService.deleteByID(attendance.getId());
            }

            Integer handelScore = getTotalPoints - 60;
            getUserLogin.setPointsCGV(handelScore);
            userService.saveUser(getUserLogin);

            //Handel notify to mail
            String body = "We so happy because you reward your score LTech, LTech sent a gift to you is CGV Cinema gift code: " +
                    "\nKey: " + generateRandomKey()+
                    ".\nCGV movie box office website nationwide: https://www.cgv.vn/default/cinox/site/.";
            //mail
            String mailAddress = getUserLogin.getEmail();
            //send mail
            mailService.sendNewMail(mailAddress,"Gift LTech exchange successful", body);

            return "success";
        }

        return "error";
    }
}
