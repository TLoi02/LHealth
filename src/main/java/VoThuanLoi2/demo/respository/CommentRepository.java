package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    public List<Comment> findByBookId(Long bookId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :user_id")
    public List<Comment> findByUserId(@Param("user_id") Integer user_id);
}
