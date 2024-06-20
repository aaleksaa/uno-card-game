package view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


/**
 * The ViewUtil class provides utility methods for handling various UI tasks in a JavaFX application.
 */
public class ViewUtil {
    public static final String USERNAME_INPUT_MESSAGE = "Fill in username field!";
    public static final String LOBBY_INPUT_MESSAGE = "Fill in lobby name field!";
    public static final String INVALID_USERNAME_MESSAGE = "Space is not allowed in username!";
    public static final String INVALID_LOBBY_MESSAGE = "Space is not allowed in lobby name!";
    public static final String LOBBY_SELECT_MESSAGE = "Lobby is not selected!";
    public static final String USER_SELECT_MESSAGE = "User is not selected!";
    public static final String NOT_ENOUGH_PLAYERS_MESSAGE = "You need at least 2 players to start the game!";
    public static final String PLAYERS_NOT_READY_MESSAGE = "Players are not ready!";
    public static final int WINDOW_WIDTH = 850;
    public static final int WINDOW_HEIGHT = 600;
    public static final int IMAGE_VIEW_WIDTH = 80;
    public static final int IMAGE_VIEW_HEIGHT = 150;

    /**
     * Sets the text of a Label on the JavaFX Application Thread.
     *
     * @param lbl the Label to set the text for
     * @param text the text to set
     */
    public static void setTextLabel(Label lbl, String text) {
        Platform.runLater(() -> lbl.setText(text));
    }

    /**
     * Shows an error alert with the specified message.
     *
     * @param message the message to display in the alert
     */
    public static void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Uno Alert");
            alert.setHeaderText("Uno Alert");
            alert.setContentText(message);
            alert.show();
        });
    }

    /**
     * Handles invalid input by showing an error alert and clearing the TextField.
     *
     * @param tf the TextField to clear
     * @param message the message to display in the alert
     */
    public static void handleInvalidInput(TextField tf, String message) {
        showErrorAlert(message);
        Platform.runLater(tf::clear);
    }

    /**
     * Adds an item to a ListView on the JavaFX Application Thread.
     *
     * @param lv the ListView to add the item to
     * @param item the item to add
     */
    public static void addItemToList(ListView<String> lv, String item) {
        Platform.runLater(() -> lv.getItems().add(item));
    }

    /**
     * Removes an item from a ListView on the JavaFX Application Thread.
     *
     * @param lv the ListView to remove the item from
     * @param item the item to remove
     */
    public static void removeItemFromList(ListView<String> lv, String item) {
        Platform.runLater(() -> lv.getItems().remove(item));
    }

    /**
     * Creates a Button representing a card with the specified card name.
     * The button will display an image of the card.
     *
     * @param cardName the name of the card
     * @return the created Button
     */
    public static Button createCardButton(String cardName) {
        ImageView iv = new ImageView(new Image("file:images/" + cardName + ".png"));
        iv.setFitWidth(IMAGE_VIEW_WIDTH);
        iv.setFitHeight(IMAGE_VIEW_HEIGHT);

        Button btnCard = new Button();
        btnCard.setGraphic(iv);
        btnCard.setUserData(cardName);

        return btnCard;
    }

    /**
     * Sets the ListView for players and users in the specified VBox containers.
     *
     * @param vbPlayers the VBox for players
     * @param vbUsers the VBox for users
     * @param lvPlayers the ListView for players
     * @param lvUsers the ListView for users
     */
    public static void setListView(VBox vbPlayers, VBox vbUsers, ListView<String> lvPlayers, ListView<String> lvUsers) {
        lvPlayers.getItems().clear();
        vbPlayers.getChildren().add(1, lvPlayers);
        vbUsers.getChildren().add(1, lvUsers);
    }

    /**
     * Removes the ListView for players and users from the specified VBox containers.
     *
     * @param vbPlayers the VBox for players
     * @param vbUsers the VBox for users
     * @param lvPlayers the ListView for players
     * @param lvUsers the ListView for users
     */
    public static void removeListView(VBox vbPlayers, VBox vbUsers, ListView<String> lvPlayers, ListView<String> lvUsers) {
        vbPlayers.getChildren().remove(lvPlayers);
        vbUsers.getChildren().remove(lvUsers);
    }

    /**
     * Sets the ID and style of the root VBox.
     *
     * @param root the root VBox
     * @param id the ID to set
     */
    public static void setRootIdAndStyle(VBox root, String id) {
        root.setId(id);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
    }

    /**
     * Inserts a message Label at the top of the root VBox.
     *
     * @param root the root VBox
     * @param lblMessage the Label to insert
     */
    public static void insertLblMessage(VBox root, Label lblMessage) {
        lblMessage.setText("");
        root.getChildren().add(0, lblMessage);
    }
}
