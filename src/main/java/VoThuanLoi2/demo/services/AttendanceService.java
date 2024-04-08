package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.Attendance;
import VoThuanLoi2.demo.respository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Attendance> getListWithUserID(Integer id){
        return attendanceRepository.findByUserId(id);
    }

    public void deleteByID(Long id){
        attendanceRepository.deleteById(id);
    }
}
