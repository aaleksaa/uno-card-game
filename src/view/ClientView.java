package view;

import client.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;


public class ClientView extends Application {
    private Stage primaryStage;
    private Client client;
    private final VBox root = new VBox(10);
    private ConnectScene connectScene;
    private StartScene startScene;
    private AdminLobbyScene adminLobbyScene;
    private LobbyScene lobbyScene;
    private final Label lblMessage = new Label();
    private final ListView<String> lvUsers = new ListView<>();
    private final ListView<String> lvPlayers = new ListView<>();
    private final Label lblLobbyError = new Label();
    private GameScene gameScene;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        client = new Client(this);

        connectScene = new ConnectScene();
        startScene = new StartScene();
        adminLobbyScene = new AdminLobbyScene();
        lobbyScene = new LobbyScene();
        gameScene = new GameScene();

        client.start();


        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setId("root");
        lblMessage.setId("lblMessage");
        lblLobbyError.setId("error");


        primaryStage.setTitle("Uno");
        primaryStage.setScene(connectScene.getScene());
        primaryStage.show();

        connectScene.getBtnConnect().setOnAction(e -> sendUsernameEvent(connectScene.getTfUsername().getText()));

        startScene.getBtnCreateLobby().setOnAction(e -> createLobbyEvent(startScene.getTfCreate().getText()));
        startScene.getBtnJoinLobby().setOnAction(e -> joinLobbyEvent(startScene.getLvLobbies().getSelectionModel().getSelectedItem()));

        adminLobbyScene.getBtnPrivate().setOnAction(e -> privateLobbyEvent());
        adminLobbyScene.getBtnInvite().setOnAction(e -> invitePlayerEvent(lvUsers.getSelectionModel().getSelectedItem(), adminLobbyScene.getLblLobbyName()));
        adminLobbyScene.getBtnStart().setOnAction(e -> client.sendRequest("start"));

        lobbyScene.getBtnInvite().setOnAction(e -> invitePlayerEvent(lvUsers.getSelectionModel().getSelectedItem(), lobbyScene.getLblLobbyName()));
        lobbyScene.getBtnReady().setOnAction(e -> setReadyEvent());
    }

    //-------------------------
    // BUTTON EVENTS
    //-------------------------

    private void sendUsernameEvent(String username) {
        if (username.isEmpty())
            setTextLabel(connectScene.getLblConnectError(), "Fill in username field!");
        else
            client.sendRequest("username " + username);
    }

    private void createLobbyEvent(String lobbyName) {
        if (lobbyName.isEmpty())
            setTextLabel(startScene.getLblStartError(), "Fill in lobby name field!");
        else
            client.sendRequest("create_lobby " + lobbyName);
    }


    private void privateLobbyEvent() {
        if (adminLobbyScene.privateBtnText()) {
            client.sendRequest("PRIVATE_LOBBY true");
            adminLobbyScene.setBtnPrivateText("Set public");
        } else {
            client.sendRequest("PRIVATE_LOBBY false");
            adminLobbyScene.setBtnPrivateText("Set private");
        }
    }

    private void invitePlayerEvent(String username, Label lblLobbyName) {
        if (username == null)
            setTextLabel(lblLobbyError, "User is not selected!");
        else
            client.sendRequest("invite " + lblLobbyName.getText() + " " + client.getUsername() + " " + username);
    }

    private void joinLobbyEvent(String lobbyName) {
        if (lobbyName == null)
            setTextLabel(startScene.getLblStartError(), "Lobby is not selected!");
        else
            client.sendRequest("JOIN " + lobbyName);
    }

    private void setReadyEvent() {
        if (lobbyScene.readyBtnText()) {
            client.sendRequest("READY true");
            lobbyScene.setBtnReadyText("Not ready");
        } else {
            client.sendRequest("READY false");
            lobbyScene.setBtnReadyText("Ready");
        }
    }

    //---------------------
    // HANDLE
    //---------------------

    public void handleError(String errorScene, String message) {
        if (errorScene.equals("START"))
            setTextLabel(startScene.getLblStartError(), message);
        else if (errorScene.equals("LOBBY"))
            setTextLabel(lblLobbyError, message);
    }

    public void handleInvalidInput(Label lbl, TextField tf, String message) {
        setTextLabel(lbl, message);
        Platform.runLater(tf::clear);
    }

    public void handleConnect(String success, String username) {
        if (success.equals("false"))
            handleInvalidInput(connectScene.getLblConnectError(), connectScene.getTfUsername(), "Username " + username + " is already taken!");
        else {
            setStartScene();
            client.setUsername(username);
        }
    }

    public void handleCreateLobby(String success, String lobbyName) {
        if (success.equals("false"))
            handleInvalidInput(startScene.getLblStartError(), startScene.getTfCreate(), "Lobby " + lobbyName + " already exists!");
        else
            setAdminLobbyScene(lobbyName);
    }

    public void setTextLabel(Label lbl, String text) {
        Platform.runLater(() -> lbl.setText(text));
    }


    //-----------------------------
    // SCENES
    //-----------------------------

    public void setStartScene() {
        Platform.runLater(() -> {
            startScene.setLabelMessage(lblMessage);
            primaryStage.setScene(startScene.getScene());
        });
    }

    public void setLobbyScene(String lobbyName) {
        Platform.runLater(() -> {
            lvPlayers.getItems().add(client.getUsername());
            setTextLabel(lobbyScene.getLblLobbyName(), lobbyName);
            lobbyScene.setListView(lvPlayers, lvUsers);
            lobbyScene.setLabel(lblMessage, lblLobbyError);
            primaryStage.setScene(lobbyScene.getScene());
        });
    }

    public void setAdminLobbyScene(String lobbyName) {
        Platform.runLater(() -> {
            lvPlayers.getItems().add(client.getUsername());
            setTextLabel(adminLobbyScene.getLblLobbyName(), lobbyName);
            adminLobbyScene.setListView(lvPlayers, lvUsers);
            adminLobbyScene.setLabel(lblMessage, lblLobbyError);

            primaryStage.setScene(adminLobbyScene.getScene());
        });
    }

    public void setGameScene() {
        Platform.runLater(() -> {
            setTextLabel(gameScene.getLblUsername(), client.getUsername());
            primaryStage.setScene(gameScene.getScene());
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
                gameScene.getHbCards().getChildren().add(btnCard);

                if (!btnCard.isDisabled())
                    btnCard.setOnAction(e -> {
                        client.sendRequest("play " + card);
                        gameScene.getHbCards().getChildren().remove(btnCard);
                    });
            }
        });
    }


    public void enableCards(String cards) {
        Platform.runLater(() -> {
            try {
                String[] parts = cards.split(" ");

                for (Node node : gameScene.getHbCards().getChildren()) {
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


    public void setCurrentCard(String current) {
        Platform.runLater(() -> gameScene.getIvCurrent().setImage(new Image("file:images/cards/" + current + ".png")));
    }

    public Label getLblMessage() {
        return lblMessage;
    }


    public ListView<String> getLvLobbies() {
        return startScene.getLvLobbies();
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

    public void handleViewItems(String itemType, String items) {
        String[] parts = items.split(" ");

        for (String part : parts) {
            if (itemType.equals("LOBBY"))
                addItemToList(startScene.getLvLobbies(), part);
            else if (itemType.equals("USER"))
                addItemToList(lvUsers, part);
            else
                addItemToList(lvPlayers, part);
        }
    }


    private void removeItemFromList(ListView<String> lv, String item) {
        Platform.runLater(() -> lv.getItems().remove(item));
    }

//    public void remove(String type, String item) {
//        if (type.equals("LOBBY"))
//            removeItemFromList(lvLobbies, item);
//    }


    public void disableCards() {
        Platform.runLater(() -> gameScene.getHbCards().getChildren().forEach(node -> node.setDisable(true)));
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
                } else if (response == btnGreen) {
                    client.sendRequest("change Green");
                    alert.close();
                }
            });
        });
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
                gameScene.getHbCards().getChildren().add(btnCard);
                btnCard.setOnAction(e -> {
                    client.sendRequest("play " + card);
                    gameScene.getHbCards().getChildren().remove(btnCard);
                });
            }
        });
    }

    public void showFinishAlert(String response) {
        Platform.runLater(() -> {
            String[] parts = response.split(" ", 2);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("GAME OVER");
            alert.setHeaderText("GAME OVER");
            alert.setContentText(parts[1]);


            ButtonType btnOK = new ButtonType("OK");

            alert.getButtonTypes().setAll(btnOK);

            alert.showAndWait().ifPresent(e -> {
                if (e == btnOK) {
                    setStartScene();
                    alert.close();
                }
            });
        });
    }

    public void showGameInfo(String typeInfo, String info) {
        if (typeInfo.equals("CARDS_NUM"))
            setTextLabel(gameScene.getLblCards(), info);
        else if (typeInfo.equals("CURR_PLAYER"))
            setTextLabel(gameScene.getLblCurrentPlayer(), info);
    }
}
