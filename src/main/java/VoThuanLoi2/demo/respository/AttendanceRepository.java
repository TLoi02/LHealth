package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query("SELECT a FROM Attendance a WHERE a.user.user_id = :userId")
    List<Attendance> findByUserId(@Param("userId") Integer userId);
}
