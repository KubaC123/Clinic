package cl.dbapp.spring.frontend.view;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@BodySize
@Route(Menu.HOME_ROUTE)
@StyleSheet("styles.css")
public class Menu extends VerticalLayout {

    public static final String HOME_ROUTE = "home";
    public static final String REGISTERED_VISIT_ROUTE = "visits";
    public static final String SEARCH_APPOINTMENTS_ROUTE = "search";
    public static final String BROWSE_APPOINTMENTS_ROUTE = "appointments";
    public static final String BROWSE_CLINICS_ROUTE = "clinics";
    public static final String MY_PROFILE_ROUTE = "myProfile";

    private static final String MENU_CLASS = "main-menu";
    private static final String MENU_BUTTON_CLASS = "menu-button";
    public static final String APP_HEADER = "Student-MED";
    public static final String DARK_THEME = "dark";
    private static final String REGISTERED_VISITS = "My registered visits";
    private static final String BROWSE_APPOINTMENTS = "Browse appointments";
    private static final String BROWSE_CLINIC = "Browse clinics";
    private static final String MY_PROFILE = "My profile";

    private H1 appHeader;
    private List<Button> menuButtons = new ArrayList<>();


    public Menu() {
        addClassName(MENU_CLASS);
        applicationHeader();
        registeredVisitButton();
        browseAppointmentButton();
        myProfileButton();
        add(appHeader);
        menuButtons.forEach(button -> button.setClassName(MENU_BUTTON_CLASS));
        menuButtons.forEach(button -> button.setAutofocus(Boolean.TRUE));
        menuButtons.forEach(this::add);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    private void applicationHeader() {
        appHeader = new H1(APP_HEADER);
        appHeader.getElement().getThemeList().add(DARK_THEME);
    }

    private void registeredVisitButton() {
        Button registeredVisitButton = new Button(REGISTERED_VISITS, new Icon(VaadinIcon.CALENDAR_USER));
        registeredVisitButton.addClickListener(click -> {
            registeredVisitButton.getUI().ifPresent(ui -> ui.navigate(REGISTERED_VISIT_ROUTE));
        });
        menuButtons.add(registeredVisitButton);
    }

    private void browseAppointmentButton() {
        Button browseAppointmentButton = new Button(BROWSE_APPOINTMENTS, new Icon(VaadinIcon.DOCTOR));
        browseAppointmentButton.addClickListener(click -> {
            browseAppointmentButton.getUI().ifPresent(ui -> ui.navigate(BROWSE_APPOINTMENTS_ROUTE));
        });
        menuButtons.add(browseAppointmentButton);
    }

    private void myProfileButton() {
        Button myProfileButton = new Button(MY_PROFILE, new Icon(VaadinIcon.MALE));
        myProfileButton.addClickListener(click -> {
            myProfileButton.getUI().ifPresent(ui -> ui.navigate(MY_PROFILE_ROUTE));
        });
        menuButtons.add(myProfileButton);
    }
}
