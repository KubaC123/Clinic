package cl.dbapp.spring.frontend.view;

import cl.dbapp.spring.backend.domain.registereduser.RegisteredUser;
import cl.dbapp.spring.backend.domain.registereduser.RegisteredUserRepository;
import cl.dbapp.spring.frontend.user.UserContext;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Route(Menu.MY_PROFILE_ROUTE)
@StyleSheet("styles.css")
public class MyProfileView extends VerticalLayout {

    private static final String MY_PROFILE_VIEW_CLASS = "myprofile-view";
    private static final String SAVE_BUTTON_CLASS = "save-button";
    private static final String INVALID_FIELD_CLASS = "invalid-textfield";
    private static final String VALID_FIELD_CLASS = "valid-textfield";

    private static final String INVALID_INPUT_MSG = "Invalid input :(";
    private static final String VALID_INPUT_MSG = "Updated :)";

    private static final String NAME_FIELD = "Name:";
    private static final String LASTNAME_FIELD = "Last mame:";
    private static final String EMAIL_FIELD = "E-mail:";
    private static final String PHONE_FIELD = "Phone:";
    private static final String MENU_BUTTON = "menu";
    private static final String SAVE_BUTTON = "Save";
    private static final String PHONE_REGEX = "\\+48\\d{9}";

    private RegisteredUserRepository registeredUserRepository;
    private RegisteredUser currentUser;

    private H1 appHeader;
    private Button backToMenuButton;
    private VerticalLayout backToMenuLayout;
    private TextField nameTextField;
    private TextField lastNameTextField;
    private TextField emailTextField;
    private TextField phoneTextField;
    private Button saveButton;
    private Notification inputNotification;
    private Pattern phonePattern;
    private Matcher phoneMatcher;

    public MyProfileView(RegisteredUserRepository registeredUserRepository) {
        setClassName(MY_PROFILE_VIEW_CLASS);
        this.registeredUserRepository = registeredUserRepository;
        this.currentUser = UserContext.getUser(registeredUserRepository);
        phonePattern = Pattern.compile(PHONE_REGEX);
        setComponents();
        addComponentAsFirst(appHeader);
        add(backToMenuLayout);
        add(nameTextField, lastNameTextField, emailTextField, phoneTextField, saveButton);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    private void setComponents() {
        setAppHeader();
        setBackToMenuLayout();
        setInvalidInputNotification();
        setTextFields();
        setSaveButton();
    }

    private void setAppHeader() {
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

    private void setInvalidInputNotification() {
        inputNotification = new Notification();
        inputNotification.setDuration(1500);
        inputNotification.setPosition(Notification.Position.BOTTOM_START);
    }

    private void setTextFields() {
        nameTextField = new TextField(NAME_FIELD);
        nameTextField.setPlaceholder(currentUser.getName());
        nameTextField.setValue(currentUser.getName());
        nameTextField.setAutofocus(Boolean.TRUE);
        nameTextField.setClassName(VALID_FIELD_CLASS);

        lastNameTextField = new TextField(LASTNAME_FIELD);
        lastNameTextField.setPlaceholder(currentUser.getLastName());
        lastNameTextField.setValue(currentUser.getLastName());
        lastNameTextField.setAutofocus(Boolean.TRUE);
        lastNameTextField.setClassName(VALID_FIELD_CLASS);

        emailTextField = new TextField(EMAIL_FIELD);
        emailTextField.setPlaceholder(currentUser.geteMail());
        emailTextField.setValue(currentUser.geteMail());
        emailTextField.setAutofocus(Boolean.TRUE);
        emailTextField.setClassName(VALID_FIELD_CLASS);

        phoneTextField = new TextField(PHONE_FIELD);
        phoneTextField.setPlaceholder(Optional.ofNullable(currentUser.getPhone()).orElse(""));
        phoneTextField.setValue(Optional.ofNullable(currentUser.getPhone()).orElse(""));
        phoneTextField.setAutofocus(Boolean.TRUE);
        phoneTextField.setClassName(VALID_FIELD_CLASS);
    }

    private void setSaveButton() {
        saveButton = new Button(SAVE_BUTTON, new Icon(VaadinIcon.DATABASE));
        saveButton.setAutofocus(Boolean.TRUE);
        saveButton.addClickListener(click -> {
            String newName = nameTextField.getValue();
            String newLastName = lastNameTextField.getValue();
            String newEmail = emailTextField.getValue();
            String newPhone = phoneTextField.getValue();
            if(!validateInput(newName, newLastName, newEmail, newPhone)) {
                inputNotification.setText(INVALID_INPUT_MSG);
                inputNotification.open();
            } else {
                currentUser.setName(newName);
                currentUser.setLastName(newLastName);
                currentUser.seteMail(newEmail);
                currentUser.setPhone(newPhone);
                registeredUserRepository.save(currentUser);
                inputNotification.setText(VALID_INPUT_MSG);
                inputNotification.open();
            }
        });
        saveButton.setClassName(SAVE_BUTTON_CLASS);
    }

    private boolean validateInput(String name, String lastName, String email, String phone) {
        boolean isValid = true;
        if(name.matches(".*\\d.*")) {
            nameTextField.setClassName(INVALID_FIELD_CLASS);
            isValid = false;
        } else {
            nameTextField.setClassName(VALID_FIELD_CLASS);
        }
        if(lastName.matches(".*\\d.*")) {
            lastNameTextField.setClassName(INVALID_FIELD_CLASS);
            isValid = false;
        } else {
            lastNameTextField.setClassName(VALID_FIELD_CLASS);
        }
        if(!EmailValidator.getInstance().isValid(email)) {
            emailTextField.setClassName(INVALID_FIELD_CLASS);
            isValid = false;
        } else {
            emailTextField.setClassName(VALID_FIELD_CLASS);
        }
        if(!phonePattern.matcher(phone).matches()) {
            phoneTextField.setClassName(INVALID_FIELD_CLASS);
            isValid = false;
        } else {
            phoneTextField.setClassName(VALID_FIELD_CLASS);
        }
        return isValid;
    }



}
