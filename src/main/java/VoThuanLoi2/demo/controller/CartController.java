package VoThuanLoi2.demo.controller;

import VoThuanLoi2.demo.config.VNPayConfig;
import VoThuanLoi2.demo.entity.Book;
import VoThuanLoi2.demo.entity.User;
import VoThuanLoi2.demo.entity.Voucher;
import VoThuanLoi2.demo.models.CartItem;
import VoThuanLoi2.demo.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private BookService productService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileUpload fileUpload;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private BookService bookService;

    //Handel voucher and shiping in cart;
    private static Long saleValue = 0L;


    @GetMapping("")
    public String index(Model model, HttpSession session) {
        //Get list item in cart
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("listCart", cartItems);

        //Handel empty or not empty cart
        String type = (cartItems.size() > 0) ? "have" : "not";
        model.addAttribute("type",type);

        //Set and handel count book in cart;
        session.setAttribute("count", cartItems.size());
        model.addAttribute("count",cartItems.size());

        //Calc total price and handel to view
        long totalPrice = cartItems.stream()
                .mapToLong(cartItem -> (long) (cartItem.getPrice() * cartItem.getQuantity()))
                .sum();
        model.addAttribute("totalPrice", totalPrice);

        //Set date shipping
        model.addAttribute("dateOrder", LocalDate.now());
        model.addAttribute("dateFuture", LocalDate.now().plusDays(4));

        return "cart/index";
    }
    //Button add to cart on list page
    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Long productId) {
        //Find book click with id
        Book book = productService.getBookById(productId);
        if (book != null) {
            cartService.addToCart(book,1);
        }
        return "redirect:/cart";
    }
    //Button add to cart on detail page
    @PostMapping("/add/{id}")
    public String addToCartDetail(@PathVariable("id") Long productId,
                                  @RequestParam("quantity") int quantity
                                  ) {
        //Find book click with id
        Book book = productService.getBookById(productId);
        if (book != null) {
            cartService.addToCart(book,quantity);
        }
        return "redirect:/cart";
    }

    @PostMapping("/update/{id}")
    public String updateCartItem(@PathVariable("id") Long productId,
                                 @RequestParam("quantity") int quantity) {
        cartService.updateCartItem(productId, quantity);
        return "redirect:/cart";
    }
    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable("productId") Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
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

    public Long handelTotalPrice(){
        List<CartItem> cartItems = cartService.getCartItems();
        long totalPrice = cartItems.stream()
                .mapToLong(cartItem -> (long) (cartItem.getPrice() * cartItem.getQuantity()))
                .sum();
        totalPrice -= saleValue;
        //totalPrice += shiping;
        return totalPrice;
    }

    @GetMapping("/checkout")
    public String Order(Model model, Authentication authentication, Long voucherApply) {
        Long handelVoucher = (voucherApply == null) ? 0 : voucherApply;
        Long handelShipping = 0L;

        //Get user login
        User user = getUSer(authentication);

        //Get list item in cart
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("listCart", cartItems);

        model.addAttribute("totalPrice", handelTotalPrice());

        //Set user info
        model.addAttribute("user",user);

        //Set handel voucher
        model.addAttribute("voucherValue", handelVoucher);

        return "/cart/checkout";
    }

    @PostMapping("/checkout/apply-voucher")
    public String applyVoucher(@RequestParam("voucher") String voucher,
                               Model model,
                               Authentication authentication){
        Voucher getVoucher = voucherService.getByVoucherCode(voucher);

        if (getVoucher != null){
            Double percentVoucher = getVoucher.getDiscountAmount();
            Long idCategory = getVoucher.getCategory().getId();
            Long calcValue = 0L;
            List<CartItem> cartItems = cartService.getCartItems();

            for (CartItem item : cartItems) {
                Book getBook = bookService.getBookById(item.getId());
                if (getBook.getCategory().getId() == idCategory){
                    calcValue += (long) ((item.getAmount() * percentVoucher) / 100);
                }
            }

            saleValue = (calcValue == 0L) ? 0L : calcValue;

            return Order(model, authentication, (calcValue == 0L) ? null : calcValue);
        }

        return Order(model, authentication, null);
    }

    public String handelPay(Long price) throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = price*100;
        String bankCode = "NCB";

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        return paymentUrl;
    }

    public void saveUserCheckout(Authentication authentication,
                                 String name,
                                 String phone,
                                 String address,
                                 String province,
                                 String district,
                                 String ward){
        User user = getUSer(authentication);
        user.setName(name);
        user.setPhone(phone);
        user.setAddress(address);
        user.setCity(province);
        user.setDistrict(district);
        user.setWard(ward);
        userService.saveUser(user);
    }

    @PostMapping("/checkout")
    public String checkout(Authentication authentication,
                           @RequestParam("province") String province,
                           @RequestParam("district") String district,
                           @RequestParam("ward") String ward,
                           @RequestParam("payment") String payment,
                           @ModelAttribute("user") User getUser,
                           Model model) throws UnsupportedEncodingException {
        User user = getUSer(authentication);

        if (payment.equals("cash")){
            cartService.orderCart(user, null, null, false);

            saveUserCheckout(
                    authentication,
                    getUser.getName(),
                    getUser.getPhone(),
                    getUser.getAddress(),
                    province,
                    district,
                    ward
            );

            return "success";
        }
        else if(payment.equals("vnpay")){
            String url = handelPay(handelTotalPrice());
            saveUserCheckout(
                    authentication,
                    getUser.getName(),
                    getUser.getPhone(),
                    getUser.getAddress(),
                    province,
                    district,
                    ward
            );
            return "redirect:"+url;
        }
        else if(payment.equals("qr"))
        {
            String imgURL = "https://img.vietqr.io/image/970415-101875984875-qr_only.png?amount="+handelTotalPrice().toString()+"&accountName=Vo Thuan Loi";
            model.addAttribute("url", imgURL);
            model.addAttribute("time", LocalDate.now());

            saveUserCheckout(
                    authentication,
                    getUser.getName(),
                    getUser.getPhone(),
                    getUser.getAddress(),
                    province,
                    district,
                    ward
            );

            return "cart/checkout-qr";
        }
        return "";
    }

    @PostMapping("/checkout/qr-code")
    public String checkoutWithQRCode(Authentication authentication,
                                     @RequestParam MultipartFile imageBill,
                                     @RequestParam("content") String content)throws IOException {
        String imgBill = null;
        if (imageBill != null && imageBill.getSize() > 0)
            imgBill = fileUpload.uploadFile(imageBill);
        User getUserLogin = getUSer(authentication);

        cartService.orderCart(getUserLogin, imgBill, content, true);

        return "success";
    }

    @GetMapping("/vnpay/success")
    public String checkoutVNPaySuccess(Authentication authentication){
        User getUserLogin = getUSer(authentication);
        cartService.orderCart(getUserLogin, "VNPAY", "VNPAY", true);
        return "success";
    }
}
