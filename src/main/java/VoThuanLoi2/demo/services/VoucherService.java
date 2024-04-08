package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.Voucher;
import VoThuanLoi2.demo.respository.IBookRespository;
import VoThuanLoi2.demo.respository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;

    public List<Voucher> getAll(){
        return voucherRepository.findAll();
    }

    public void createVoucher(Voucher v){
        voucherRepository.save(v);
    }

    public void deleteVoucher(Long id){
        voucherRepository.deleteById(id);
    }

    public Voucher getVoucherByID(Long id){
        return voucherRepository.findById(id).orElse(null);
    }

    public Voucher getByVoucherCode(String voucher){
        return voucherRepository.findByVoucherCode(voucher);
    }
}
