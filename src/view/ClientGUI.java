package view;

import client.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ClientGUI extends Application {
    private Client client;
    private Label lblMessage = new Label();

    @Override
    public void start(Stage stage) throws Exception {
        client = new Client(this);
        client.start();

        VBox root = new VBox(20);
        Label lblTitle = new Label("Welcome to UNO server!");
        TextField tfUsername = new TextField();
        Button btnJoin = new Button("Join");
        root.getChildren().addAll(lblTitle, tfUsername, btnJoin, lblMessage);

        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        tfUsername.setPromptText("Enter username...");

        Scene scene = new Scene(root, 300, 300);
        stage.setScene(scene);
        stage.show();

        btnJoin.setOnAction(e -> sendUsername(tfUsername));
    }

    private void sendUsername(TextField tf) {
        String username = tf.getText();

        if (username.isEmpty()) {
            showMessageLabel("Fill in username field!");
            tf.clear();
        } else {
            client.sendCommand("username " + username);
        }
    }

    public void showMessageLabel(String message) {
        Platform.runLater(() -> lblMessage.setText(message));
    }
}
