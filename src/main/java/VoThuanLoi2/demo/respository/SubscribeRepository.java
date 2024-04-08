package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    public Subscribe findByEmail(String email);
}
