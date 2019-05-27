package cl.dbapp.spring.backend.domain.registereduser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {

    List<RegisteredUser> findUserByOktaSub(String oktaSub);
}
