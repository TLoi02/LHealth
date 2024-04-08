package VoThuanLoi2.demo.respository;

import VoThuanLoi2.demo.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    public Voucher findByVoucherCode(String voucherCode);
}
