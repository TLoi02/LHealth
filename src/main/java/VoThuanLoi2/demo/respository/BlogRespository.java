package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRespository extends JpaRepository<Blog, Long> {
    @Query("SELECT b FROM Blog b ORDER BY b.datepost DESC LIMIT :value")
    List<Blog> findLatestBlogs(@Param("value") Integer value);
}
