package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameScene {
    private final VBox root;
    private final Label lblUsername;
    private final Label lblCurrentPlayer;
    private final Label lblCards;
    private final ImageView ivCurrent;
    private final ImageView ivBack;
    private final HBox hbDeck;
    private final HBox hbCards;
    private final VBox vbGame;
    private Scene scene;

    public GameScene() {
        lblUsername = new Label();
        lblCurrentPlayer = new Label();
        lblCards = new Label();
        ivCurrent = new ImageView();
        ivBack = new ImageView(new Image("file:images/cards/back.png"));
        hbDeck = new HBox(20, ivCurrent, ivBack);
        hbCards = new HBox(10);
        vbGame = new VBox(10, lblUsername, lblCurrentPlayer, lblCards, hbDeck, hbCards);
        root = new VBox(vbGame);
        initializeScene();
    }

    private void initializeScene() {
        root.setId("game");
        ivCurrent.setFitWidth(80);
        ivCurrent.setFitHeight(150);
        ivBack.setFitWidth(80);
        ivBack.setFitHeight(150);
        vbGame.setAlignment(Pos.CENTER);
        hbDeck.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);

        scene = new Scene(root, 850, 600);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    public VBox getRoot() {
        return root;
    }

    public Label getLblUsername() {
        return lblUsername;
    }

    public Label getLblCurrentPlayer() {
        return lblCurrentPlayer;
    }

    public Label getLblCards() {
        return lblCards;
    }

    public ImageView getIvCurrent() {
        return ivCurrent;
    }

    public ImageView getIvBack() {
        return ivBack;
    }

    public HBox getHbDeck() {
        return hbDeck;
    }

    public HBox getHbCards() {
        return hbCards;
    }

    public VBox getVbGame() {
        return vbGame;
    }
}
