package il.cshaifasweng.OCSFMediatorExample.client.boundaries.registeredUser;

import il.cshaifasweng.OCSFMediatorExample.client.boundaries.user.MainBoundary;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.MovieController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.PurchaseController;
import il.cshaifasweng.OCSFMediatorExample.client.util.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.animations.Animations;
import il.cshaifasweng.OCSFMediatorExample.client.util.generators.ButtonFactory;
import il.cshaifasweng.OCSFMediatorExample.client.util.constants.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ComplaintMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.PurchaseMessage;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import il.cshaifasweng.OCSFMediatorExample.client.util.DialogTool;
import il.cshaifasweng.OCSFMediatorExample.client.util.CustomContextMenu;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class OrdersBoundary implements Initializable {


    @FXML
    private AnchorPane OrderContainer;



    @FXML
    private TableColumn<Purchase, Integer> colId;

    @FXML
    private TableColumn<Purchase, Button> colStatus;

    @FXML
    private TableColumn<Purchase, Button> colTypePurchase;

    @FXML
    private TableColumn<Purchase, String> colpurchaseDate;

    @FXML
    private TextField txtCounter;
    @FXML
    private AnchorPane deleteUserContainer;

    @FXML
    private HBox hboxSearch;


    @FXML
    private AnchorPane rootUsers;

    @FXML
    private StackPane stckUsers;

    @FXML
    private TableView<Purchase> tblOrders;

    @FXML
    private Text txtRefund;

    @FXML
    private Button btnDelete;



    private DialogTool dialogMyOrder;

    private DialogTool dialogDelete;

    private ObservableList<Purchase> listOrders;


    private int id;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listOrders = FXCollections.observableArrayList();
        EventBus.getDefault().register(this);
        setId(MainBoundary.getId());
        PurchaseController.GetPurchasesByCustomerID(id);
        animateNodes();
        setContextMenu();

    }

    public void setId(int id) {
        this.id = id;
    }



    @Subscribe
    public void onPurchaseMessageReceived(PurchaseMessage message) {
        System.out.println(message.responseType);

        Platform.runLater(() -> {  if (message.responseType == PurchaseMessage.ResponseType.PURCHASES_LIST) {
            loadTableData(message.purchases) ;
        }else if (message.responseType == PurchaseMessage.ResponseType.PURCHASE_REMOVED) {

            loadTableData(message.purchases) ;
        }
        else if (message.responseType == PurchaseMessage.ResponseType.PURCHASE_FAILED) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "Cannot Cancell purchase selected!");
        }
        else {
            MovieController.requestAllMovies();
        }});
    }


    @Subscribe
    public void onComplainMessageReceived(ComplaintMessage message) {
        System.out.println(message.responseType);
        Platform.runLater(() -> {  if (message.responseType == ComplaintMessage.ResponseType.COMPLIANT_ADDED) {
            AlertsBuilder.create(AlertType.SUCCESS, stckUsers, rootUsers, tblOrders, "Complaint added!");
        }else if (message.responseType == ComplaintMessage.ResponseType.COMPLIANT_MESSAGE_FAILED) {

            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "Complaint message failed!");

        }
        });
    }


    private void loadTableData(List<Purchase> purchases) {
        Platform.runLater(() -> {
            listOrders = FXCollections.observableArrayList(purchases);
            tblOrders.setItems(listOrders);
            tblOrders.setFixedCellSize(30);
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colpurchaseDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPurchaseDate().toLocalDate().toString()));
            ButtonFactory buttonFactory = new ButtonFactory();
            colTypePurchase.setCellValueFactory(new ButtonFactory.ButtonTypePurchaseCellValueFactory());
            colStatus.setCellValueFactory(param -> {
                Button button = new Button("Available");
                button.setPrefWidth(100);
                button.getStyleClass().addAll("button-green", "table-row-cell");
                return new SimpleObjectProperty<>(button);
            });
        });
    }



    private void setContextMenu() {
        CustomContextMenu contextMenu = new CustomContextMenu(tblOrders);
        contextMenu.setActionDetails(ev -> {
            showDialogOrder();
            contextMenu.hide();
        });

        contextMenu.setActionEdit(ev -> {
            showDialogComplain();
            contextMenu.hide();
        });

        contextMenu.setActionDelete(ev -> {
            showDialogCancel();
            contextMenu.hide();
        });

        contextMenu.show();
    }

    private void animateNodes() {
        Animations.fadeInUp(tblOrders);
        Animations.fadeInUp(hboxSearch);
    }

    @FXML
    private void showDialogOrder() {
        disableTable();
        Purchase selectedPurchase = tblOrders.getSelectionModel().getSelectedItem();

        if (selectedPurchase == null) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "No purchase selected");
            tblOrders.setDisable(false);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.DIALOG_TICKET_VIEW));
            AnchorPane ticketPane = loader.load();

            DialogTicket dialogTicket = loader.getController();
            dialogTicket.setOrdersController(this);

            dialogTicket.setTicketInfo(selectedPurchase);

            OrderContainer.getChildren().clear();
            OrderContainer.getChildren().add(ticketPane);
            OrderContainer.setVisible(true);

            dialogMyOrder = new DialogTool(OrderContainer, stckUsers);
            dialogMyOrder.show();

            dialogMyOrder.setOnDialogClosed(ev -> {
                tblOrders.setDisable(false);
                rootUsers.setEffect(null);
                OrderContainer.setVisible(false);
            });

            rootUsers.setEffect(ConstantsPath.BOX_BLUR_EFFECT);

        } catch (IOException e) {
            e.printStackTrace();
            tblOrders.setDisable(false);
        }
    }




    @FXML
    private void showDialogComplain() {
        disableTable();
        Purchase selectedPurchase = tblOrders.getSelectionModel().getSelectedItem();

        if (selectedPurchase == null) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "No purchase selected");
            tblOrders.setDisable(false);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.DIALOG_COMPLAINT_VIEW));
            AnchorPane ticketPane = loader.load();

            DialogComplaint dialogComplaint = loader.getController();
            dialogComplaint.setOrdersController(this);

            dialogComplaint.setPurchase(selectedPurchase);

            OrderContainer.getChildren().clear();
            OrderContainer.getChildren().add(ticketPane);
            OrderContainer.setVisible(true);

            dialogMyOrder = new DialogTool(OrderContainer, stckUsers);
            dialogMyOrder.show();

            dialogMyOrder.setOnDialogClosed(ev -> {
                tblOrders.setDisable(false);
                rootUsers.setEffect(null);
                OrderContainer.setVisible(false);
            });

            rootUsers.setEffect(ConstantsPath.BOX_BLUR_EFFECT);

        } catch (IOException e) {
            e.printStackTrace();
            tblOrders.setDisable(false);
        }
    }



    @FXML
    public void closeDialog() {
        if (dialogMyOrder != null) {
            dialogMyOrder.close();
        }
    }

    @FXML
    public void closeDialogDelete() {
        if (dialogDelete != null) {
            dialogDelete.close();
        }
    }



    private void disableTable() {
        tblOrders.setDisable(true);
    }


    @FXML
    private void showDialogCancel() {
        Purchase delete = tblOrders.getSelectionModel().getSelectedItem();
        if (delete == null) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, ConstantsPath.MESSAGE_NO_RECORD_SELECTED);
            return;
        }
        if (delete instanceof  MultiEntryTicket) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "You cannot cancel multi-entry tickets!");
            return;
        }
        txtRefund.setText(processRefund(delete));

        Platform.runLater(() -> {
            rootUsers.setEffect(ConstantsPath.BOX_BLUR_EFFECT);
            deleteUserContainer.setVisible(true);
            disableTable();

            dialogDelete = new DialogTool(deleteUserContainer, stckUsers);


            btnDelete.setOnAction(ev -> {

                PurchaseController.RemovePurchase(delete);
                dialogDelete.close();
            });

            dialogDelete.show();

            dialogDelete.setOnDialogClosed(ev -> {
                tblOrders.setDisable(false);
                rootUsers.setEffect(null);
                deleteUserContainer.setVisible(false);
                txtRefund.setText("");
            });
        });
    }
    @FXML
    private void handleRefund(Purchase delete) {

        String refundDetails = processRefund(delete);
        txtRefund.setText(refundDetails);

        AlertsBuilder.create(AlertType.SUCCESS, stckUsers, rootUsers, tblOrders, refundDetails);
    }

    private String processRefund(Purchase purchase) {
        if (purchase instanceof MovieTicket) {
            MovieTicket movieTicket = (MovieTicket) purchase;
            int price = movieTicket.getMovieInstance().getMovie().getTheaterPrice();
            LocalDateTime screeningTime = purchase.getPurchaseDate();
            long hoursUntilScreening = ChronoUnit.HOURS.between(LocalDateTime.now(), screeningTime);
            if (hoursUntilScreening > 3) {
                return "You will refund Full Price Ticket: " + price + "₪";
            } else if (hoursUntilScreening > 1) {
                return "You will refund 50% of the Price Ticket: " + price * 0.5 + "₪";
            } else {
                return "You won't be refunded!";
            }
        } else {
            HomeViewingPackageInstance movieTicket = (HomeViewingPackageInstance) purchase;
            return "You will refund 50% of the Price Ticket: " + movieTicket.getMovie().getHomeViewingPrice() * 0.5 + "₪";
        }
    }

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }
}
