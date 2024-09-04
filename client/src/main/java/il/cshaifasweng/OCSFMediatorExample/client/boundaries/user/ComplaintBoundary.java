package il.cshaifasweng.OCSFMediatorExample.client.boundaries.user;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.ComplaintController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.RegisteredUserController;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.animationAndImages.Animations;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationsBuilder;
import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ComplaintMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.RegisteredUserMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.RegisteredUser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ComplaintBoundary implements Initializable {


    @FXML
    private StackPane stackPane;
    @FXML
    private BorderPane complaintPane;



    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtCustomerEmail;

    @FXML
    private Label complaintHeader;

    @FXML
    private TextArea txtComplaintDetails;

    @FXML
    private Label emailHeader;

    @FXML
    private Label nameHeader;

    @FXML
    private Button btnSubmitComplaint;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";


    private RegisteredUser user;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComplaintForm();

        EventBus.getDefault().register(this);
    }

    private void initializeComplaintForm() {
        if(!SimpleClient.user.isEmpty() && MainBoundary.getEmployee()==null) {
            txtCustomerName.setVisible(false);
            txtCustomerEmail.setVisible(false);
            nameHeader.setVisible(false);
            emailHeader.setVisible(false);
            RegisteredUserController.getUserByID(SimpleClient.user);
        }
    }

    @FXML
    private void submitComplaint() {
        String customerName="", customerEmail="",complaintDetails="";
        if(txtCustomerName.isVisible()) {
            customerName = txtCustomerName.getText().trim();
            customerEmail = txtCustomerEmail.getText().trim();
        }
        complaintDetails = txtComplaintDetails.getText().trim();

        if(!txtCustomerName.isVisible()) {
            if (complaintDetails.isEmpty() ) {
                Animations.shake(txtComplaintDetails);
                NotificationsBuilder.create(NotificationType.ERROR, "Please fill in all required fields.",complaintPane);
                return;
            }
            LocalDateTime creationDate = LocalDateTime.now();
            ComplaintController.addComplaintRegister(complaintDetails, creationDate, null, false, user);
        }
        else {
            if (customerName.isEmpty() || customerEmail.isEmpty() || complaintDetails.isEmpty()) {
                if (customerName.isEmpty()) {
                    Animations.shake(txtCustomerName);
                }
                if (customerEmail.isEmpty()) {
                    Animations.shake(txtCustomerEmail);
                }
                if (complaintDetails.isEmpty()) {
                    Animations.shake(txtComplaintDetails);
                }
                NotificationsBuilder.create(NotificationType.ERROR, "Please fill in all required fields.",complaintPane);
                return;
            }
            Pattern pattern = Pattern.compile(EMAIL_REGEX);
            if (!pattern.matcher(customerEmail).matches())
            {
                NotificationsBuilder.create(NotificationType.ERROR,"Email address is invalid.",complaintPane);
                return;
            }


            LocalDateTime creationDate = LocalDateTime.now();
            ComplaintController.addComplaintUnregister(complaintDetails, creationDate, null, false, txtCustomerEmail.getText());
        }
    }

    @Subscribe
    public void onComplaintMessageReceived(ComplaintMessage message) {
        switch (message.responseType)
        {
            case COMPLIANT_ADDED:
                AlertsBuilder.create(AlertType.SUCCESS, stackPane, complaintPane, complaintPane, "Complaint submitted successfully.\n A response will be sent to your email within 24 hours.");
                break;
            case COMPLIANT_MESSAGE_FAILED:
                AlertsBuilder.create(AlertType.ERROR, stackPane, complaintPane, complaintPane, "Failed to submit complaint.\n Please try again later."
                );

        }
        txtCustomerEmail.clear();
        txtComplaintDetails.clear();
        txtCustomerName.clear();
     }

    @Subscribe
    public void onRegisteredUserMessageReceived(RegisteredUserMessage message) { user= message.registeredUser;}

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }
}
