package cl.dbapp.spring.backend.domain.apointment;

import cl.dbapp.spring.backend.domain.clinic.Clinic;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "APOINTMENT")
public class Apointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "CLINIC_ID", nullable = false)
    private Clinic clinic;

    @Column(name = "DATE")
    private Date date;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DOCTOR_ID", nullable = false)
    private RegisteredUser doctor;

    @Column(name = "RESERVED")
    private Boolean reserved;

    public Apointment(Clinic clinic, Date date, RegisteredUser doctor, Boolean reserved) {
        this.clinic = clinic;
        this.date = date;
        this.doctor = doctor;
        this.reserved = reserved;
    }

    public Long getId() {
        return id;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RegisteredUser getDoctor() {
        return doctor;
    }

    public void setDoctor(RegisteredUser doctor) {
        this.doctor = doctor;
    }

    public Boolean getReserved() {
        return reserved;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }
}
