package cl.dbapp.spring.backend.domain.reservedvisit;

import cl.dbapp.spring.backend.domain.apointment.Apointment;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;

import javax.persistence.*;

@Entity
@Table(name = "RESERVED_VISIT")
public class ReservedVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    private RegisteredUser patient;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "APOINTMENT_ID", nullable = false)
    private Apointment apointment;

    public ReservedVisit() { }

    public ReservedVisit(RegisteredUser patient, Apointment apointment) {
        this.patient = patient;
        this.apointment = apointment;
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

    public Apointment getApointment() {
        return apointment;
    }

    public void setApointment(Apointment apointment) {
        this.apointment = apointment;
    }
}
