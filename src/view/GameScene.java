package view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private final Button btnDraw;
    private final ImageView ivBack;
    private final HBox hbDeck;
    private final HBox hbCards;
    private final VBox vbGame;
    private final ScrollPane spCards;

    private Scene scene;

    public GameScene() {
        lblUsername = new Label();
        lblCurrentPlayer = new Label();
        lblCards = new Label();
        ivCurrent = new ImageView();
        ivBack = new ImageView(new Image("file:images/cards/back.png"));
        btnDraw = new Button();
        btnDraw.setGraphic(ivBack);
        hbDeck = new HBox(20, ivCurrent, btnDraw);
        hbCards = new HBox(10);

        spCards = new ScrollPane(hbCards);
        spCards.setFitToHeight(true);
        spCards.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        vbGame = new VBox(10, lblUsername, lblCurrentPlayer, lblCards, hbDeck, spCards);
        root = new VBox(vbGame);
        initializeScene();
    }

    private void initializeScene() {
        ViewUtil.setRootIdAndStyle(root, "game");
        ivCurrent.setFitWidth(ViewUtil.IMAGE_VIEW_WIDTH);
        ivCurrent.setFitHeight(ViewUtil.IMAGE_VIEW_HEIGHT);
        ivBack.setFitWidth(ViewUtil.IMAGE_VIEW_WIDTH);
        ivBack.setFitHeight(ViewUtil.IMAGE_VIEW_HEIGHT);
        vbGame.setAlignment(Pos.CENTER);
        hbDeck.setAlignment(Pos.CENTER);

        scene = new Scene(root, ViewUtil.WINDOW_WIDTH, ViewUtil.WINDOW_HEIGHT);
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

    public void disableBtnDraw(boolean disable) {
        Platform.runLater(() -> btnDraw.setDisable(disable));
    }

    public Button getBtnDraw() {
        return btnDraw;
    }

    public VBox getVbGame() {
        return vbGame;
    }
}
