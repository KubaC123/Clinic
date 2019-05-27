package cl.dbapp.spring.frontend.view;

import cl.dbapp.spring.backend.domain.clinic.Clinic;
import cl.dbapp.spring.backend.domain.registereduser.Speciality;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppointmentSearchCriteria {

    private Clinic clinic;
    private String date;
    private Map<String, Boolean> specialities;

    public AppointmentSearchCriteria() {
        specialities = Stream.of(Speciality.values())
                .map(Speciality::getName)
                .map(speciality -> new AbstractMap.SimpleEntry<>(speciality, Boolean.TRUE))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Optional<Clinic> getClinic() {
        return Optional.ofNullable(clinic);
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Optional<String> getDate() {
        return Optional.ofNullable(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addSpeciality(String speciality, Boolean selected) {
        specialities.put(speciality, selected);
    }

    public Set<String> getSelectedSpecialities() {
        return specialities.entrySet().stream()
                .filter(entry -> entry.getValue().equals(Boolean.TRUE))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
