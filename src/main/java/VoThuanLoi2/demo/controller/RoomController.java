package VoThuanLoi2.demo.controller;

import VoThuanLoi2.demo.entity.Room;
import VoThuanLoi2.demo.services.FileUpload;
import VoThuanLoi2.demo.services.MailService;
import VoThuanLoi2.demo.services.RegService;
import VoThuanLoi2.demo.services.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Random;

@Controller
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private MailService mailService;
    private final FileUpload fileUpload;
    @Autowired
    private RegService regService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public RoomController(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    @GetMapping("/join-partner")
    public String index(Model model){
        model.addAttribute("room",new Room());

        return "partner/index";
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

    @PostMapping("/join-partner")
    public String submitForm(@Valid Room newRoom,
                             @RequestParam MultipartFile imageLogo,
                             Model model) throws IOException {
        Room check = roomService.findByName(newRoom.getName());

        if (check == null){
            if (imageLogo != null && imageLogo.getSize() > 0){
                String imageURL = fileUpload.uploadFile(imageLogo);
                newRoom.setLogo(imageURL);
            }
            String randKey = generateRandomKey();

            newRoom.setPassword(randKey);

            roomService.save(newRoom);

            //Handel notify to mail
            String body = "Account login to your Dashboard: " +
                    ".\nKey: " + randKey +
                    ".\nLink to your Dashboard: http://localhost:8080/login-partner.";
            //mail
            String mailAddress = newRoom.getEmail();
            //send mail
            mailService.sendNewMail(mailAddress,"Registration Successful", body);

            return "success";
        }

        return "success";
    }

    @GetMapping("/login-partner")
    public String handelLogin(Model model){
        return "partner/login";
    }
    @PostMapping("/login-partner")
    public String postLogin(@RequestParam("keyword") String keyword){
        Room checkCenter = roomService.findByPassWord(keyword);

        if (checkCenter != null){
            Long id = checkCenter.getId();
            return "redirect:/dashboard-partner/"+id;
        }

        return "error";
    }

    @GetMapping("/dashboard-partner/{id}")
    public String indexDashboard(   @PathVariable("id") Long id,
                                    Model model){
        model.addAttribute("listReg", regService.findWithRoomId(id));

        return "partner/dashboard";
    }
}
