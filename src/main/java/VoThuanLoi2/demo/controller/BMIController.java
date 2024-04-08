package VoThuanLoi2.demo.controller;

import VoThuanLoi2.demo.dto.ChatGPTRequest;
import VoThuanLoi2.demo.dto.ChatGptResponse;
import VoThuanLoi2.demo.entity.Appointment;
import VoThuanLoi2.demo.entity.Book;
import VoThuanLoi2.demo.entity.Reg;
import VoThuanLoi2.demo.entity.User;
import VoThuanLoi2.demo.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class BMIController {
    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private CartService cartService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private RegService regService;
    @Autowired
    private MailService mailService;
    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;

    @Autowired
    private RestTemplate template;

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

    public static double calculateBMI(double weightKg, double heightCm) {
        // change value from cm to m
        double heightM = heightCm / 100.0;

        double bmi = weightKg / (heightM * heightM);
        bmi = Math.round(bmi * 100.0) / 100.0;
        return bmi;
    }

    @GetMapping("/calc-bmi")
    public String indexCalc(){
        return "bmi/calc";
    }
    @PostMapping("/calc-bmi")
    public String calculateBMI(@RequestParam("weight") double weight,
                               @RequestParam("height") double height,
                               @RequestParam("age") double age,
                               @RequestParam("job") String job,
                               @RequestParam("anamnesis") String anamnesis,
                               @RequestParam("treated") String treated,
                               Authentication authentication) {
        User getUserLogin = getUSer(authentication);
        double bmi = calculateBMI(weight, height);

        getUserLogin.setWeight(weight);
        getUserLogin.setHeight(height);
        getUserLogin.setBmi(bmi);
        getUserLogin.setAge(age);
        getUserLogin.setJob(job);
        getUserLogin.setAnamnesis((anamnesis.isBlank()) ? "Không có" : anamnesis);
        getUserLogin.setTreated((treated.isBlank()) ? "Không có" : treated);
        userService.saveUser(getUserLogin);

        if (anamnesis.isBlank())
            return "error";

        return "redirect:/health";
    }

    @GetMapping("/health")
    public String indexHealth(Model modelTemplate,
                              Authentication authentication){
        User getUser = getUSer(authentication);
        Double bmi = getUser.getBmi();

        //handel create API query
        String prompt = "Chuẩn đoán sức khỏe cho người có chiều cao "+getUser.getHeight()+" nặng "+getUser.getWeight()+", chỉ số BMI "+bmi+", "+getUser.getAge()+" tuổi nghề nghiệp "+getUser.getJob()+" có tiền sử bệnh "+getUser.getAnamnesis()+" và hiện tại đang điều trị bệnh "+getUser.getTreated();
        ChatGPTRequest request=new ChatGPTRequest(model, prompt);
        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        String result = chatGptResponse.getChoices().get(0).getMessage().getContent();

        modelTemplate.addAttribute("bmi", bmi);
        modelTemplate.addAttribute("result", result);

        String type = null;
        if (bmi < 18.5)
            type = "Underweight";
        else if (bmi >= 18.5 && bmi <= 25)
            type = "Normal";
        else if (bmi > 25)
            type = "Overweight";

        modelTemplate.addAttribute("type", type);

        return "bmi/result";
    }

    @GetMapping("/doctor")
    public String indexDoctor(){
        return "bmi/doctor";
    }
    @PostMapping("/doctor")
    public String handelPostDoctor(@RequestParam("date") String date,
                                   Authentication authentication) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = dateFormat.parse(date);
        User getUserLogin = getUSer(authentication);

        Appointment a = new Appointment();
        a.setUser_id(Long.valueOf(getUserLogin.getUser_id()));
        a.setIsCheck(false);
        a.setDate_appointment(parsedDate);
        appointmentService.save(a);

        String body = "We will get in touch with you soon for confirmation.";
        String mailAddress = getUserLogin.getEmail();
        mailService.sendNewMail(mailAddress,"Appointment booking successful", body);

        return "redirect:/account";
    }


    @GetMapping("/menu")
    public String handelCreateMenu(Model modelTemplate, Authentication authentication){
        //handel create API query
        User getUserLogin = getUSer(authentication);
        String prompt = "Thực đơn cho người có chỉ số BMI "+getUserLogin.getBmi();

        ChatGPTRequest request=new ChatGPTRequest(model, prompt);
        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        String result = chatGptResponse.getChoices().get(0).getMessage().getContent();

        modelTemplate.addAttribute("result", result);
        modelTemplate.addAttribute("listProduct", bookService.getListCalo(4));
        return "bmi/menu";
    }

    @GetMapping("/order/all-bmi")
    public String handelOrderAll(){
        List<Book> listProduct = bookService.getListCalo(4);

        for (Book book : listProduct)
        {
            cartService.addToCart(book,1);
        }

        return "redirect:/cart";
    }

    @GetMapping("/appointment")
    public String indexAppointment(Model model, Authentication authentication){
        User getUserLogin = getUSer(authentication);
        model.addAttribute("listAppointment", appointmentService.findListWithUserId(Long.valueOf(getUserLogin.getUser_id())));

        return "bmi/appointment";
    }

    @GetMapping("/appointment/dashboard")
    public String indexDashboard(Model model){
        model.addAttribute("listAppointment", appointmentService.getAll());
        return "bmi/appointment-dashboard";
    }
    @GetMapping("/appointment/dashboard/accept/{id}")
    public String handelUpdateDashboard(@PathVariable("id") Long id){
        Appointment getAppointment = appointmentService.findById(id);
        if (getAppointment != null){
            getAppointment.setIsCheck(true);
            appointmentService.save(getAppointment);
        }
        return "redirect:/appointment/dashboard";
    }

    @GetMapping("/workout")
    public String handelCreateWorkout(Model modelTemplate, Authentication authentication){
        //handel create API query
        User getUserLogin = getUSer(authentication);
        String prompt = "Lịch tập cho người có chỉ số BMI "+getUserLogin.getBmi();

        ChatGPTRequest request=new ChatGPTRequest(model, prompt);
        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        String result = chatGptResponse.getChoices().get(0).getMessage().getContent();

        modelTemplate.addAttribute("result", result);
        modelTemplate.addAttribute("listRoom", roomService.getAll());

        return "bmi/workout";
    }

    @GetMapping("/workout/register/{id}")
    public String registerWorkout(@PathVariable("id") Long id, Model model){
        model.addAttribute("id", id);

        return "bmi/register-workout";
    }
    @PostMapping("/workout/register/{id}")
    public String processRegistration(
            @PathVariable("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("session") String session,
            Model model,
            Authentication authentication
    ) {
        User getUserLogin = getUSer(authentication);

        Reg r = new Reg();
        r.setName(name);
        r.setPhone(phone);
        r.setEmail(email);
        r.setSession_workout(session);
        r.setId_room(id);
        regService.saveReg(r);

        String body = "We will get in touch with you soon for confirmation.";
        String mailAddress = getUserLogin.getEmail();
        mailService.sendNewMail(mailAddress,"Register gym room successful", body);

        return "redirect:/";
    }
}
