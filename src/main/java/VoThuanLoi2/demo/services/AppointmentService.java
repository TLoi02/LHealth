package VoThuanLoi2.demo.services;

import VoThuanLoi2.demo.entity.Appointment;
import VoThuanLoi2.demo.respository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public void save(Appointment a){
        appointmentRepository.save(a);
    }

    public List<Appointment> findListWithUserId(Long id){
        return appointmentRepository.findByUserId(id);
    }

    public List<Appointment> getAll(){
        return appointmentRepository.findAll();
    }

    public Appointment findById(Long id){
        return appointmentRepository.findById(id).orElse(null);
    }
}
