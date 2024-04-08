package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.Room;
import VoThuanLoi2.demo.respository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public void save(Room g){
        roomRepository.save(g);
    }

    public Room findByName(String name){
        return roomRepository.findByName(name);
    }

    public Room findByPassWord(String password){
        return roomRepository.findByPassword(password);
    }

    public List<Room> getAll(){
        return roomRepository.findAll();
    }
}
