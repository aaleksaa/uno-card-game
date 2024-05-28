package view;

import client.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;


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
    private final Button btnReady = new Button("Ready");
    private final HBox hbLobbyButtons = new HBox(5, btnLeaveLobby, btnReady);

    private final Button btnPrivate = new Button("Set private");
    private final Button btnStart = new Button("Start game");
    private final HBox hbAdminLobbyButtons = new HBox(5, btnAdminLeaveLobby, btnPrivate, btnStart);
    private final VBox vbPlayers = new VBox(10, lblPlayers, lvPlayers);
    private final Button btnInvite = new Button("Invite");
    private final VBox vbInvite = new VBox(10, vbUsers, btnInvite);
    private final HBox hbLobby = new HBox(150, vbPlayers, vbInvite);
    private final Label lblLobbyError = new Label();
    // Game scene components
    private final Label lblUsername = new Label();
    private final Label lblGame = new Label();
    private final ImageView ivCurrent = new ImageView();
    private final ImageView ivBack = new ImageView(new Image("file:images/cards/back.png"));
    private final HBox hbDeck = new HBox(20, ivCurrent, ivBack);
    private final HBox hbCards = new HBox(10);
    private final VBox vbGame = new VBox(10, lblUsername, lblGame, hbDeck, hbCards);


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


        Scene scene = new Scene(root, 850, 600);
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
            client.sendRequest("username " + username);
    }


    public void setStartScene() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            root.setId("start");
            root.getChildren().addAll(lblMessage, vbLobbies);
            tfCreate.setPromptText("Enter lobby name...");
            btnCreateLobby.setOnAction(e -> createLobbyEvent(tfCreate));
            vbLobbies.setId("start-lobby");
            btnJoinLobby.setOnAction(e -> client.sendRequest("join " + lvLobbies.getSelectionModel().getSelectedItem()));
            vbLobbies.setAlignment(Pos.TOP_LEFT);
            lblLobby.setId("lblLobby");
            lblStartError.setId("error");
        });
    }

    public void setCards(String cards) {
        Platform.runLater(() -> {
            String[] parts = cards.split(" ");
            for (int i = 1; i < parts.length; i++) {
                ImageView iv = new ImageView(new Image("file:images/cards/" + parts[i] + ".png"));
                iv.setFitWidth(80);
                iv.setFitHeight(150);
                Button btnCard = new Button();
                btnCard.setGraphic(iv);
                String card = parts[i];
                hbCards.getChildren().add(btnCard);

                if (!btnCard.isDisabled())
                    btnCard.setOnAction(e -> {
                        client.sendRequest("play " + card);
                        hbCards.getChildren().remove(btnCard);
                    });
            }
        });
    }

    public void setLobbyScene(String lobbyName) {
        Platform.runLater(() -> {
            root.getChildren().clear();
            lblMessage.setText("");
            root.getChildren().addAll(lblMessage, lblLobbyName, hbLobby, lblLobbyError);
            lblLobbyName.setId("lblLobby");
            hbLobby.setId("start-lobby");
            btnInvite.setOnAction(e -> invitePlayerEvent(lvUsers.getSelectionModel().getSelectedItem()));
            vbPlayers.getChildren().add(hbLobbyButtons);
            lvPlayers.getItems().add(client.getUsername());
            btnLeaveLobby.setOnAction(e -> client.sendRequest("leave"));
            lblLobbyName.setText(lobbyName);
            btnReady.setOnAction(e -> setReadyEvent());
        });
    }

    public void enableCards(String cards) {
        Platform.runLater(() -> {
            try {
                String[] parts = cards.split(" ");

                for (Node node : hbCards.getChildren()) {
                    Button btn = (Button) node;
                    ImageView iv = (ImageView) btn.getGraphic();
                    Image img = iv.getImage();
                    String url = img.getUrl();
                    URL imageURL = new URL(url);
                    String fileName = Paths.get(imageURL.getPath()).getFileName().toString();
                    String name = fileName.substring(0, fileName.lastIndexOf('.'));

                    for (int i = 1; i < parts.length; i++)
                        if (name.equals(parts[i])) {
                            btn.setDisable(false);
                            break;
                        }
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
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
            btnStart.setOnAction(e -> client.sendRequest("start"));
        });
    }

    public void setGameScene() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            root.setId("game");
            root.getChildren().add(vbGame);
            ivCurrent.setFitWidth(80);
            ivCurrent.setFitHeight(150);
            ivBack.setFitWidth(80);
            ivBack.setFitHeight(150);
            vbGame.setAlignment(Pos.CENTER);
            root.setAlignment(Pos.CENTER);
            lblUsername.setText(client.getUsername());
        });
    }

    public void setCurrent(String current) {
        Platform.runLater(() -> ivCurrent.setImage(new Image("file:images/cards/" + current + ".png")));
    }

    public void showMessageLabel(String message) {
        Platform.runLater(() -> lblMessage.setText(message));
    }

    public void showConnectErrorLabel(String message) {
        Platform.runLater(() -> lblConnectError.setText(message));
    }

    public void showErrorLabel(Label lbl, String message) {
        Platform.runLater(() -> lbl.setText(message));
    }

    public Label getLblStartError() {
        return lblStartError;
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
            client.sendRequest("create_lobby " + lobbyName);
    }

    public void disableCards() {
        Platform.runLater(() -> {
            for (Node node : hbCards.getChildren()) {
                Button btn = (Button) node;
                btn.setDisable(true);
            }
        });
    }


    public void handleViewPlayers(String players) {
        String[] parts = players.split(" ");

        for (int i = 1; i < parts.length; i++)
            addItemToList(lvPlayers, parts[i]);
    }

    private void privateLobbyEvent() {
        if (btnPrivate.getText().equals("Set private")) {
            client.sendRequest("set_private");
            btnPrivate.setText("Set public");
        } else {
            client.sendRequest("set_public");
            btnPrivate.setText("Set private");
        }
    }

    public void invitePlayerEvent(String username) {
        if (username == null)
            lblLobbyError.setText("User is not selected!");
        else
            client.sendRequest("invite " + lblLobbyName.getText() + " " + client.getUsername() + " " + username);
    }

    public void showChangeColorAlert() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("UNO CHANGE COLOR");
            alert.setHeaderText("UNO CHANGE COLOR");
            alert.setContentText("Set new color");


            ButtonType btnRed = new ButtonType("Red");
            ButtonType btnYellow = new ButtonType("Yellow");
            ButtonType btnBlue = new ButtonType("Blue");
            ButtonType btnGreen = new ButtonType("Green");

            alert.getButtonTypes().setAll(btnRed, btnYellow, btnBlue, btnGreen);

            alert.showAndWait().ifPresent(response -> {
                if (response == btnRed) {
                    client.sendRequest("change Red");
                    alert.close();
                } else if (response == btnYellow) {
                    client.sendRequest("change Yellow");
                    alert.close();
                } else if (response == btnBlue) {
                    client.sendRequest("change Blue");
                    alert.close();
                } else if (response == btnGreen){
                    client.sendRequest("change Green");
                    alert.close();
                }
            });
        });
    }

    private void setReadyEvent() {
        if (btnReady.getText().equals("Ready")) {
            client.sendRequest("ready true");
            btnReady.setText("Not ready");
        } else {
            client.sendRequest("ready false");
            btnReady.setText("Ready");
        }
    }



    public void removePlayerFromList(String username) {
        Platform.runLater(() -> lvPlayers.getItems().remove(username));
    }

    public void showInviteAlert(String lobbyName, String username) {
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
                    client.sendRequest("accept " + lobbyName);
                    alert.close();
                } else if (response == btnDecline) {
                    client.sendRequest("decline " + lobbyName);
                    alert.close();
                }
            });
        });
    }

    public void addCards(String cards) {
        Platform.runLater(() -> {
            String[] parts = cards.split(" ");
            for (int i = 1; i < parts.length; i++) {
                ImageView iv = new ImageView(new Image("file:images/cards/" + parts[i] + ".png"));
                iv.setFitWidth(80);
                iv.setFitHeight(150);
                Button btnCard = new Button();
                btnCard.setGraphic(iv);
                String card = parts[i];
                hbCards.getChildren().add(btnCard);
                btnCard.setOnAction(e -> {
                    client.sendRequest("play " + card);
                    hbCards.getChildren().remove(btnCard);
                });
            }
        });
    }

    public void gameStatus(String status) {
        Platform.runLater(() -> {
            String info = status.substring(status.indexOf(' '));
            lblGame.setText(info);
        });
    }
}
