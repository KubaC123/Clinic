package cl.dbapp.spring.backend.domain.reservedvisit;

import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservedVisitRepository extends JpaRepository<ReservedVisit, Long> {

    List<ReservedVisit> findReservedVisitByPatient(RegisteredUser registeredUser);
}
