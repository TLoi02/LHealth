package VoThuanLoi2.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name="book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @NotEmpty(message = "Khong duoc de trong ten sach")
    @Size(max = 50, min =1, message = "Chuoi khong hop le")
    private String title;

    @Column(name = "author")
    @NotEmpty(message = "Khong duoc de trong ten tac gia")
    private String author;

    @Column(name = "price")
    @NotNull(message = "Khong duoc de trong gia tien")
    private Double price;

    @Column(name ="image")
    private String image;

    @Column(name = "intro", columnDefinition = "TEXT")
    @NotNull(message = "Vui lòng nhập mô tả ngắn")
    private String intro;

    @Column(name = "content", columnDefinition = "TEXT")
    @NotNull(message = "Vui lòng nhập nội dung")
    private String content;

    @Column(name = "year")
    @NotNull(message = "Vui lòng nhập năm suất bản")
    private Integer year;

    @Column(name = "company")
    @NotNull(message = "Vui lòng nhập nhà suất bản")
    private String company;

    @Column(name = "size")
    @NotNull(message = "Vui lòng nhập kích thước")
    private String size;

    @Column(name = "quantity")
    @NotNull(message = "Vui lòng nhập số lượng sách trong kho")
    private Integer quantity;

    @Column(name = "type")
    @NotNull(message = "Vui lòng nhập loại bìa")
    private String type;

    @Column(name = "pages")
    @NotNull(message = "Vui lòng nhập số trang")
    private Integer pages;

    @Column
    private Date datePost;

    @Column
    private Integer countSell;

    @Column
    private Integer calo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Favorite> favorites = new HashSet<>();

    @Column
    private Double discountPercentage;

    public Double getPriceSale(){
        Double discountAmount = (this.price * this.discountPercentage) / 100;
        return this.price - discountAmount;
    }
}
