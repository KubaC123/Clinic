package cl.dbapp.spring.frontend.view;

import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUserRepository;
import cl.dbapp.spring.backend.domain.registereduser.UserType;
import cl.dbapp.spring.backend.domain.reservedvisit.ReservedVisit;
import cl.dbapp.spring.backend.domain.reservedvisit.ReservedVisitRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collections;
import java.util.Optional;

@Route(Menu.REGISTERED_VISIT_ROUTE)
public class RegisteredVisitsView extends VerticalLayout {

    private final RegisteredUserRepository registeredUserRepository;
    private final ReservedVisitRepository reservedVisitRepository;
    private final RegisteredUser currentUser;
    private Grid<ReservedVisit> reservedVisitGrid;

    public RegisteredVisitsView(RegisteredUserRepository registeredUserRepository, ReservedVisitRepository reservedVisitRepository) {
        this.registeredUserRepository = registeredUserRepository;
        this.reservedVisitRepository = reservedVisitRepository;
        this.currentUser = findUserBasedOnAuthorizedUserInfo();
        declareReservedVisitGrid();
    }

    private void declareReservedVisitGrid() {
        reservedVisitGrid = new Grid<>(ReservedVisit.class);
        reservedVisitGrid.setItems(Collections.emptyList());
    }

    private RegisteredUser findUserBasedOnAuthorizedUserInfo() {
        OidcUserInfo userInfo = getUserInfo();
        Optional<RegisteredUser> registeredUser = registeredUserRepository.findUserByOktaSub(userInfo.getSubject()).stream().findAny();
        if(!registeredUser.isPresent()) {
            createRegisteredUser(userInfo);
        }
        return registeredUserRepository.findUserByOktaSub(userInfo.getSubject()).get(0);
    }

    private OidcUserInfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OidcUser userDetails = (OidcUser) authentication.getPrincipal();
        return userDetails.getUserInfo();
    }

    private void createRegisteredUser(OidcUserInfo userInfo) {
        RegisteredUser newUser = new RegisteredUser(
                userInfo.getSubject(),
                userInfo.getGivenName(),
                userInfo.getFamilyName(),
                userInfo.getEmail(),
                UserType.PATIENT.getName());
        registeredUserRepository.save(newUser);
    }

}
