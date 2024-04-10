package VoThuanLoi2.demo.controller;

import VoThuanLoi2.demo.entity.Comment;
import VoThuanLoi2.demo.entity.User;
import VoThuanLoi2.demo.services.BookService;
import VoThuanLoi2.demo.services.CommentService;
import VoThuanLoi2.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

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

    //Handel post comment
    @PostMapping("/post")
    public String postComment(@RequestParam Long bookId, @RequestParam String content, Authentication authentication) {
        Comment newComment = new Comment();
        newComment.setContent(content);
        newComment.setUser(getUSer(authentication));
        newComment.setBook(bookService.getBookById(bookId));
        newComment.setComment__date(new Date());
        commentService.save(newComment);

        return "redirect:/book/detail/" + bookId;
    }
}
