package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Book;
import VoThuanLoi2.demo.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookRespository extends JpaRepository<Book, Long>{
    List<Book> findByCategoryId(Long categoryId);
    Book findByTitle(String title);

    @Query("SELECT b FROM Book b WHERE b.id IN (SELECT c.book.id FROM Comment c WHERE c.user.id = :userid)")
    public List<Book> findBooksByUserId(@Param("userid") Integer userId);

    @Query("SELECT b FROM Book b WHERE b.id IN (SELECT c.book.id FROM Favorite c WHERE c.user.id = :userid)")
    public List<Book> findBookFavoriteByUserId(@Param("userid") Integer userId);

    @Query("SELECT b FROM Book b ORDER BY b.countSell DESC LIMIT :value")
    public List<Book> findBestSeller(@Param("value") Integer value);

    @Query("SELECT b FROM Book b ORDER BY b.datePost DESC LIMIT :value")
    public List<Book> findNewProduct(@Param("value") Integer value);

    @Query("SELECT b FROM Book b ORDER BY b.discountPercentage DESC LIMIT :value")
    public List<Book> findBookTopSale(@Param("value") Integer value);

    @Query("SELECT b FROM Book b ORDER BY b.calo ASC LIMIT :value")
    public List<Book> findBookWithCalo(@Param("value") Integer value);
}
