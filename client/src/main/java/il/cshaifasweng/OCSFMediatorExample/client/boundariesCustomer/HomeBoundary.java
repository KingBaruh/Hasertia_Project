package il.cshaifasweng.OCSFMediatorExample.client.boundariesCustomer;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.MovieController;
import il.cshaifasweng.OCSFMediatorExample.client.util.DialogTool;
import il.cshaifasweng.OCSFMediatorExample.client.util.constants.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeBoundary implements Initializable {

    private static final int ITEMS_PER_ROW = 4;
    private List<Movie> items;
    private DialogTool dialogInfo;

    @FXML
    private AnchorPane InfoContainer;

    @FXML
    private Button btnHV;

    @FXML
    private Button btnTheater;

    @FXML
    private StackPane stckHome;

    @FXML
    private GridPane grid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Register this controller to listen for MovieMessage events
        EventBus.getDefault().register(this);

        // Request the list of movies from the server
        MovieController.requestAllMovies();
    }

    @Subscribe
    public void onMovieMessageReceived(MovieMessage message) {
        Platform.runLater(() ->
        {
            try {
                setItems(message.movies);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setItems(List<Movie> movies) throws IOException {
        this.items = movies;
        Platform.runLater(() -> {
            try {
                updateGrid();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void updateGrid() throws IOException {
        grid.getChildren().clear();
        int row = 0;
        int col = 0;
        for (Movie item : items) {
            Node normalItem = createItem(item);
            grid.add(normalItem, col, row);
            col++;
            if (col == ITEMS_PER_ROW) {
                col = 0;
                row++;
            }
        }
    }

    private Node createItem(Movie item) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.SMALL_MOVIE_VIEW));
        StackPane itemBox = loader.load();
        MovieSmallBoundary controller = loader.getController();
        if (controller != null) {
            controller.setMovieShort(item);
            controller.setHomeController(this);
        } else {
            System.err.println("Controller is null");
        }
        return itemBox;
    }

    public void setRoot(Parent root) {
        if (root != null) {
            stckHome.getChildren().setAll(root);
        } else {
            System.err.println("Root is null, cannot set.");
        }
    }

    public void showInfo(Movie movie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.COSTUMER_PACKAGE + "MovieInfo.fxml"));
            Parent pane = loader.load();

            MovieInfoBoundary movieInfoController = loader.getController();
            movieInfoController.sethomeController(this);
            movieInfoController.setInfo(movie);

            InfoContainer.getChildren().clear();
            InfoContainer.getChildren().add(pane);
            InfoContainer.setVisible(true);

            dialogInfo = new DialogTool(InfoContainer, stckHome);
            dialogInfo.show();

            dialogInfo.setOnDialogClosed(ev -> {
                stckHome.setEffect(null);
                InfoContainer.setVisible(false);
            });

            stckHome.setEffect(ConstantsPath.BOX_BLUR_EFFECT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void closeDialogAddUser() {
        if (dialogInfo != null) {
            dialogInfo.close();
        }
    }

     public void cleanup() {
        // Unregister this controller from EventBus when it's no longer needed
        EventBus.getDefault().unregister(this);
    }
}
