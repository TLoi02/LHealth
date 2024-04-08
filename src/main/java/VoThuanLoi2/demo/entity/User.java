package VoThuanLoi2.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;
    @Column
    private String username;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String name;
    @Column
    private String phone;
    @Column
    private String city;
    @Column
    private String district;
    @Column
    private String ward;
    @Column
    private String address;
    @Column
    private String otp;

    @Column
    private Double weight;
    @Column
    private Double height;
    @Column
    private Double bmi;
    @Column
    private Double age;
    @Column
    private String job;
    @Column
    private String anamnesis;
    @Column
    private String treated;
    @Column
    private Boolean isMenu;
    @Column
    private Boolean isWorkout;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "UserRole",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Favorite> favorites = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Attendance> attendances = new HashSet<>();

    public Date getDateToDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date currentDateWithoutTime = calendar.getTime();
        return  currentDateWithoutTime;
    }

    public boolean hasAttendedToday() {
        Date currentDate = getDateToDay();

        for (Attendance attendance : attendances) {
            if (attendance.getAttendanceDate().equals(currentDate)) {
                return true;
            }
        }
        return false;
    }

    public void addAttendancePoints(int pointsToAdd) {
        Date currentDate = new Date();
        boolean checkAttend = hasAttendedToday();

        if (!checkAttend ) {
            Attendance newAttendance = new Attendance();
            newAttendance.setUser(this);
            newAttendance.setAttendanceDate(currentDate);
            newAttendance.setPoints(pointsToAdd);
            attendances.add(newAttendance);
        }
    }

    public void setPointsCGV(int newPoints) {
        Date currentDate = new Date();
        Attendance newAttendance = new Attendance();
        newAttendance.setUser(this);
        newAttendance.setAttendanceDate(currentDate);
        newAttendance.setPoints(newPoints);

        attendances.add(newAttendance);
    }


    public int getTotalPoints() {
        int totalPoints = 0;
        for (Attendance attendance : attendances) {
            totalPoints += attendance.getPoints();
        }
        return totalPoints;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id);
    }
}
