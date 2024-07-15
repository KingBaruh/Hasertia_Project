package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.scene.effect.BoxBlur;

import java.util.Objects;

public class Constants {

    public static final String TITLE = "Electronic Store Tool FX";
    public static final Double MIN_WIDTH = 1040.00;
    public static final Double MIN_HEIGHT = 640.00;

    public static final String SOURCE_PACKAGES = "/il/cshaifasweng/OCSFMediatorExample/client";
    public static final String MEDIA_PACKAGE = "/il/cshaifasweng/OCSFMediatorExample/client/media/";
    public static final String VIEWS_PACKAGE = SOURCE_PACKAGES+"/";
    public static final String PROFILE_PICTURES_PACKAGE = MEDIA_PACKAGE + "profiles/";

    public static final String LOGIN_VIEW = VIEWS_PACKAGE + "/LoginView.fxml";
    public static final String START_VIEW = VIEWS_PACKAGE + "/StartView.fxml";
    public static final String MAIN_VIEW = VIEWS_PACKAGE + "/MainView.fxml";

    public static final String STAGE_ICON = MEDIA_PACKAGE + "icon.png";
    public static final String NO_IMAGE_AVAILABLE = MEDIA_PACKAGE + "empty-image.jpg";
    public static final String INFORMATION_IMAGE = MEDIA_PACKAGE + "information.png";
    public static final String ERROR_IMAGE = MEDIA_PACKAGE + "error.png";
    public static final String SUCCESS_IMAGE = MEDIA_PACKAGE + "success.png";

    public static final String CSS_LIGHT_THEME = "/il/cshaifasweng/OCSFMediatorExample/client/LightTheme.css";
    public static final String LIGHT_THEME = Objects.requireNonNull(Constants.class.getResource(CSS_LIGHT_THEME)).toExternalForm();

    public static final String MESSAGE_IMAGE_LARGE = "Please upload a picture smaller than 1 MB.";
    public static final String MESSAGE_IMAGE_NOT_FOUND = "Image not found. The record will be saved.";
    public static final String MESSAGE_INSUFFICIENT_DATA = "Insufficient data";
    public static final String MESSAGE_USER_ALREADY_EXISTS = "This user already exists";
    public static final String MESSAGE_PASSWORDS_NOT_MATCH = "Passwords do not match.";
    public static final String MESSAGE_ENTER_AT_LEAST_4_CHARACTERES = "Please enter at least 4 characters";
    public static final String MESSAGE_NO_RECORD_SELECTED = "Select an item from the table.";

    public static final String MESSAGE_ADDED = "Record added successfully";
    public static final String MESSAGE_UPDATED = "Record updated successfully";
    public static final String MESSAGE_DELETED = "Record deleted successfully";

    public static final String EXISTENT = "Existent";
    public static final String NOT_EXISTENT = "Not existent";

    public static final String REALIZED = "Realized";
    public static final String NOT_REALIZED = "Not realized";

    public static final String REPORTED = "Reported";
    public static final String NOT_REPORTED = "Not reported";

    public static final BoxBlur BOX_BLUR_EFFECT = new BoxBlur(3, 3, 3);
}
