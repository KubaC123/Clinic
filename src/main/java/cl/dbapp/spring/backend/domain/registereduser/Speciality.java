package cl.dbapp.spring.backend.domain.registereduser;

public enum Speciality {

    INTERNIST("Internist"),
    DENTIST("Dentist"),
    PSYCHOLOGIST("Psychologist"),
    NEUROLOGIST("Neurologist"),
    CARDIOLOGIST("Cardiologist"),
    DERMATOLOGIST("Dermatologist"),
    ONCOLOGIST("Oncologist");

    private String name;

    private Speciality(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
