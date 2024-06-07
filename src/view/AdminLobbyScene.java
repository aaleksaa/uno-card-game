package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.swing.text.View;

public class AdminLobbyScene {
    private final VBox root;
    private final Label lblLobbyName;
    private final Label lblPlayers;
    private final HBox hbButtons;
    private final Button btnLeave;
    private final Button btnPrivate;
    private final Button btnStart;
    private final HBox hbLobby;
    private final VBox vbPlayers;
    private final VBox vbInvite;
    private final Button btnInvite;
    private final Label lblUsers;
    private Scene scene;


    public AdminLobbyScene() {
        lblLobbyName = new Label();

        lblPlayers = new Label("Players");
        btnLeave = new Button("Leave");
        btnPrivate = new Button("Set private");
        btnStart = new Button("Start");
        hbButtons = new HBox(5, btnLeave, btnPrivate, btnStart);
        vbPlayers = new VBox(10, lblPlayers, hbButtons);

        lblUsers = new Label("Online users");
        btnInvite = new Button("Invite");
        vbInvite = new VBox(10, lblUsers, btnInvite);
        hbLobby = new HBox(150, vbPlayers, vbInvite);

        root = new VBox(10, lblLobbyName, hbLobby);
        initializeScene();
    }

    private void initializeScene() {
        ViewUtil.setRootIdAndStyle(root, "start");
        lblLobbyName.setId("lblLobby");
        hbLobby.setId("start-lobby");
        lblPlayers.setId("lblMessage");
        lblUsers.setId("lblMessage");

        scene = new Scene(root, ViewUtil.WINDOW_WIDTH, ViewUtil.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
    }

    public VBox getRoot() {
        return root;
    }

    public Label getLblPlayers() {
        return lblPlayers;
    }

    public HBox getHbButtons() {
        return hbButtons;
    }

    public HBox getHbLobby() {
        return hbLobby;
    }

    public VBox getVbPlayers() {
        return vbPlayers;
    }

    public VBox getVbInvite() {
        return vbInvite;
    }

    public Label getLblUsers() {
        return lblUsers;
    }

    public boolean privateBtnText() {
        return btnPrivate.getText().equals("Set private");
    }

    public void setBtnPrivateText(String text) {
        btnPrivate.setText(text);
    }

    public Button getBtnPrivate() {
        return btnPrivate;
    }

    public Button getBtnInvite() {
        return btnInvite;
    }


    public Button getBtnLeave() {
        return btnLeave;
    }

    public Button getBtnStart() {
        return btnStart;
    }

    public Label getLblLobbyName() {
        return lblLobbyName;
    }

    public Scene getScene() {
        return scene;
    }
}
