package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The AdminLobbyScene class represents the user interface for the lobby admin in the Uno game application.
 * It allows admin to set private lobby, invite users, and starting the game.
 */
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

    /**
     * Constructs a AdminLobbyScene object, initializing the user interface elements.
     */
    public AdminLobbyScene() {
        lblLobbyName = new Label();

        lblPlayers = new Label("Players");
        btnLeave = new Button("Leave");
        btnPrivate = new Button();
        btnStart = new Button("Start");
        hbButtons = new HBox(5, btnLeave, btnPrivate, btnStart);
        vbPlayers = new VBox(10, lblPlayers, hbButtons);

        lblUsers = new Label("Online users");
        btnInvite = new Button("Invite");
        vbInvite = new VBox(10, lblUsers, btnInvite);
        hbLobby = new HBox(250, vbPlayers, vbInvite);

        root = new VBox(10, lblLobbyName, hbLobby);
        initializeScene();
    }

    /**
     * Initializes the scene by setting up the root VBox, alignment, and CSS styling.
     */
    private void initializeScene() {
        ViewUtil.setRootIdAndStyle(root, "start");
        lblLobbyName.setId("lbl2");
        hbLobby.setId("start-lobby");
        lblPlayers.setId("lbl1");
        lblUsers.setId("lbl1");

        scene = new Scene(root, ViewUtil.WINDOW_WIDTH, ViewUtil.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
    }

    /**
     * Returns the root VBox of the scene.
     *
     * @return the root VBox
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Returns the VBox containing player information and buttons.
     *
     * @return the VBox for players
     */
    public VBox getVbPlayers() {
        return vbPlayers;
    }

    /**
     * Returns the VBox containing user information and buttons.
     *
     * @return the VBox for invitations
     */
    public VBox getVbInvite() {
        return vbInvite;
    }

    /**
     * Returns the button for setting private lobby.
     *
     * @return the private lobby button
     */
    public Button getBtnPrivate() {
        return btnPrivate;
    }

    /**
     * Returns the button for inviting users.
     *
     * @return the invite button
     */
    public Button getBtnInvite() {
        return btnInvite;
    }

    /**
     * Returns the button for leaving the lobby.
     *
     * @return the leave button
     */
    public Button getBtnLeave() {
        return btnLeave;
    }

    /**
     * Returns the button for starting the game.
     *
     * @return the start button
     */
    public Button getBtnStart() {
        return btnStart;
    }

    /**
     * Returns the label for the lobby name.
     *
     * @return the lobby name label
     */
    public Label getLblLobbyName() {
        return lblLobbyName;
    }

    /**
     * Returns the Scene object representing the admin lobby scene.
     *
     * @return the Scene object
     */
    public Scene getScene() {
        return scene;
    }
}
