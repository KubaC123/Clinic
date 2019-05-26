package cl.dbapp.spring.frontend.view;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.router.Route;

@BodySize
@Route(Menu.HOME_PATH)
public class Menu extends VerticalLayout {

    public static final String HOME_PATH = "home";
    public static final String REGISTERED_VISIT_ROUTE = "registeredVisit";
    public static final String BROWSE_APOINTMENTS_ROUTE = "browseApointment";
    public static final String MY_PROFILE_ROUTE = "myProfile";

    private static final String REGISTERED_VISITS_BUTTON_TITLE = "My registered visit";
    private static final String BROWSE_APOINTMENTS_BUTTON_TITLE = "Browse apointment";
    private static final String MY_PROFILE_BUTTON_TITLE = "My profile";

    private Button registeredVisitsButton;
    private Button browseApointmentButton;
    private Button myProfileButton;

    public Menu() {
        registeredVisitsButton = new Button(REGISTERED_VISITS_BUTTON_TITLE, new Icon(VaadinIcon.CALENDAR_USER));
        registeredVisitsButton.addClickListener(click -> {
            registeredVisitsButton.getUI().ifPresent(ui -> ui.navigate(REGISTERED_VISIT_ROUTE));
        });

        browseApointmentButton = new Button(BROWSE_APOINTMENTS_BUTTON_TITLE, new Icon(VaadinIcon.DOCTOR));
        browseApointmentButton.addClickListener(click -> {
            browseApointmentButton.getUI().ifPresent(ui -> ui.navigate(BROWSE_APOINTMENTS_ROUTE));
        });

        myProfileButton = new Button(MY_PROFILE_BUTTON_TITLE, new Icon(VaadinIcon.MALE));
        myProfileButton.addClickListener(click -> {
            myProfileButton.getUI().ifPresent(ui -> ui.navigate(MY_PROFILE_ROUTE));
        });
        add(registeredVisitsButton, browseApointmentButton, myProfileButton);
        setSizeFull();
    }
}
