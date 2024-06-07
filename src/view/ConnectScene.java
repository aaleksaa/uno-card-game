package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.swing.text.View;


public class ConnectScene {
    private final VBox root;
    private final VBox vbConnect;
    private final Text txtTitle;
    private final TextField tfUsername;
    private final Button btnConnect;
    private Scene scene;

    public ConnectScene() {
        txtTitle = new Text("Welcome to UNO server!");
        tfUsername = new TextField();
        btnConnect = new Button("Connect");
        vbConnect = new VBox(20, txtTitle, tfUsername, btnConnect);
        root = new VBox(10, vbConnect);
        initializeScene();
    }

    private void initializeScene() {
        ViewUtil.setRootIdAndStyle(root, "root");
        vbConnect.setAlignment(Pos.CENTER);
        vbConnect.setId("vb-connect");
        tfUsername.setPromptText("Enter username...");

        scene = new Scene(root, ViewUtil.WINDOW_WIDTH, ViewUtil.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
    }

    public VBox getRoot() {
        return root;
    }

    public VBox getVbConnect() {
        return vbConnect;
    }

    public Text getTxtTitle() {
        return txtTitle;
    }

    public TextField getTfUsername() {
        return tfUsername;
    }

    public Button getBtnConnect() {
        return btnConnect;
    }

    public Scene getScene() {
        return scene;
    }
}
