package view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


public class ViewUtil {
    public static final String USERNAME_INPUT_MESSAGE = "Fill in username field!";
    public static final String LOBBY_INPUT_MESSAGE = "Fill in lobby name field!";
    public static final String LOBBY_SELECT_MESSAGE = "Lobby is not selected!";
    public static final String USER_SELECT_MESSAGE = "User is not selected!";
    public static final String NOT_ENOUGH_PLAYERS_MESSAGE = "You need at least 2 players to start the game!";
    public static final String PLAYERS_NOT_READY_MESSAGE = "Players are not ready!";
    public static final int WINDOW_WIDTH = 850;
    public static final int WINDOW_HEIGHT = 600;
    public static final int IMAGE_VIEW_WIDTH = 80;
    public static final int IMAGE_VIEW_HEIGHT = 150;

    public static void setTextLabel(Label lbl, String text) {
        Platform.runLater(() -> lbl.setText(text));
    }

    public static void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Uno Alert");
            alert.setHeaderText("Uno Alert");
            alert.setContentText(message);
            alert.show();
        });
    }

    public static void handleInvalidInput(TextField tf, String message) {
        showErrorAlert(message);
        Platform.runLater(tf::clear);
    }

    public static void addItemToList(ListView<String> lv, String item) {
        Platform.runLater(() -> lv.getItems().add(item));
    }

    public static void removeItemFromList(ListView<String> lv, String item) {
        Platform.runLater(() -> lv.getItems().remove(item));
    }

    public static Button createCardButton(String cardName) {
        ImageView iv = new ImageView(new Image("file:images/cards/" + cardName + ".png"));
        iv.setFitWidth(IMAGE_VIEW_WIDTH);
        iv.setFitHeight(IMAGE_VIEW_HEIGHT);

        Button btnCard = new Button();
        btnCard.setGraphic(iv);
        btnCard.setUserData(cardName);

        return btnCard;
    }

    public static void setListView(VBox vbPlayers, VBox vbUsers, ListView<String> lvPlayers, ListView<String> lvUsers) {
        lvPlayers.getItems().clear();
        vbPlayers.getChildren().add(1, lvPlayers);
        vbUsers.getChildren().add(1, lvUsers);
    }

    public static void removeListView(VBox vbPlayers, VBox vbUsers, ListView<String> lvPlayers, ListView<String> lvUsers) {
        vbPlayers.getChildren().remove(lvPlayers);
        vbUsers.getChildren().remove(lvUsers);
    }

    public static void setRootIdAndStyle(VBox root, String id) {
        root.setId(id);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
    }

    public static void insertLblMessage(VBox root, Label lblMessage) {
        lblMessage.setText("");
        root.getChildren().add(0, lblMessage);
    }
}
