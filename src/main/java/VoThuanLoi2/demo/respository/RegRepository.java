package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Reg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegRepository extends JpaRepository<Reg, Long> {
    @Query("SELECT a FROM Reg a WHERE a.id_room = :roomId")
    List<Reg> findByRoomId(@Param("roomId") Long roomId);
}
