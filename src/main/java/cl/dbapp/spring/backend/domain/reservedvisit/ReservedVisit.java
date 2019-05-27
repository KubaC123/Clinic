package cl.dbapp.spring.backend.domain.reservedvisit;

import cl.dbapp.spring.backend.domain.apointment.Appointment;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;

import javax.persistence.*;

@Entity
@Table(name = "RESERVED_VISIT")
public class ReservedVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    private RegisteredUser patient;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "APOINTMENT_ID", nullable = false)
    private Appointment appointment;

    @Column(name = "ACTIVE")
    private Boolean active;

    public ReservedVisit() { }

    public ReservedVisit(RegisteredUser patient, Appointment appointment, Boolean active) {
        this.patient = patient;
        this.appointment = appointment;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public RegisteredUser getPatient() {
        return patient;
    }

    public void setPatient(RegisteredUser patient) {
        this.patient = patient;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
