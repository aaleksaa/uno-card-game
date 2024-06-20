package view;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The GameScene class represents the main game interface for the UNO game.
 * It includes components for displaying the current player's username,
 * the card currently in play, and the player's hand of cards.
 */
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

    /**
     * Constructs a GameScene object, initializing the user interface elements.
     */
    public GameScene() {
        lblUsername = new Label();
        lblCurrentPlayer = new Label();
        lblCards = new Label();
        ivCurrent = new ImageView();
        ivBack = new ImageView(new Image("file:images/back.png"));
        btnDraw = new Button();
        btnDraw.setGraphic(ivBack);
        hbDeck = new HBox(20, ivCurrent, btnDraw);
        hbCards = new HBox(10);

        spCards = new ScrollPane(hbCards);
        spCards.setFitToHeight(true);
        spCards.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        vbGame = new VBox(15, lblUsername, lblCurrentPlayer, lblCards, hbDeck, spCards);
        root = new VBox(vbGame);
        initializeScene();
    }

    /**
     * Initializes the scene by setting up the root VBox, alignment, and CSS styling.
     */
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

    /**
     * Returns the Scene object representing the game scene.
     *
     * @return the Scene object
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Returns the label displaying the username.
     *
     * @return the username label
     */
    public Label getLblUsername() {
        return lblUsername;
    }

    /**
     * Returns the label displaying the current player.
     *
     * @return the current player label
     */
    public Label getLblCurrentPlayer() {
        return lblCurrentPlayer;
    }

    /**
     * Returns the label displaying the card information.
     *
     * @return the cards label
     */
    public Label getLblCards() {
        return lblCards;
    }

    /**
     * Returns the ImageView displaying the current card.
     *
     * @return the current card ImageView
     */
    public ImageView getIvCurrent() {
        return ivCurrent;
    }

    /**
     * Returns the HBox containing the player's cards.
     *
     * @return the HBox with the cards
     */
    public HBox getHbCards() {
        return hbCards;
    }

    /**
     * Returns the button for drawing a card.
     *
     * @return the draw button
     */
    public Button getBtnDraw() {
        return btnDraw;
    }

    /**
     * Disables the player's cards and the draw button.
     * This is used to prevent interaction when it is not the player's turn.
     */
    public void disableCards() {
        hbCards.getChildren().forEach(node -> node.setDisable(true));
        btnDraw.setDisable(true);
    }
}
