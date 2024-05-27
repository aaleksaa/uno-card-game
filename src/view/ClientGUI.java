package view;

import client.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;



public class ClientGUI extends Application {
    private Client client;
    private final VBox root = new VBox(10);
    // Connect window components
    private final Text txtTitle = new Text("Welcome to UNO server!");
    private final TextField tfUsername = new TextField();
    private final Button btnConnect = new Button("Connect");
    private final Label lblConnectError = new Label();
    private final VBox vbConnect = new VBox(20, txtTitle, tfUsername, btnConnect, lblConnectError);
    // Start window components
    private final Label lblMessage = new Label();
    private final Label lblLobby = new Label("Current lobbies");
    private final ListView<String> lvLobbies = new ListView<>();
    private final Button btnCreateLobby = new Button("Create lobby");
    private final Button btnJoinLobby = new Button("Join lobby");
    private final TextField tfCreate = new TextField();
    private final HBox hbCreateJoinLobby = new HBox(20, tfCreate, btnCreateLobby, btnJoinLobby);
    private final Label lblStartError = new Label();
    private final VBox vbLobbies = new VBox(20, lblLobby, lvLobbies, hbCreateJoinLobby, lblStartError);
    // Lobby scene components
    private final Label lblLobbyName = new Label();
    private final Label lblUser = new Label("Online users");
    private final ListView<String> lvUsers = new ListView<>();
    private final VBox vbUsers = new VBox(5, lblUser, lvUsers);
    private final Label lblPlayers = new Label("Players");
    private final ListView<String> lvPlayers = new ListView<>();
    private final Button btnAdminLeaveLobby = new Button("Leave lobby");
    private final Button btnLeaveLobby = new Button("Leave lobby");
    private final Button btnSetReady = new Button("Set ready");
    private final HBox hbLobbyButtons = new HBox(5, btnLeaveLobby, btnSetReady);

    private final Button btnPrivate = new Button("Set private");
    private final Button btnStart = new Button("Start game");
    private final HBox hbAdminLobbyButtons = new HBox(5, btnAdminLeaveLobby, btnPrivate, btnStart);
    private final VBox vbPlayers = new VBox(10, lblPlayers, lvPlayers);
    private final Button btnInvite = new Button("Invite");
    private final VBox vbInvite = new VBox(10, vbUsers, btnInvite);
    private final HBox hbLobby = new HBox(150, vbPlayers, vbInvite);
    private final Label lblLobbyError = new Label();


    @Override
    public void start(Stage stage) throws Exception {
        client = new Client(this);
        client.start();

        root.getChildren().add(vbConnect);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setId("root");
        vbConnect.setId("vb-connect");
        vbConnect.setAlignment(Pos.CENTER);
        tfUsername.setPromptText("Enter username...");
        lblMessage.setId("lblMessage");
        lblLobbyError.setId("error");


        Scene scene = new Scene(root, 750, 500);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
        stage.setTitle("Uno");
        stage.setScene(scene);
        stage.show();

        btnConnect.setOnAction(e -> sendUsername(tfUsername));
    }

    private void sendUsername(TextField tf) {
        String username = tf.getText();

        if (username.isEmpty())
            showConnectErrorLabel("Fill in username field!");
        else
            client.sendCommand("username " + username);
    }


    public void setStartScene() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            root.setId("start");
            root.getChildren().addAll(lblMessage, vbLobbies);
            tfCreate.setPromptText("Enter lobby name...");
            btnCreateLobby.setOnAction(e -> createLobbyEvent(tfCreate));
            vbLobbies.setId("start-lobby");
            btnJoinLobby.setOnAction(e -> joinLobbyEvent(lvLobbies.getSelectionModel().getSelectedItem()));
            vbLobbies.setAlignment(Pos.TOP_LEFT);
            lblLobby.setId("lblLobby");
            lblStartError.setId("error");
        });
    }

    public void setLobbyScene() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            lblMessage.setText("");
            root.getChildren().addAll(lblMessage, lblLobbyName, hbLobby, lblLobbyError);
            lblLobbyName.setId("lblLobby");
            hbLobby.setId("start-lobby");
            btnInvite.setOnAction(e -> invitePlayerEvent(lvUsers.getSelectionModel().getSelectedItem()));
            vbPlayers.getChildren().add(hbLobbyButtons);
            lvPlayers.getItems().add(client.getUsername());
            btnLeaveLobby.setOnAction(e -> client.sendCommand("leave"));
        });
    }

    public void setAdminLobbyScene() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            lblMessage.setText("");
            root.getChildren().addAll(lblMessage, lblLobbyName, hbLobby, lblLobbyError);
            lblLobbyName.setId("lblLobby");
            hbLobby.setId("start-lobby");
            vbPlayers.getChildren().add(hbAdminLobbyButtons);
            btnInvite.setOnAction(e -> invitePlayerEvent(lvUsers.getSelectionModel().getSelectedItem()));
            lvPlayers.getItems().add(client.getUsername());
            btnPrivate.setOnAction(e -> privateLobbyEvent());
            lblPlayers.setId("lblMessage");
            lblUser.setId("lblMessage");
        });
    }

    public void showMessageLabel(String message) {
        Platform.runLater(() -> lblMessage.setText(message));
    }

    public void showConnectErrorLabel(String message) {
        Platform.runLater(() -> lblConnectError.setText(message));
    }


    public void connectEvent(String success, String username) {
        if (success.equals("true")) {
            setStartScene();
            client.setUsername(username);
        } else {
            showConnectErrorLabel("Username " + username + " is already taken! Try again.");
            tfUsername.clear();
        }
    }

    public ListView<String> getLvLobbies() {
        return lvLobbies;
    }

    public ListView<String> getLvUsers() {
        return lvUsers;
    }

    public ListView<String> getLvPlayers() {
        return lvPlayers;
    }

    public void addItemToList(ListView<String> listView, String item) {
        Platform.runLater(() -> listView.getItems().add(item));
    }


    public void handleViewUsers(String users) {
        String[] parts = users.split(" ");

        for (int i = 1; i < parts.length; i++)
            addItemToList(lvUsers, parts[i]);
    }

    public void handleViewLobbies(String lobbies) {
        String[] parts = lobbies.split(" ");

        for (int i = 1; i < parts.length; i++)
            addItemToList(lvLobbies, parts[i]);
    }

    public void handleCreateLobby(String success, String lobbyName) {
        if (success.equals("false")) {
            Platform.runLater(() -> {
                lblStartError.setText("Lobby " + lobbyName + " already exists!");
                tfCreate.clear();
            });
        } else {
            setAdminLobbyScene();
            lblLobbyName.setText(lobbyName);
        }
    }

    private void createLobbyEvent(TextField tf) {
        String lobbyName = tf.getText();

        if (lobbyName.isEmpty())
            lblStartError.setText("Fill in lobby name field!");
        else
            client.sendCommand("create_lobby " + lobbyName);
    }


    private void joinLobbyEvent(String lobbyName) {
        if (lobbyName == null)
            lblStartError.setText("Lobby is not selected!");
        else
            client.sendCommand("join " + lobbyName);
    }


    public void handleViewPlayers(String players) {
        String[] parts = players.split(" ");

        for (int i = 1; i < parts.length; i++)
            addItemToList(lvPlayers, parts[i]);
    }

    public void handleJoinLobby(String success, String lobbyName) {
        if (success.equals("false"))
            Platform.runLater(() -> lblStartError.setText(lobbyName + " is private!"));
        else {
            setLobbyScene();
            lblLobbyName.setText(lobbyName);
        }
    }

    private void privateLobbyEvent() {
        if (btnPrivate.getText().equals("Set private")) {
            client.sendCommand("set_private");
            btnPrivate.setText("Set public");
        } else {
            client.sendCommand("set_public");
            btnPrivate.setText("Set private");
        }
    }

    public void invitePlayerEvent(String username) {
        if (username == null)
            lblLobbyError.setText("User is not selected!");
        else
            client.sendCommand("invite " + lblLobbyName.getText() + " " + client.getUsername() + " " + username);
    }

    public void removePlayerFromList(String username) {
        Platform.runLater(() -> lvPlayers.getItems().remove(username));
    }

    public void showAlert(String lobbyName, String username) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("UNO INVITE");
            alert.setHeaderText("UNO INVITE");
            alert.setContentText(username + " has sent you an invite to " + lobbyName);

            ButtonType btnAccept = new ButtonType("Accept");
            ButtonType btnDecline = new ButtonType("Decline");

            alert.getButtonTypes().setAll(btnAccept, btnDecline);

            alert.showAndWait().ifPresent(response -> {
                if (response == btnAccept) {
                    client.sendCommand("accept " + lblLobbyName.getText());
                    alert.close();
                } else if (response == btnDecline) {
                    System.out.println("Invite declined");
                }
            });
        });
    }
}
