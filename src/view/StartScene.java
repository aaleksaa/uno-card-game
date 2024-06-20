package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


/**
 * The StartScene class represents the initial scene where users can view current lobbies,
 * create a new lobby, or join an existing lobby in the UNO game application.
 */
public class StartScene {
    private final VBox root;
    private final VBox vbLobbies;
    private final Label lblLobby;
    private final ListView<String> lvLobbies;
    private final HBox hbCreateJoin;
    private final TextField tfCreate;
    private final Button btnCreateLobby;
    private final Button btnJoinLobby;
    private Scene scene;

    /**
     * Constructs a StartScene object, initializing the user interface elements.
     */
    public StartScene() {
        lblLobby = new Label("Current lobbies");
        lvLobbies = new ListView<>();
        tfCreate = new TextField();
        btnCreateLobby = new Button("Create lobby");
        btnJoinLobby = new Button("Join lobby");
        hbCreateJoin = new HBox(20, tfCreate, btnCreateLobby, btnJoinLobby);
        vbLobbies = new VBox(10, lblLobby, lvLobbies, hbCreateJoin);
        root = new VBox(10, vbLobbies);
        initializeScene();
    }

    /**
     * Initializes the scene by setting up the root VBox, alignment, and CSS styling.
     */
    private void initializeScene() {
        ViewUtil.setRootIdAndStyle(root, "start");
        vbLobbies.setAlignment(Pos.TOP_LEFT);
        tfCreate.setPromptText("Enter lobby name...");
        vbLobbies.setId("start-lobby");
        lblLobby.setId("lbl2");

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
     * Returns the ListView displaying the list of current lobbies.
     *
     * @return the ListView displaying the list of current lobbies
     */
    public ListView<String> getLvLobbies() {
        return lvLobbies;
    }

    /**
     * Returns the TextField for entering a new lobby name.
     *
     * @return the TextField for entering a new lobby name
     */
    public TextField getTfCreate() {
        return tfCreate;
    }

    /**
     * Returns the Button for creating a new lobby.
     *
     * @return the Button for creating a new lobby
     */
    public Button getBtnCreateLobby() {
        return btnCreateLobby;
    }

    /**
     * Returns the Button for joining an existing lobby.
     *
     * @return the Button for joining an existing lobby
     */
    public Button getBtnJoinLobby() {
        return btnJoinLobby;
    }

    /**
     * Returns the Scene object representing this start scene.
     *
     * @return the Scene object
     */
    public Scene getScene() {
        return scene;
    }
}
