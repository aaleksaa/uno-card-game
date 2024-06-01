package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class LobbyScene {
    private final VBox root;
    private final Label lblLobbyName;
    private final Label lblPlayers;
    private final HBox hbButtons;
    private final Button btnLeave;
    private final Button btnReady;
    private final Button btnInvite;
    private final HBox hbLobby;
    private final VBox vbInvite;
    private final VBox vbPlayers;
    private final Label lblUsers;
    private Scene scene;

    public LobbyScene() {
        lblLobbyName = new Label();

        lblPlayers = new Label("Players");
        btnLeave = new Button("Leave");
        btnReady = new Button("Ready");
        hbButtons = new HBox(5, btnLeave, btnReady);
        vbPlayers = new VBox(10, lblPlayers, hbButtons);

        lblUsers = new Label("Online users");
        btnInvite = new Button("Invite");
        vbInvite = new VBox(10, lblUsers, btnInvite);
        hbLobby = new HBox(150, vbPlayers, vbInvite);

        root = new VBox(10, lblLobbyName, hbLobby);
        initializeScene();
    }

    public void setLabel(Label lblMessage, Label lblError) {
        lblMessage.setText("");
        lblError.setText("");
        root.getChildren().add(0, lblMessage);
        vbPlayers.getChildren().add(2, lblError);
    }


    public void setListView(ListView<String> lvPlayers, ListView<String> lvUsers) {
        vbPlayers.getChildren().add(1, lvPlayers);
        vbInvite.getChildren().add(1, lvUsers);
    }

    public Button getBtnInvite() {
        return btnInvite;
    }

    public Button getBtnReady() {
        return btnReady;
    }

    public boolean readyBtnText() {
        return btnReady.getText().equals("Ready");
    }

    public void setBtnReadyText(String text) {
        btnReady.setText(text);
    }

    private void initializeScene() {
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setId("start");
        lblLobbyName.setId("lblLobby");
        hbLobby.setId("start-lobby");
        lblPlayers.setId("lblMessage");
        lblUsers.setId("lblMessage");

        scene = new Scene(root, 850, 600);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
    }

    public Label getLblLobbyName() {
        return lblLobbyName;
    }

    public Scene getScene() {
        return scene;
    }
}
