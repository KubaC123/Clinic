package cl.dbapp.spring.backend.domain.registereduser;

public enum UserType {

    PATIENT("Patient"),
    DOCTOR("Doctort");

    private String name;

    UserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
