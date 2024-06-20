package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;


/**
 * The ConnectScene class represents the initial connection scene in the UNO game application.
 * It allows users to input their username and connect to the server.
 */
public class ConnectScene {
    private final VBox root;
    private final VBox vbConnect;
    private final Label lblTitle;
    private final TextField tfUsername;
    private final Button btnConnect;
    private Scene scene;

    /**
     * Constructs a ConnectScene object, initializing the user interface elements.
     */
    public ConnectScene() {
        lblTitle = new Label("Welcome to UNO server!");
        tfUsername = new TextField();
        btnConnect = new Button("Connect");
        vbConnect = new VBox(20, lblTitle, tfUsername, btnConnect);
        root = new VBox(10, vbConnect);
        initializeScene();
    }

    /**
     * Initializes the scene by setting up the root VBox, alignment, and CSS styling.
     */
    private void initializeScene() {
        ViewUtil.setRootIdAndStyle(root, "connect");
        vbConnect.setAlignment(Pos.CENTER);
        tfUsername.setPromptText("Enter username...");

        scene = new Scene(root, ViewUtil.WINDOW_WIDTH, ViewUtil.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
    }

    /**
     * Returns the TextField for entering the username.
     *
     * @return the TextField for entering the username
     */
    public TextField getTfUsername() {
        return tfUsername;
    }

    /**
     * Returns the Button for connecting to the server.
     *
     * @return the Button for connecting to the server
     */
    public Button getBtnConnect() {
        return btnConnect;
    }

    /**
     * Returns the Scene object representing this connection scene.
     *
     * @return the Scene object
     */
    public Scene getScene() {
        return scene;
    }
}
