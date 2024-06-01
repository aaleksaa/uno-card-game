package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class ConnectScene {
    private final VBox root;
    private final VBox vbConnect;
    private final Text txtTitle;
    private final TextField tfUsername;
    private final Button btnConnect;
    private final Label lblConnectError;
    private Scene scene;

    public ConnectScene() {
        txtTitle = new Text("Welcome to UNO server!");
        tfUsername = new TextField();
        btnConnect = new Button("Connect");
        lblConnectError = new Label();
        vbConnect = new VBox(20, txtTitle, tfUsername, btnConnect, lblConnectError);
        root = new VBox(10, vbConnect);
        initializeScene();
    }

    private void initializeScene() {
        root.setId("root");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        vbConnect.setAlignment(Pos.CENTER);
        vbConnect.setId("vb-connect");
        tfUsername.setPromptText("Enter username...");

        scene = new Scene(root, 850, 600);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
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

    public Label getLblConnectError() {
        return lblConnectError;
    }

    public Scene getScene() {
        return scene;
    }
}
