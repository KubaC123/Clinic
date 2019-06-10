package cl.dbapp.spring.frontend.view;

import cl.dbapp.spring.backend.domain.apointment.Appointment;
import cl.dbapp.spring.backend.domain.apointment.AppointmentRepository;
import cl.dbapp.spring.backend.domain.clinic.Clinic;
import cl.dbapp.spring.backend.domain.clinic.ClinicRepository;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUserRepository;
import cl.dbapp.spring.backend.domain.registereduser.Speciality;
import cl.dbapp.spring.backend.domain.reservedvisit.ReservedVisit;
import cl.dbapp.spring.backend.domain.reservedvisit.ReservedVisitRepository;
import cl.dbapp.spring.frontend.user.UserContext;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route(Menu.BROWSE_APPOINTMENTS_ROUTE)
@StyleSheet("styles.css")
public class AppointmentsView extends VerticalLayout {

    private static final String BROWSE_APPOINTMENTS_CLASS = "browse-appointments";
    private static final String RESERVE_BUTTON_CLASS = "reserve-button";
    private static final String YES_BUTTON_CLASS = "yes-button";
    private static final String NO_BUTTON_CLASS = "no-button";
    private static final String MENU_BUTTON = "menu";
    private static final String RESERVE_BUTTON = "Reserve";
    private static final String CLINIC_HEADER = "Clinic";
    private static final String SPECIALITY_HEADER = "Speciality";
    private static final String DOCTOR_HEADER = "Doctor";
    private static final String DATE_HEADER = "Date";
    private static final String ACTION_HEADER = "Actions";

    private static final int LONG_NOTIFICATION_DURATION = 4500;
    private static final int SHORT_NOTIFICATION_DURATION = 2000;
    private static final String SELECTED_VISIT_MSG = "You selected appointment at {0}, in {1}, with Doctor {2}.";
    private static final String AVAILABLE_APPOINTMENTS_MSG = "Found {0} available appointments.";
    private static final String APPOINTMENT_RESERVED_MSG = "Appointment reserved :)";
    private static final String DATE_WITH_TIME_PATTERN = "dd-MM-yyyy HH:mm";
    private static final String DATE_PATTERN = "dd-MM-yyyy";

    private static SimpleDateFormat DATE_FORMAT_WITH_TIME;
    private static SimpleDateFormat DATE_FORMAT;

    private AppointmentRepository appointmentRepository;
    private ClinicRepository clinicRepository;
    private ReservedVisitRepository reservedVisitRepository;
    private RegisteredUserRepository registeredUserRepository;
    private AppointmentSearchCriteria searchCriteria;

    private RegisteredUser currentUser;
    private List<Clinic> clinics = new ArrayList<>();
    private List<Appointment> filteredAppointments = new ArrayList<>();
    private Appointment selectedAppointment = new Appointment();

    private Date todayDate = new Date(System.currentTimeMillis());

    private H1 appHeader;
    private Button backToMenuButton;
    private VerticalLayout backToMenuLayout;
    private DatePicker datePicker;
    private ComboBox<Clinic> clinicComboBox;
    private List<Checkbox> specialityCheckBoxes;
    private Notification appointmentNotification;
    private Grid<Appointment> appointmentsGrid;
    private HorizontalLayout clinicAndDateLayout;
    private HorizontalLayout specialitiesLayout;
    private Dialog confirmationDialog;

    public AppointmentsView(AppointmentRepository appointmentRepository, ClinicRepository clinicRepository,
                            ReservedVisitRepository reservedVisitRepository, RegisteredUserRepository registeredUserRepository) {
        DATE_FORMAT_WITH_TIME = new SimpleDateFormat(DATE_WITH_TIME_PATTERN);
        DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
        setDataFields(appointmentRepository, clinicRepository, reservedVisitRepository, registeredUserRepository);
        setComponents();
    }

    private void setDataFields(AppointmentRepository appointmentRepository, ClinicRepository clinicRepository,
                               ReservedVisitRepository reservedVisitRepository, RegisteredUserRepository registeredUserRepository) {
        this.clinicRepository = clinicRepository;
        this.reservedVisitRepository = reservedVisitRepository;
        this.appointmentRepository = appointmentRepository;
        this.registeredUserRepository = registeredUserRepository;
        this.currentUser = UserContext.getUser(registeredUserRepository);
        this.searchCriteria = new AppointmentSearchCriteria();
        this.clinics = clinicRepository.findAll();
    }

    private void setComponents() {
        addClassName(BROWSE_APPOINTMENTS_CLASS);
        setApplicationHeader();
        setBackToMenuLayout();
        setClinicAndDateLayout();
        setSpecialitiesLayout();
        setConfirmationDialog();
        setAppointmentsGrid();
        setAppointmentNotification();
        addComponentAsFirst(appHeader);
        add(backToMenuLayout);
        add(clinicAndDateLayout);
        add(specialitiesLayout);
        add(appointmentsGrid);
        setAlignItems(Alignment.CENTER);
    }

    private void setApplicationHeader() {
        appHeader = new H1(Menu.APP_HEADER);
        appHeader.getElement().getThemeList().add(Menu.DARK_THEME);
    }

    private void setBackToMenuLayout() {
        backToMenuLayout = new VerticalLayout();
        setBackToMenuButton();
        backToMenuLayout.add(backToMenuButton);
        backToMenuLayout.setAlignItems(Alignment.START);
    }

    private void setBackToMenuButton() {
        backToMenuButton = new Button(MENU_BUTTON, new Icon(VaadinIcon.ANGLE_LEFT));
        backToMenuButton.addClickListener(click -> {
            backToMenuButton.getUI().ifPresent(ui -> ui.navigate(Menu.HOME_ROUTE));
        });
        backToMenuButton.setAutofocus(Boolean.TRUE);
    }

    private void setClinicAndDateLayout() {
        clinicAndDateLayout = new HorizontalLayout();
        setClinicComboBox();
        setDatePicker();
        clinicAndDateLayout.add(clinicComboBox, datePicker);
    }

    private void setClinicComboBox() {
        clinicComboBox = new ComboBox<>(CLINIC_HEADER);
        clinicComboBox.setItemLabelGenerator(Clinic::getName);
        clinicComboBox.setItems(clinics);
        clinicComboBox.addValueChangeListener(event ->  {
            searchCriteria.setClinic(clinicComboBox.getValue());
            filteredAppointments = filterAppointmentsWithSearchCriteria();
            appointmentsGrid.setItems(filteredAppointments);
            showFoundAppointmentsNotification();
        });
    }

    private void showReserveAppointmentsNotification(String text) {
        appointmentNotification.setText(text);
        appointmentNotification.setDuration(SHORT_NOTIFICATION_DURATION);
        appointmentNotification.open();
    }

    private void setDatePicker() {
        datePicker = new DatePicker();
        datePicker.setLabel(DATE_HEADER);
        datePicker.addValueChangeListener(event -> {
            LocalDate selectedDate = datePicker.getValue();
            String convertedDate = DATE_FORMAT.format(Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            if(java.sql.Date.valueOf(selectedDate).compareTo(todayDate) > 0) {
                searchCriteria.setDate(convertedDate);
                filteredAppointments = filterAppointmentsWithSearchCriteria();
                appointmentsGrid.setItems(filteredAppointments);
                showFoundAppointmentsNotification();
            } else {
                showReserveAppointmentsNotification("You cannot browse appointments from the past :(");
            }
        });
    }

    private void setSpecialitiesLayout() {
        specialitiesLayout = new HorizontalLayout();
        setSpecialityCheckBoxes();
        specialityCheckBoxes.forEach(specialitiesLayout::add);
    }

    private void setSpecialityCheckBoxes() {
        specialityCheckBoxes = Stream.of(Speciality.values())
                .map(Speciality::getName)
                .map(Checkbox::new)
                .collect(Collectors.toList());
        specialityCheckBoxes.forEach(checkbox -> checkbox.setValue(Boolean.TRUE));
        specialityCheckBoxes.forEach(checkbox -> {
            checkbox.addValueChangeListener(event -> {
               searchCriteria.addSpeciality(checkbox.getLabel(), event.getValue());
               filteredAppointments = filterAppointmentsWithSearchCriteria();
               appointmentsGrid.setItems(filteredAppointments);
               showFoundAppointmentsNotification();
            });
        });
    }

    private void showFoundAppointmentsNotification() {
        appointmentNotification.setText(MessageFormat.format(AVAILABLE_APPOINTMENTS_MSG, filteredAppointments.size()));
        appointmentNotification.setDuration(SHORT_NOTIFICATION_DURATION);
        appointmentNotification.open();
    }

    private void setAppointmentNotification() {
        appointmentNotification = new Notification();
        appointmentNotification.setPosition(Notification.Position.BOTTOM_START);
    }

    private void setConfirmationDialog() {
        confirmationDialog = new Dialog(new Label("Please confirm visit reservation"));
        confirmationDialog.setCloseOnEsc(false);
        confirmationDialog.setCloseOnOutsideClick(false);
        Button confirmButton = new Button(new Icon(VaadinIcon.CHECK_CIRCLE));
        Button cancelButton = new Button(new Icon(VaadinIcon.CLOSE));

        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);

        cancelButton.addClickListener(buttonClick2 -> confirmationDialog.close());

        confirmButton.addClickListener(buttonClick -> {
            confirmationDialog.close();
            reservedVisitRepository.save(new ReservedVisit(currentUser, selectedAppointment, Boolean.TRUE));
            selectedAppointment.setReserved(Boolean.TRUE);
            appointmentRepository.save(selectedAppointment);
            showReserveAppointmentsNotification(APPOINTMENT_RESERVED_MSG);
            appointmentsGrid.setItems(filterAppointmentsWithSearchCriteria());
        });
        confirmButton.setClassName(YES_BUTTON_CLASS);
        cancelButton.setClassName(NO_BUTTON_CLASS);
        confirmButton.setAutofocus(Boolean.TRUE);
        cancelButton.setAutofocus(Boolean.TRUE);
        confirmationDialog.add(buttonsLayout);
    }

    private void setAppointmentsGrid() {
        appointmentsGrid = new Grid<>();
        appointmentsGrid.setItems(appointmentRepository.findAppointmentByReserved(Boolean.FALSE));

        Grid.Column<Appointment> clinicColumn = appointmentsGrid
                .addColumn(appointment -> appointment.getClinic().getName()).setHeader(CLINIC_HEADER);

        Grid.Column<Appointment> specialityColumn = appointmentsGrid
                .addColumn(appointment -> appointment.getDoctor().getSpeciality()).setHeader(SPECIALITY_HEADER);

        Grid.Column<Appointment> doctorName = appointmentsGrid
                .addColumn(appointment -> appointment.getDoctor().getName() + " " + appointment.getDoctor().getLastName()).setHeader(DOCTOR_HEADER);

        Grid.Column<Appointment> dateColumn = appointmentsGrid
                .addColumn(appointment -> DATE_FORMAT_WITH_TIME.format(appointment.getDate())).setHeader(DATE_HEADER);

        appointmentsGrid.addComponentColumn(appointment -> createReserveButton(appointmentsGrid, appointment))
                .setHeader(ACTION_HEADER);

        appointmentsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        appointmentsGrid.asSingleSelect().addValueChangeListener(this::showSelectedAppointmentNotification);
    }

    private Button createReserveButton(Grid<Appointment> appointmentsGrid, Appointment appointment) {
        Button reserveButton = new Button(RESERVE_BUTTON, new Icon(VaadinIcon.CHECK));
        reserveButton.addClickListener(click -> {
            this.selectedAppointment = appointment;
            confirmationDialog.open();
        });
        reserveButton.setClassName(RESERVE_BUTTON_CLASS);
        return reserveButton;
    }

    private void showSelectedAppointmentNotification(AbstractField.ComponentValueChangeEvent<Grid<Appointment>, Appointment> event) {
        String message = MessageFormat.format(SELECTED_VISIT_MSG,
                event.getValue().getDate(),
                event.getValue().getClinic().getName(),
                event.getValue().getDoctor().getLastName());
        appointmentNotification.setText(message);
        appointmentNotification.setDuration(LONG_NOTIFICATION_DURATION);
        appointmentNotification.open();
    }

    private List<Appointment> filterAppointmentsWithSearchCriteria() {
        return appointmentRepository.findAppointmentByReserved(Boolean.FALSE).stream()
                .filter(byClinic())
                .filter(byDate())
                .filter(bySpeciality())
                .collect(Collectors.toList());
    }

    private Predicate<Appointment> bySpeciality() {
        return appointment -> searchCriteria.getSelectedSpecialities().contains(appointment.getDoctor().getSpeciality());
    }

    private Predicate<Appointment> byClinic() {
        return appointment -> searchCriteria.getClinic()
                .map(clinic -> appointment.getClinic().getId().equals(clinic.getId())).orElse(Boolean.TRUE);
    }

    private Predicate<Appointment> byDate() {
        return appointment -> searchCriteria.getDate()
                .map(date -> DATE_FORMAT.format(appointment.getDate()).equals(date)).orElse(Boolean.TRUE);
    }

}
