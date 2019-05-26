package cl.dbapp.spring.frontend.user;

import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUserRepository;
import cl.dbapp.spring.backend.domain.registereduser.UserType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

public class UserContext {

    public static RegisteredUser getUser(RegisteredUserRepository registeredUserRepository) {
        OidcUserInfo userInfo = getUserInfo();
        Optional<RegisteredUser> registeredUser = registeredUserRepository.findUserByOktaSub(userInfo.getSubject()).stream().findAny();
        if(!registeredUser.isPresent()) {
            createRegisteredUser(userInfo, registeredUserRepository);
        }
        return registeredUserRepository.findUserByOktaSub(userInfo.getSubject()).get(0);
    }

    private static OidcUserInfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OidcUser userDetails = (OidcUser) authentication.getPrincipal();
        return userDetails.getUserInfo();
    }

    private static void createRegisteredUser(OidcUserInfo userInfo, RegisteredUserRepository registeredUserRepository) {
        RegisteredUser newUser = new RegisteredUser(
                userInfo.getSubject(),
                userInfo.getGivenName(),
                userInfo.getFamilyName(),
                userInfo.getEmail(),
                UserType.PATIENT.getName());
        registeredUserRepository.save(newUser);
    }
}
