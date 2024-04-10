package VoThuanLoi2.demo.controller;

import VoThuanLoi2.demo.entity.Favorite;
import VoThuanLoi2.demo.entity.User;
import VoThuanLoi2.demo.services.BookService;
import VoThuanLoi2.demo.services.FavoriteService;
import VoThuanLoi2.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/favorite")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
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

    @GetMapping("/add/{id}")
    public String addFavorite(@PathVariable("id") Long idBook, Authentication authentication){
        //Get User is login
        User userLogin = getUSer(authentication);

        //Check this favorite have or not
        Boolean isFavorite = favoriteService.isFavorite(userLogin.getUser_id(),idBook);
        if (isFavorite == false){
            Favorite favorite = new Favorite();
            favorite.setBook(bookService.getBookById(idBook));
            favorite.setUser(userLogin);
            favoriteService.save(favorite);
        }

        return "redirect:/book/detail/" + idBook;
    }

    @GetMapping("/delete/{id}")
    public String deleteFavorite(@PathVariable("id") Long idBook, Authentication authentication){
        //Get User is login
        User userLogin = getUSer(authentication);

        //Check this favorite have or not
        Boolean isFavorite = favoriteService.isFavorite(userLogin.getUser_id(),idBook);
        if (isFavorite == true){
            Favorite getFavorite = favoriteService.getFavoriteQuery(getUSer(authentication).getUser_id(), idBook);
            favoriteService.deleteFavorite(getFavorite);
        }

        return "redirect:/book/detail/" + idBook;
    }
}
