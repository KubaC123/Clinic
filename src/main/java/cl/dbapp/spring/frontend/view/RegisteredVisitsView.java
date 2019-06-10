package cl.dbapp.spring.frontend.view;

import cl.dbapp.spring.backend.domain.apointment.Appointment;
import cl.dbapp.spring.backend.domain.apointment.AppointmentRepository;
import cl.dbapp.spring.backend.domain.clinic.Clinic;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUserRepository;
import cl.dbapp.spring.backend.domain.reservedvisit.ReservedVisit;
import cl.dbapp.spring.backend.domain.reservedvisit.ReservedVisitRepository;
import cl.dbapp.spring.frontend.user.UserContext;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Route(Menu.REGISTERED_VISIT_ROUTE)
@StyleSheet("styles.css")
public class RegisteredVisitsView extends VerticalLayout {

    private static final String RESERVED_VISITS_CLASS = "reserved-visits";
    private static final String CANCEL_BUTTON_CLASS = "cancel-button";
    private static final String YES_BUTTON_CLASS = "yes-button";
    private static final String NO_BUTTON_CLASS = "no-button";

    private static final String NO_RESERVED_VISIT_MSG = "You have no upcoming appointments. Look for a free date by selecting the 'Browse appointments' option from the main menu.";
    private static final String CANCEL_SUCCEDED = "Appointment canceled.";

    private static final String MENU_BUTTON = "menu";
    private static final String CANCEL_BUTTON = "Cancel";
    private static final String DOCTOR_HEADER = "Doctor";
    private static final String CLINIC_HEADER = "Clinic";
    private static final String ADDRESS_HEADER = "Address";
    private static final String DATE_HEADER = "Date";
    private static final String ACTION_HEADER = "Actions";
    private static final int NOTIFICATION_DURATION = 10000;
    private static final int SHORT_NOTIFICATION_DURATION = 1200;
    private static final String DATE_WITH_TIME_PATTERN = "dd-MM-yyyy HH:mm";

    private static SimpleDateFormat DATE_FORMAT_WITH_TIME;
    private final RegisteredUserRepository registeredUserRepository;
    private final ReservedVisitRepository reservedVisitRepository;
    private final AppointmentRepository appointmentRepository;
    private final RegisteredUser currentUser;

    private Date todayDate = new Date(System.currentTimeMillis());
    private String today;

    private ReservedVisit visitToCanel = new ReservedVisit();

    private H1 appHeader;
    private Button backToMenuButton;
    private Grid<ReservedVisit> reservedVisitGrid;
    private Grid<ReservedVisit> historyVisitGrid;
    private Notification cancelNotification;
    private Notification noReservedVisitsNotification;
    private Dialog confirmationDialog;

    private List<ReservedVisit> reservedVisits;

    public RegisteredVisitsView(RegisteredUserRepository registeredUserRepository, ReservedVisitRepository reservedVisitRepository, AppointmentRepository appointmentRepository) {
        addClassName(RESERVED_VISITS_CLASS);
        this.registeredUserRepository = registeredUserRepository;
        this.reservedVisitRepository = reservedVisitRepository;
        this.appointmentRepository = appointmentRepository;
        this.currentUser = UserContext.getUser(registeredUserRepository);
        this.reservedVisits = reservedVisitRepository.findReservedVisitByPatientAndActive(currentUser, Boolean.TRUE);
        DATE_FORMAT_WITH_TIME = new SimpleDateFormat(DATE_WITH_TIME_PATTERN);
        this.today = DATE_FORMAT_WITH_TIME.format(todayDate);

        setApplicationHeader();
        setBackToMenuButton();
        setCancelNotification();
        setConfirmationDialog();
        setReservedVisitGrid();
        setHistoryVisitGrid();
        setNoReservedVisitNotification();

        addComponentAsFirst(appHeader);
        add(backToMenuButton);
        if(reservedVisits.isEmpty()) {
            noReservedVisitsNotification.open();
        } else {
            Label label1 = new Label("Your upcoming visits");
            label1.setClassName("label");
            add(label1);
            add(reservedVisitGrid);
        }
        Label label2 = new Label("Your past visits");
        label2.setClassName("label");
        add(label2);
        add(historyVisitGrid);
        setSizeFull();
    }

    private void setApplicationHeader() {
        appHeader = new H1(Menu.APP_HEADER);
        appHeader.getElement().getThemeList().add(Menu.DARK_THEME);
    }

    private void setBackToMenuButton() {
        backToMenuButton = new Button(MENU_BUTTON, new Icon(VaadinIcon.ANGLE_LEFT));
        backToMenuButton.addClickListener(click -> {
            noReservedVisitsNotification.close();
            backToMenuButton.getUI().ifPresent(ui -> ui.navigate(Menu.HOME_ROUTE));
        });
        backToMenuButton.setAutofocus(Boolean.TRUE);
    }

    private void setCancelNotification() {
        cancelNotification = new Notification();
        cancelNotification.setText(CANCEL_SUCCEDED);
        cancelNotification.setDuration(SHORT_NOTIFICATION_DURATION);
        cancelNotification.setPosition(Notification.Position.BOTTOM_START);
    }

    private void setConfirmationDialog() {
        confirmationDialog = new Dialog(new Label("Do you really want to cancel this visit?"));
        confirmationDialog.setCloseOnEsc(false);
        confirmationDialog.setCloseOnOutsideClick(false);
        Button confirmButton = new Button("Yes", new Icon(VaadinIcon.CHECK_CIRCLE));
        Button cancelButton = new Button("No", new Icon(VaadinIcon.CLOSE));

        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);

        cancelButton.addClickListener(buttonClick2 -> confirmationDialog.close());

        confirmButton.addClickListener(buttonClick -> {
            confirmationDialog.close();
            Appointment appointmentToFree = visitToCanel.getAppointment();
            appointmentToFree.setReserved(Boolean.FALSE);
            appointmentRepository.save(appointmentToFree);
            visitToCanel.setActive(Boolean.FALSE);
            reservedVisitRepository.save(visitToCanel);
            reservedVisitGrid.setItems(reservedVisitRepository.findReservedVisitByPatientAndActive(currentUser, Boolean.TRUE));
            confirmationDialog.close();
            if(reservedVisitRepository.findReservedVisitByPatientAndActive(currentUser, Boolean.TRUE).isEmpty()) {
                noReservedVisitsNotification.open();
            } else {
                cancelNotification.open();
            }
        });
        confirmButton.setClassName(YES_BUTTON_CLASS);
        cancelButton.setClassName(NO_BUTTON_CLASS);
        confirmButton.setAutofocus(Boolean.TRUE);
        cancelButton.setAutofocus(Boolean.TRUE);
        confirmationDialog.add(buttonsLayout);
    }

    private void setReservedVisitGrid() {
        reservedVisitGrid = new Grid<>();
        reservedVisitGrid.setItems(reservedVisitRepository.findReservedVisitByPatientAndActive(currentUser, Boolean.TRUE).stream()
        .filter(reservedVisit -> reservedVisit.getAppointment().getDate().compareTo(todayDate) > 0));

        Grid.Column<ReservedVisit> doctorColumn = reservedVisitGrid
                .addColumn(reservedVisit -> {
                    RegisteredUser doctor = reservedVisit.getAppointment().getDoctor();
                    return MessageFormat.format("{0} {1} {2}", doctor.getSpeciality(), doctor.getName(), doctor.getLastName());
                }).setHeader(DOCTOR_HEADER);

        Grid.Column<ReservedVisit> dateColumn = reservedVisitGrid
                .addColumn(reservedVisit -> DATE_FORMAT_WITH_TIME.format(reservedVisit.getAppointment().getDate())).setHeader(DATE_HEADER);

        Grid.Column<ReservedVisit> clinicColumn = reservedVisitGrid
                .addColumn(reservedVisit -> reservedVisit.getAppointment().getClinic().getName()).setHeader(CLINIC_HEADER);

        Grid.Column<ReservedVisit> addressColumn = reservedVisitGrid
                .addColumn(reservedVisit -> {
                    Clinic clinic = reservedVisit.getAppointment().getClinic();
                    return MessageFormat.format("{0}, {1} {2}", clinic.getCity(), clinic.getStreet(), clinic.getStreetNumber());
                }).setHeader(ADDRESS_HEADER);

        reservedVisitGrid.addComponentColumn(reservedVisit -> createCancelButton(reservedVisit))
                .setHeader(ACTION_HEADER);

        reservedVisitGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
    }

    private Button createCancelButton(ReservedVisit visitToCancel) {
        Button cancelButton = new Button(CANCEL_BUTTON, new Icon(VaadinIcon.MINUS_CIRCLE));
        cancelButton.addClickListener(click -> {
            this.visitToCanel = visitToCancel;
            confirmationDialog.open();
        });
        cancelButton.setClassName(CANCEL_BUTTON_CLASS);
        return cancelButton;
    }

    private void setHistoryVisitGrid() {
        historyVisitGrid = new Grid<>();
        historyVisitGrid.setItems(reservedVisitRepository.findReservedVisitByPatientAndActive(currentUser, Boolean.TRUE).stream()
        .filter(reservedVisit -> reservedVisit.getAppointment().getDate().compareTo(todayDate) < 0));

        Grid.Column<ReservedVisit> doctorColumn = historyVisitGrid
                .addColumn(reservedVisit -> {
                    RegisteredUser doctor = reservedVisit.getAppointment().getDoctor();
                    return MessageFormat.format("{0} {1} {2}", doctor.getSpeciality(), doctor.getName(), doctor.getLastName());
                }).setHeader(DOCTOR_HEADER);

        Grid.Column<ReservedVisit> dateColumn = historyVisitGrid
                .addColumn(reservedVisit -> DATE_FORMAT_WITH_TIME.format(reservedVisit.getAppointment().getDate())).setHeader(DATE_HEADER);

        Grid.Column<ReservedVisit> clinicColumn = historyVisitGrid
                .addColumn(reservedVisit -> reservedVisit.getAppointment().getClinic().getName()).setHeader(CLINIC_HEADER);

        historyVisitGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        historyVisitGrid.setClassName("history");
    }

    private void setNoReservedVisitNotification() {
        noReservedVisitsNotification = new Notification();
        noReservedVisitsNotification.setDuration(NOTIFICATION_DURATION);
        noReservedVisitsNotification.setPosition(Notification.Position.MIDDLE);
        noReservedVisitsNotification.setText(NO_RESERVED_VISIT_MSG);
    }

}
