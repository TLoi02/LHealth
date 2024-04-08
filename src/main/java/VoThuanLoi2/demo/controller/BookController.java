package VoThuanLoi2.demo.controller;

import VoThuanLoi2.demo.entity.Book;
import VoThuanLoi2.demo.entity.Category;
import VoThuanLoi2.demo.entity.User;
import VoThuanLoi2.demo.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FavoriteService favoriteService;
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

    public String getPageWithCategory(Model model, int currentPage, Long categoryID){
        Page<Book> page = null;

        //Handel list with id category
        if (categoryID.equals(0L)){
            //Get All book
            page = bookService.findPage(currentPage);

            model.addAttribute("titlePage","All Product");
            model.addAttribute("typePaging","all");
        }
        else{
            //Get book with category
            page = bookService.findPageByIdCategory(categoryID,currentPage);

            model.addAttribute("titlePage",categoryService.getCategoryById(categoryID).getName());

            model.addAttribute("idPaging",categoryService.getCategoryById(categoryID).getId());
            model.addAttribute("typePaging","one");
        }

        int totalPages = page.getTotalPages();
        model.addAttribute("totalPages", totalPages);

        //Handel slider categories
        List<Category> listCategory = categoryService.getAllCategories();
        List<List<Category>> handelSubList = new ArrayList<>();
        for (int i = 0; i < listCategory.size(); i += 3) {
            int end = Math.min(i + 3, listCategory.size());
            List<Category> subList = listCategory.subList(i, end);
            handelSubList.add(subList);
        }
        model.addAttribute("handelSubList", handelSubList);

        model.addAttribute("listBook", page.getContent());
        //Type check have or doesn't product
        model.addAttribute("type","have");

        return "book/index";
    }

    @GetMapping("/list/{pageNumber}")
    public String getOnPage(Model model, @PathVariable("pageNumber") int currentPage){
        Page<Book> page = bookService.findPage(currentPage);
        int totalPages = page.getTotalPages();

        model.addAttribute("titlePage","All Product");
        model.addAttribute("typePaging","all");

        model.addAttribute("totalPages", totalPages);

        //Handel slider categories
       /*
        List<Category> listCategory = categoryService.getAllCategories();
        List<List<Category>> handelSubList = new ArrayList<>();
        for (int i = 0; i < listCategory.size(); i += 3) {
            int end = Math.min(i + 3, listCategory.size());
            List<Category> subList = listCategory.subList(i, end);
            handelSubList.add(subList);
        }
        model.addAttribute("handelSubList", handelSubList);
        */

        model.addAttribute("listBook", page.getContent());
        //Type check have or doesn't product
        model.addAttribute("type","have");

        return "book/index";

    }
    @GetMapping("/list")
    public String getAllPages(Model model){
        return getOnPage(model, 1);
    }

    @GetMapping("/list/category/{id}")
    public String handelIndex(Model model, @PathVariable("id") Long id){
        return getPageWithCategory(model, 1, id);
    }

    @GetMapping("/list/category/{id}/{page}")
    public String handelIndexWithPage(Model model, @PathVariable("id") Long id, @PathVariable("page") int pageNumber){
        return getPageWithCategory(model, pageNumber, id);
    }

    @GetMapping("/detail/{id}")
    public String handelDetailPage(Model model, @PathVariable("id") Long id, Authentication authentication){
        //props: book, book same category
        Book findBook = bookService.getBookById(id);
        if (findBook == null){
            return "error";
        }

        //Find book same category to handel
        List<Book> filterList = bookService.getListBookByCategory(findBook.getCategory().getId(), findBook.getId());

        //Handel limit of same book is 4 book
        if (filterList.size() > 4)
            filterList = filterList.subList(0, 4);

        model.addAttribute("book", findBook);
        model.addAttribute("listBookSame", filterList);
        model.addAttribute("listComment", commentService.getCommentByIdBook(id));
        model.addAttribute("isFavorite", favoriteService.isFavorite(getUSer(authentication).getUser_id(),id));

        return "book/detail";
    }

    @PostMapping("/search")
    public String searchPage (@RequestParam("keyword") String keyword, Model model){
        Page<Book> page = bookService.findPage(1);
        int totalPages = page.getTotalPages();

        //Create empty list to for loop find book contain with keyword
        List<Book> result = new ArrayList<>();
        bookService.getAll().forEach(c -> {
            if ((c.getTitle().toLowerCase()).contains(keyword.toLowerCase()))
                result.add(c);
        });

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("listBook", result);

        //Type check have or doesn't product
        String type = (result.size() != 0) ? "have" : "not";
        model.addAttribute("type",type);

        //Handel slider categories
        List<Category> listCategory = categoryService.getAllCategories();
        List<List<Category>> handelSubList = new ArrayList<>();
        for (int i = 0; i < listCategory.size(); i += 3) {
            int end = Math.min(i + 3, listCategory.size());
            List<Category> subList = listCategory.subList(i, end);
            handelSubList.add(subList);
        }
        model.addAttribute("handelSubList", handelSubList);

        return "book/index";
    }
}
