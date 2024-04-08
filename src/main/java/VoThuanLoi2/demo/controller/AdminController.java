package VoThuanLoi2.demo.controller;

import VoThuanLoi2.demo.entity.*;
import VoThuanLoi2.demo.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private JobService jobService;
    @Autowired
    private ApplyService applyService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TypeService typeService;
    private final FileUpload fileUpload;
    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private MailService mailService;

    @GetMapping("/dashboard")
    public String index(Model model, Long idCategory){
        model.addAttribute("listOrdersComplete", orderService.getOrderWithNameType("Hoàn thành"));
        model.addAttribute("listOrdersCancel", orderService.getOrderWithNameType("Hủy"));
        model.addAttribute("listOrderDate", orderService.getListWithDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue()));

        return "admin/index";
    }

    @GetMapping("/book/category/{id}")
    public String handelIndex(@PathVariable("id") Long id, Model model){
        index(model, id);
        return "admin/index";
    }

    //Handel category
    @GetMapping("/category")
    public String indexCategory(Model model){
        model.addAttribute("listCategory", categoryService.getAllCategories());
        model.addAttribute("category", new Category());

        return "admin/category";
    }
    @PostMapping("/category/create")
    public String createCategory(@Valid Category newCategory,
                                 BindingResult result,
                                 Model model){
        if (result.hasErrors()){
            return "admin/category";
        }
        categoryService.saveCategory(newCategory);
        return "redirect:/admin/category";
    }
    @GetMapping("/category/update/{id}")
    public String editCategory(@PathVariable("id") Long id, Model model){
        Category category = categoryService.getCategoryById(id);
        if (category == null){
            return "not-found";
        }
        model.addAttribute("category", category);
        return "admin/update-category";
    }
    @PostMapping("/category/update/{id}")
    public String editCategory(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("category") Category updatedCategory,
                               BindingResult result,
                               Model model){
        if (result.hasErrors()){
            return "admin/update-category";
        }
        Category category = categoryService.getCategoryById(id);
        if (category == null){
            return "error";
        }
        category.setName(updatedCategory.getName());
        categoryService.saveCategory(category);
        return "redirect:/admin/category";
    }
    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id){
        categoryService.deleteCategory(id);
        return "redirect:/admin/category";
    }


    //Handel Book
    @GetMapping("/product")
    public String indexProduct(Model model){
        model.addAttribute("listProduct", bookService.getAll());
        model.addAttribute("book",new Book());
        model.addAttribute("categories",categoryService.getAllCategories());

        return "admin/product";
    }
    @PostMapping("/book/create")
    public String createBook(@Valid Book newBook,
                         @RequestParam MultipartFile imageProduct,
                         BindingResult result,
                         Model model) throws IOException {
        if (result.hasErrors()){
            model.addAttribute("categories",categoryService.getAllCategories());
            return "admin/product";
        }
        if (imageProduct != null && imageProduct.getSize() > 0){
            String imageURL = fileUpload.uploadFile(imageProduct);
            newBook.setImage(imageURL);
        }

        //Set date post
        newBook.setDatePost(new Date());

        bookService.add(newBook);
        return "redirect:/admin/product";
    }
    @GetMapping("/book/update/{id}")
    public String editBook(@PathVariable("id") Long id, Model model){
        Book book = bookService.getBookById(id);
        if (book == null){
            return "not-found";
        }
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("book", book);
        return "admin/update-product";
    }
    @PostMapping("/book/update/{id}")
    public String editBook(@PathVariable("id") Long id,
                             @RequestParam MultipartFile imageProduct,
                             @Valid @ModelAttribute("book") Book updatedBook,
                             BindingResult result,
                             Model model) throws IOException {
        if (result.hasErrors()){
            model.addAttribute("categories",categoryService.getAllCategories());
            return "admin/update-product";
        }

        Book book = bookService.getBookById(id);
        if (book == null){
            return "error";
        }
        if (imageProduct != null && imageProduct.getSize() > 0){
            String imageURL = fileUpload.uploadFile(imageProduct);
            book.setImage(imageURL);
        }

        //Set content update
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setPrice(updatedBook.getPrice());
        book.setIntro(updatedBook.getIntro());
        book.setContent(updatedBook.getContent());
        book.setYear(updatedBook.getYear());
        book.setCompany(updatedBook.getCompany());
        book.setSize(updatedBook.getSize());
        book.setQuantity(updatedBook.getQuantity());
        book.setCategory(updatedBook.getCategory());
        book.setDatePost(new Date());
        book.setPages(100);
        book.setType("CJ Group");
        book.setCalo(updatedBook.getCalo());

        bookService.updateBook(book);
        return "redirect:/admin/product";
    }
    @GetMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id){
        bookService.deleteBook(id);
        return "redirect:/admin/product";
    }


    //Handel blog
    @GetMapping("/blog")
    public String indexblog(Model model){
        model.addAttribute("listBlog", blogService.getAll());
        model.addAttribute("blog", new Blog());

        return "admin/blog";
    }
    @PostMapping("/blog/create")
    public String createBlog(@Valid Blog newBlog,
                         @RequestParam MultipartFile imageBlog,
                         BindingResult result,
                         Model model) throws IOException {
        if (result.hasErrors()){
            return "admin/blog";
        }

        if (imageBlog != null && imageBlog.getSize() > 0){
            String imageURL = fileUpload.uploadFile(imageBlog);
            newBlog.setImage(imageURL);
        }

        //Set date post is day create blog
        newBlog.setDatepost(LocalDate.now());

        blogService.add(newBlog);
        return "redirect:/admin/blog";
    }
    @GetMapping("/blog/update/{id}")
    public String editBlog(@PathVariable("id") Long id, Model model){
        Blog blog = blogService.getBlogById(id);
        if (blog == null){
            return "not-found";
        }
        model.addAttribute("blog", blog);
        return "admin/update-blog";
    }
    @PostMapping("/blog/update/{id}")
    public String editBlog(@PathVariable("id") Long id,
                           @RequestParam MultipartFile imageBlog,
                           @Valid @ModelAttribute("blog") Blog updateBlog,
                           BindingResult result,
                           Model model) throws IOException {
        if (result.hasErrors()){
            model.addAttribute("categories",categoryService.getAllCategories());
            return "admin/update-blog";
        }
        Blog blog = blogService.getBlogById(id);
        if (blog == null){
            return "error";
        }
        if (imageBlog != null && imageBlog.getSize() > 0){
            String imageURL = fileUpload.uploadFile(imageBlog);
            blog.setImage(imageURL);
        }
        blog.setTitle(updateBlog.getTitle());
        blog.setIntro(updateBlog.getIntro());
        blog.setContent(updateBlog.getContent());
        blogService.add(blog);
        return "redirect:/admin/blog";
    }
    @GetMapping("/blog/delete/{id}")
    public String deleteBlog(@PathVariable("id") Long id){
        blogService.deleteBlog(id);
        return "redirect:/admin/blog";
    }


    //Handel job
    @GetMapping("/job")
    public String indexJob(Model model){
        model.addAttribute("listJob", jobService.getAll());
        model.addAttribute("listApply", applyService.getAll());
        model.addAttribute("job", new Job());

        return "admin/job";
    }
    @PostMapping("/job/create")
    public String createJob(@Valid Job newJob,
                                 BindingResult result,
                                 Model model){
        if (result.hasErrors()){
            return "admin/job";
        }
        jobService.saveJob(newJob);
        return "redirect:/admin/job";
    }
    @GetMapping("/job/update/{id}")
    public String editJob(@PathVariable("id") Long id, Model model){
        Job job = jobService.getJobById(id);
        if (job == null){
            return "not-found";
        }
        model.addAttribute("job", job);
        return "admin/update-job";
    }
    @PostMapping("/job/update/{id}")
    public String editJob(@PathVariable("id") Long id, @Valid @ModelAttribute("job") Job updateJob, BindingResult result, Model model){
        if (result.hasErrors()){
            return "admin/update-job";
        }
        Job job = jobService.getJobById(id);
        if (job == null){
            return "error";
        }
        job.setTitle(updateJob.getTitle());
        job.setContent(updateJob.getContent());
        jobService.saveJob(job);
        return "redirect:/admin/job";
    }
    @GetMapping("/job/delete/{id}")
    public String deleteJob(@PathVariable("id") Long id){
        jobService.deleteJob(id);
        return "redirect:/admin/job";
    }


    //Handel apply
    @GetMapping("/apply/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadImage(@PathVariable("id") Long id) {
        try {
            String getURL = applyService.findById(id).getImage();
            URL url = new URL(getURL);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();

            byte[] bytes = inputStream.readAllBytes();
            ByteArrayResource resource = new ByteArrayResource(bytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "cv.jpg");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    //Handel orders
    @GetMapping("/orders")
    public String handelOrdersIndex(Model model){
        model.addAttribute("listNewOrder", orderService.getOrderWithNameType("Chưa xác nhận"));
        model.addAttribute("listOrder", orderService.getListAdmin());

        return "admin/orders";
    }
    @GetMapping("/orders/update/{id}")
    public String handelUpdateOrder(@PathVariable("id") Long id, Model model){
        model.addAttribute("order",orderService.getOrderById(id));
        model.addAttribute("listType",typeService.getAll());

        return "admin/update-order";
    }
    @PostMapping("/orders/update/{id}")
    public String handelUpdate(@PathVariable("id") Long id, @ModelAttribute("order") Orders updateOrder){
        //Get order
        Orders order = orderService.getOrderById(id);

        order.setType(updateOrder.getType());
        orderService.updateOrder(order);

        return "redirect:/admin/dashboard";
    }

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

    @GetMapping("/user")
    public String indexUser(Model model){
        model.addAttribute("listSubscribe", subscribeService.getAll());
        model.addAttribute("listVoucher", voucherService.getAll());

        return "admin/user";
    }


    //Sales handel
    @GetMapping("/sale")
    public String indexSales(Model model){
        model.addAttribute("listCategory", categoryService.getAllCategories());
        model.addAttribute("listBook", bookService.getAll());

        return "admin/sales";
    }
    @PostMapping("/sale/create")
    public String createSale(@RequestParam("value") Double value,
                             @RequestParam("category") Long categoryId,
                             Model model){
        bookService.setSale(categoryId, value);
        return "redirect:/admin/sale";
    }
    @GetMapping("/sale/delete/{id}")
    public String deleteSale(@PathVariable("id") Long id){
        Boolean isSale = bookService.isSale(id);

        if (isSale){
            bookService.deleteSale(id);
            return "redirect:/admin/sale";
        }

        return "redirect:/admin/sale";
    }


    //Voucher handel
    @GetMapping("/voucher")
    public String indexVoucher(Model model){
        model.addAttribute("listVoucher", voucherService.getAll());
        model.addAttribute("listCategory", categoryService.getAllCategories());

        return "admin/voucher";
    }
    @PostMapping("/voucher/create")
    public String createVoucher(@RequestParam("code") String code,
                                 @RequestParam("value") Double value,
                                 @RequestParam("category") Long categoryId,
                                 Model model){
        Category getCategory = categoryService.getCategoryById(categoryId);

        Voucher v = new Voucher();
        v.setVoucherCode(code);
        v.setDiscountAmount(value);
        v.setCategory(getCategory);
        voucherService.createVoucher(v);

        return "redirect:/admin/voucher";
    }
    @GetMapping("/voucher/delete/{id}")
    public String deleteVoucher(@PathVariable("id") Long id){
        voucherService.deleteVoucher(id);
        return "redirect:/admin/voucher";
    }
    @GetMapping("/voucher/sent/{id}")
    public String sentVoucher(@PathVariable("id") Long id){
        Voucher findVoucher = voucherService.getVoucherByID(id);

        if (findVoucher.getIsSent() == null){
            List<Subscribe> listSub = subscribeService.getAll();

            for (Subscribe subscribe : listSub) {
                String body = "We sincerely appreciate your following our website, and as a token of our gratitude, we are pleased to send you a shopping voucher with the following content and usage details: " +
                        "\n\n- Voucher code: "+findVoucher.getVoucherCode()+"."+
                        "\n- Voucher value: "+findVoucher.getDiscountAmount()+"."+
                        "\n- Voucher use for category name: "+findVoucher.getCategory().getName()+"."+
                        "\nWe are delighted to send you this voucher and hope you can find a product you like on our website!";
                //mail Customer
                String mailAddress = subscribe.getEmail();
                //send mail
                mailService.sendNewMail(mailAddress,"Ltech is giving you a new voucher", body);
            }

            findVoucher.setIsSent(true);
            //update voucher type
            voucherService.createVoucher(findVoucher);
            return "redirect:/admin/user";
        }

        return "redirect:/admin/user";
    }

    @GetMapping("/invoice/{id}")
    public String handelDetail(@PathVariable("id") Long id,
                               Model model,
                               Authentication authentication){
        User user = getUSer(authentication);

        model.addAttribute("order",orderService.getOrderById(id));
        model.addAttribute("user", user);
        model.addAttribute("listOrderDetail", orderService.getListCartWithId(id));
        model.addAttribute("pageType","admin");

        return "invoice";
    }
}
