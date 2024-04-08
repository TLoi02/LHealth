package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.Subscribe;
import VoThuanLoi2.demo.respository.SubscribeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscribeService {
    @Autowired
    private SubscribeRepository subscribeRepository;

    public void save(Subscribe s){
        subscribeRepository.save(s);
    }

    public Subscribe findByEmail(String email){
        return subscribeRepository.findByEmail(email);
    }

    public List<Subscribe> getAll(){
        return subscribeRepository.findAll();
    }
}
