package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f WHERE f.book.id = :book_id AND f.user.id = :user_id")
    public Favorite findFavoriteByBookIdAndUserId(@Param("book_id") Long book_id, @Param("user_id") Integer user_id);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :user_id")
    public List<Favorite> findFavoriteByUserId(@Param("user_id") Integer user_id);
}
