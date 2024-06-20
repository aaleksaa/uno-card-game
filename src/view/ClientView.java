package view;

import client_server.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientView extends Application {
    private Stage primaryStage;
    private Client client;
    private ConnectScene connectScene;
    private StartScene startScene;
    private AdminLobbyScene adminLobbyScene;
    private LobbyScene lobbyScene;
    private GameScene gameScene;
    private final Label lblMessage = new Label();
    private final ListView<String> lvUsers = new ListView<>();
    private final ListView<String> lvPlayers = new ListView<>();

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
        lblMessage.setId("lbl1");
        primaryStage.setTitle("Uno");
        primaryStage.setScene(connectScene.getScene());
        primaryStage.getScene().getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
        primaryStage.show();

        setEventsOnButtons();
    }

    public Label getLblMessage() {
        return lblMessage;
    }

    private void setEventsOnButtons() {
        connectScene.getBtnConnect().setOnAction(e -> sendUsernameEvent(connectScene.getTfUsername().getText()));

        startScene.getBtnCreateLobby().setOnAction(e -> createLobbyEvent(startScene.getTfCreate().getText()));
        startScene.getBtnJoinLobby().setOnAction(e -> joinLobbyEvent(startScene.getLvLobbies().getSelectionModel().getSelectedItem()));

        adminLobbyScene.getBtnPrivate().setOnAction(e -> privateLobbyEvent());
        adminLobbyScene.getBtnInvite().setOnAction(e -> invitePlayerEvent(lvUsers.getSelectionModel().getSelectedItem(), adminLobbyScene.getLblLobbyName()));
        adminLobbyScene.getBtnStart().setOnAction(e -> client.sendRequest("START"));
        adminLobbyScene.getBtnLeave().setOnAction(e -> client.sendRequest("LEAVE"));

        lobbyScene.getBtnLeave().setOnAction(e -> client.sendRequest("LEAVE"));
        lobbyScene.getBtnInvite().setOnAction(e -> invitePlayerEvent(lvUsers.getSelectionModel().getSelectedItem(), lobbyScene.getLblLobbyName()));
        lobbyScene.getBtnReady().setOnAction(e -> setReadyEvent());

        gameScene.getBtnDraw().setOnAction(e -> client.sendRequest("DRAW"));

        primaryStage.setOnCloseRequest(e -> disconnectEvent());
    }

    private void disconnectEvent() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Please confirm your action.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                client.sendRequest("DISCONNECT");
                primaryStage.close();
            }
        });
    }

    private void sendUsernameEvent(String username) {
        if (username.isEmpty())
            ViewUtil.showErrorAlert(ViewUtil.USERNAME_INPUT_MESSAGE);
        else if (username.contains(" "))
            ViewUtil.handleInvalidInput(connectScene.getTfUsername(), ViewUtil.INVALID_USERNAME_MESSAGE);
        else
            client.sendRequest("CONNECT " + username);
    }

    private void createLobbyEvent(String lobbyName) {
        if (lobbyName.isEmpty())
            ViewUtil.showErrorAlert(ViewUtil.LOBBY_INPUT_MESSAGE);
        else if (lobbyName.contains(" "))
            ViewUtil.handleInvalidInput(startScene.getTfCreate(), ViewUtil.INVALID_LOBBY_MESSAGE);
        else
            client.sendRequest("CREATE_LOBBY " + lobbyName);
    }

    private void joinLobbyEvent(String lobbyName) {
        if (lobbyName == null)
            ViewUtil.showErrorAlert(ViewUtil.LOBBY_SELECT_MESSAGE);
        else
            client.sendRequest("JOIN " + lobbyName);
    }

    private void setReadyEvent() {
        if (lobbyScene.getBtnReady().getText().equals("Ready")) {
            client.sendRequest("READY true");
            lobbyScene.getBtnReady().setText("Not ready");
        } else {
            client.sendRequest("READY false");
            lobbyScene.getBtnReady().setText("Ready");
        }
    }

    private void privateLobbyEvent() {
        if (adminLobbyScene.getBtnPrivate().getText().equals("Set private")) {
            client.sendRequest("PRIVATE_LOBBY true");
            adminLobbyScene.getBtnPrivate().setText("Set public");
        } else {
            client.sendRequest("PRIVATE_LOBBY false");
            adminLobbyScene.getBtnPrivate().setText("Set private");
        }
    }

    private void invitePlayerEvent(String username, Label lblLobbyName) {
        if (username == null)
            ViewUtil.showErrorAlert(ViewUtil.USER_SELECT_MESSAGE);
        else
            client.sendRequest("INVITE " + lblLobbyName.getText() + " " + client.getUsername() + " " + username);
    }

    public void handleConnect(String success, String username) {
        if (success.equals("false"))
            ViewUtil.handleInvalidInput(connectScene.getTfUsername(), "Username " + username + " is already taken!");
        else {
            setStartScene();
            client.setUsername(username);
        }
    }

    public void handleCreateLobby(String success, String lobbyName) {
        if (success.equals("false"))
            ViewUtil.handleInvalidInput(startScene.getTfCreate(), "Lobby " + lobbyName + " already exists!");
        else
            setAdminLobbyScene(lobbyName, false);
    }

    public void setStartScene() {
        Platform.runLater(() -> {
            startScene.getTfCreate().clear();

            ViewUtil.removeListView(adminLobbyScene.getVbPlayers(), adminLobbyScene.getVbInvite(), lvPlayers, lvUsers);
            ViewUtil.removeListView(lobbyScene.getVbPlayers(), lobbyScene.getVbInvite(), lvPlayers, lvUsers);

            gameScene.getHbCards().getChildren().clear();
            ViewUtil.insertLblMessage(startScene.getRoot(), lblMessage);
            primaryStage.getScene().getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
            primaryStage.setScene(startScene.getScene());
        });
    }

    public void setLobbyScene(String lobbyName) {
        Platform.runLater(() -> {
            ViewUtil.setTextLabel(lobbyScene.getLblLobbyName(), lobbyName);
            ViewUtil.setListView(lobbyScene.getVbPlayers(), lobbyScene.getVbInvite(), lvPlayers, lvUsers);
            lvPlayers.getItems().add(client.getUsername());
            ViewUtil.insertLblMessage(lobbyScene.getRoot(), lblMessage);
            lobbyScene.getBtnReady().setText("Ready");
            primaryStage.getScene().getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
            primaryStage.setScene(lobbyScene.getScene());
        });
    }

    public void setAdminLobbyScene(String lobbyName, boolean changeToAdmin) {
        Platform.runLater(() -> {
            if (changeToAdmin)
                ViewUtil.removeListView(lobbyScene.getVbPlayers(), lobbyScene.getVbInvite(), lvPlayers, lvUsers);

            ViewUtil.setTextLabel(adminLobbyScene.getLblLobbyName(), lobbyName);
            ViewUtil.setListView(adminLobbyScene.getVbPlayers(), adminLobbyScene.getVbInvite(), lvPlayers, lvUsers);
            lvPlayers.getItems().add(client.getUsername());
            ViewUtil.insertLblMessage(adminLobbyScene.getRoot(), lblMessage);
            adminLobbyScene.getBtnPrivate().setText("Set private");
            primaryStage.getScene().getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
            primaryStage.setScene(adminLobbyScene.getScene());
        });
    }

    public void setGameScene() {
        Platform.runLater(() -> {
            ViewUtil.setTextLabel(gameScene.getLblUsername(), client.getUsername());
            primaryStage.getScene().getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
            primaryStage.setScene(gameScene.getScene());
        });
    }

    public void setCards(String cards) {
        Platform.runLater(() -> {
            String[] parts = cards.split(" ");

            for (String part : parts) {

                Button btnCard = ViewUtil.createCardButton(part);
                gameScene.getHbCards().getChildren().add(btnCard);

                btnCard.setOnAction(e -> {
                    client.sendRequest("PLAY " + btnCard.getUserData());
                    gameScene.getHbCards().getChildren().remove(btnCard);
                });
            }
        });
    }

    public void enableCards(String cards) {
        Platform.runLater(() -> {
            String[] parts = cards.split(" ");

            for (Node node : gameScene.getHbCards().getChildren()) {
                Button btnCard = (Button) node;

                for (String part : parts)
                    if (btnCard.getUserData().equals(part)) {
                        btnCard.setDisable(false);
                        break;
                    }
            }
        });
    }

    public void handleAddItems(String itemType, String items) {
        String[] parts = items.split(" ");

        for (String part : parts)
            if (!part.isEmpty())
                switch (itemType) {
                    case "LOBBY":
                        ViewUtil.addItemToList(startScene.getLvLobbies(), part);
                        break;
                    case "USER":
                        ViewUtil.addItemToList(lvUsers, part);
                        break;
                    case "PLAYER":
                        ViewUtil.addItemToList(lvPlayers, part);
                        break;
                }
    }

    public void handleRemoveItem(String itemType, String item) {
        switch (itemType) {
            case "LOBBY":
                ViewUtil.removeItemFromList(startScene.getLvLobbies(), item);
                break;
            case "USER":
                ViewUtil.removeItemFromList(lvUsers, item);
                break;
            case "PLAYER":
                ViewUtil.removeItemFromList(lvPlayers, item);
                break;
        }
    }

    public void setCurrentCard(String current) {
        Platform.runLater(() -> gameScene.getIvCurrent().setImage(new Image("file:images/" + current + ".png")));
    }

    public void disableCards() {
        Platform.runLater(() -> gameScene.disableCards());
    }

    public void enableDrawCard() {
        Platform.runLater(() -> gameScene.getBtnDraw().setDisable(false));
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
                    client.sendRequest("CHANGE RED");
                    alert.close();
                } else if (response == btnYellow) {
                    client.sendRequest("CHANGE YELLOW");
                    alert.close();
                } else if (response == btnBlue) {
                    client.sendRequest("CHANGE BLUE");
                    alert.close();
                } else {
                    client.sendRequest("CHANGE GREEN");
                    alert.close();
                }
            });
        });
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
                    client.sendRequest("ACCEPT " + lobbyName);
                    alert.close();
                } else {
                    client.sendRequest("DECLINE " + lobbyName);
                    alert.close();
                }
            });
        });
    }

    public void showFinishAlert(String finishText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("GAME OVER");
            alert.setHeaderText("GAME OVER");
            alert.setContentText(finishText);

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    setStartScene();
                    alert.close();
                }
            });
        });
    }

    public void showGameInfo(String typeInfo, String info) {
        if (typeInfo.equals("CARDS_NUM"))
            ViewUtil.setTextLabel(gameScene.getLblCards(), info);
        else
            ViewUtil.setTextLabel(gameScene.getLblCurrentPlayer(), info);
    }
}
