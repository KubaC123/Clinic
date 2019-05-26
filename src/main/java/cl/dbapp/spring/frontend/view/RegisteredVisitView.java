package cl.dbapp.spring.frontend.view;

import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUserRepository;
import cl.dbapp.spring.backend.domain.reservedvisit.ReservedVisit;
import cl.dbapp.spring.backend.domain.reservedvisit.ReservedVisitRepository;
import cl.dbapp.spring.frontend.user.UserContext;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(Menu.REGISTERED_VISIT_ROUTE)
public class RegisteredVisitView extends VerticalLayout {

    private static final String NO_RESERVED_VISIT_MSG = "You have not yet registered any appointments. Look for a free date by selecting the 'Browse appointments' option from the main menu.";
    private static final String MENU_BUTTON = "menu";

    private static final int NOTIFICATION_DURATION = 1000000;

    private final RegisteredUserRepository registeredUserRepository;
    private final ReservedVisitRepository reservedVisitRepository;
    private final RegisteredUser currentUser;

    private Button backToMenuButton;
    private Grid<ReservedVisit> reservedVisitGrid;
    private Notification noReservedVisitNotification;

    private List<ReservedVisit> reservedVisits;

    public RegisteredVisitView(RegisteredUserRepository registeredUserRepository, ReservedVisitRepository reservedVisitRepository) {
        this.registeredUserRepository = registeredUserRepository;
        this.reservedVisitRepository = reservedVisitRepository;
        this.currentUser = UserContext.getUser(registeredUserRepository);
        this.reservedVisits = reservedVisitRepository.findReservedVisitByPatient(currentUser);

        backToMenuButton();
        reservedVisitGrid();
        noReservedVisitNotification();

        add(backToMenuButton);
        if(reservedVisits.isEmpty()) {
            noReservedVisitNotification.open();
        } else {
            add(reservedVisitGrid);
        }
        setSizeFull();
    }

    private void reservedVisitGrid() {
        reservedVisitGrid = new Grid<>(ReservedVisit.class);
        reservedVisitGrid.setItems(reservedVisits);
    }

    private void backToMenuButton() {
        backToMenuButton = new Button(MENU_BUTTON, new Icon(VaadinIcon.ANGLE_LEFT));
        backToMenuButton.addClickListener(click -> {
            noReservedVisitNotification.close();
            backToMenuButton.getUI().ifPresent(ui -> ui.navigate(Menu.HOME_ROUTE));
        });
        backToMenuButton.setAutofocus(Boolean.TRUE);
    }

    private void noReservedVisitNotification() {
        Label information = new Label(NO_RESERVED_VISIT_MSG);
        noReservedVisitNotification = new Notification(information);
        noReservedVisitNotification.setDuration(NOTIFICATION_DURATION);
        noReservedVisitNotification.setPosition(Notification.Position.MIDDLE);
    }

}
