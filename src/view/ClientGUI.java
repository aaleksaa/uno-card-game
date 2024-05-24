package view;

import client.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ClientGUI extends Application {
    private Client client;
    private final VBox root = new VBox(20);
    // Connect window components
    private final Label lblTitle = new Label("Welcome to UNO server");
    private final TextField tfUsername = new TextField();
    private final Button btnJoin = new Button("Join");
    private final Label lblMessage = new Label();
    // Start window components
    private final HBox hbStart = new HBox(20);
    private final Label lblWelcome = new Label();
    private final Label lblTest = new Label();
    private final VBox vbLobbies = new VBox();
    private final ListView<String> lvLobbies = new ListView<>();
    private final Button btnCreateLobby = new Button("Create lobby");
    private final Button btnJoinLobby = new Button("Join lobby");
    private final VBox vbUsers = new VBox();
    private final ListView<String> lvUsers = new ListView<>();


    @Override
    public void start(Stage stage) throws Exception {
        client = new Client(this);
        client.start();

        root.getChildren().addAll(lblTitle, tfUsername, btnJoin, lblMessage);

        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        tfUsername.setPromptText("Enter username...");

        Scene scene = new Scene(root, 650, 350);
        stage.setScene(scene);
        stage.show();

        btnJoin.setOnAction(e -> sendUsername(tfUsername));
    }

    private void sendUsername(TextField tf) {
        String username = tf.getText();

        if (username.isEmpty()) {
            showMessageLabel("Fill in username field!");
            tf.clear();
        } else
            client.sendCommand("username " + username);
    }


    public void setStartScene() {
        Platform.runLater(() -> {
            root.getChildren().clear();

            root.getChildren().addAll(lblWelcome, lblTest, hbStart);
            hbStart.getChildren().addAll(vbLobbies, vbUsers);
            lblWelcome.setText("Welcome " + client.getUsername());
            vbLobbies.getChildren().addAll(lvLobbies, btnCreateLobby, btnJoinLobby);
            vbUsers.getChildren().addAll(lvUsers);
        });
    }

    public void showTest(String message) {
        Platform.runLater(() -> lblTest.setText(message));
    }

    public void showMessageLabel(String message) {
        Platform.runLater(() -> lblMessage.setText(message));
    }

    public void addUserToList(String username) {
        Platform.runLater(() -> lvUsers.getItems().add(username));
    }
}
