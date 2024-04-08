package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Appointment;
import VoThuanLoi2.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.user_id = :userId")
    List<Appointment> findByUserId(@Param("userId") Long userId);
}
