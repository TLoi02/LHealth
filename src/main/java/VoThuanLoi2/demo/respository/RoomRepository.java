package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    public Room findByName(String name);
    public Room findByPassword(String password);
}
