package VoThuanLoi2.demo.controller;

import VoThuanLoi2.demo.entity.User;
import VoThuanLoi2.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SocialController {
    @Autowired
    private UserService userService;

    @GetMapping("/login-social")
    public String handelIndex(Authentication authentication, Model model){
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String githubLogin = oauth2User.getAttribute("login");
        String userName = null;
        String email = null;
        String name = null;

        if (githubLogin != null) {
            model.addAttribute("type", "github");
            userName = oauth2User.getAttribute("login");
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");

            //handel save to database
            User checkUser = userService.getUser(userName);
            if (checkUser == null){
                User user = new User();
                user.setEmail("github");
                user.setUsername(userName);
                user.setName(name);
                userService.addUserWithRole(user,"user");
            }
        }
        else{
            model.addAttribute("type", "google");
            userName = oauth2User.getAttribute("sub");
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");

            User checkUser = userService.getUser(email);
            if (checkUser == null){
                User user = new User();
                user.setEmail(email);
                user.setUsername(userName);
                user.setName(name);
                userService.addUserWithRole(user,"user");
            }
        }

        return "redirect:/";
    }
}
