package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class FogetPasswordService {
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;

    public String generateOTP() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOTPEmail(String email) {
        String otp = generateOTP();

        //Save otp to email
        User getUser = userService.getUser(email);
        getUser.setOtp(otp);
        userService.saveUser(getUser);

        //Handel notify to mail
        String body = "The code below is the otp for you to change your password " +
                "\n"+otp;
        //mail Customer
        String mailAddress = email;
        //send mail
        mailService.sendNewMail(mailAddress,"LTech OTP Change Password", body);
    }

    public boolean checkOTP(String email, String otpInput) {
        User getUser = userService.getUser(email);
        String otp = getUser.getOtp();

        if (otp != null){
            if (otp.equals(otpInput)){
                getUser.setOtp(null);
                userService.saveUser(getUser);
                return true;
            }
            return false;
        }
        return false;
    }

    public void setPassWord(String email, String passWord){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(passWord);
        User getUser = userService.getUser(email);
        getUser.setPassword(encodedPassword);
        userService.saveUser(getUser);
    }
}
