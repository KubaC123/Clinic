package cl.dbapp.spring.backend.domain.registereduser;

import javax.persistence.*;

@Entity
@Table(name="REGISTERED_USERS")
public class RegisteredUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "OKTA_SUB", nullable = false)
    private String oktaSub;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LASTNAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String eMail;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "USER_TYPE")
    private String userType;

    @Column(name = "SPECIALITY")
    private String speciality;

    public RegisteredUser() { }

    public RegisteredUser(String oktaSub, String name, String lastName, String eMail, String userType) {
        this.oktaSub = oktaSub;
        this.name = name;
        this.lastName = lastName;
        this.eMail = eMail;
        this.userType = userType;
    }

    public Long getId() {
        return id;
    }

    public String getOktaSub() {
        return oktaSub;
    }

    public void setOktaSub(String oktaSub) {
        this.oktaSub = oktaSub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
}
