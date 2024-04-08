package VoThuanLoi2.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommunityController {

    @GetMapping("/community")
    public String index(){
        return "community/index";
    }

    @GetMapping("/community/page")
    public String handelDetailPage(){
        return "community/page";
    }

    @GetMapping("/chat")
    public String indexChat(){
        return "community/chat";
    }
}
