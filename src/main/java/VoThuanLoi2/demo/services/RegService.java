package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.Reg;
import VoThuanLoi2.demo.respository.RegRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegService {
    @Autowired
    private RegRepository regRepository;

    public List<Reg> getAll(){
        return regRepository.findAll();
    }

    public void saveReg(Reg r){
        regRepository.save(r);
    }

    public List<Reg> findWithRoomId(Long id){
        return regRepository.findByRoomId(id);
    }
}
