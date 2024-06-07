package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

    private void initializeScene() {
        ViewUtil.setRootIdAndStyle(root, "start");
        vbLobbies.setAlignment(Pos.TOP_LEFT);
        tfCreate.setPromptText("Enter lobby name...");
        vbLobbies.setId("start-lobby");
        lblLobby.setId("lblLobby");

        scene = new Scene(root, ViewUtil.WINDOW_WIDTH, ViewUtil.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
    }

    public VBox getRoot() {
        return root;
    }

    public VBox getVbLobbies() {
        return vbLobbies;
    }

    public Label getLblLobby() {
        return lblLobby;
    }

    public ListView<String> getLvLobbies() {
        return lvLobbies;
    }

    public HBox getHbCreateJoin() {
        return hbCreateJoin;
    }

    public TextField getTfCreate() {
        return tfCreate;
    }

    public Button getBtnCreateLobby() {
        return btnCreateLobby;
    }

    public Button getBtnJoinLobby() {
        return btnJoinLobby;
    }

    public Scene getScene() {
        return scene;
    }
}
