package View;

import Controller.AbstractController;
import Controller.RegularCheckersController;
import Model.Settings;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;

public class GameView {

    // Program arguments
    public static String[] args;

    // Constants
    protected static final int BOARD_SIZE = 700;
    protected static final int BOARD_TILT = 50;
    protected static final int DEPTH = 50;
    protected static final int HEIGHT = 800;
    protected static final int POPUP_SIZE = 400;
    protected static final int WIDTH = 1000;

    // Assets and background styling
    protected static final String ASSET_GRID = "/assets/grid.png";
    protected static final String BACKGROUND_FIELD = "-fx-background-image: url(/assets/dark_wood.jpg);";

    protected AbstractController controller; // GameControllers.Controller instance
    protected int dimension = 8; // Board dimension
    protected Text displayTurn; // Text element displaying turn
    protected GridPane grid;
    protected Pane surfacePane;
    protected RotateTransition surfacePaneRotation; // Transition rotating board after each turn

    protected Settings settings;

    // Calculate how far away elements should be moved to avoid colliding with background
    // Using the Pythagorean theorem and the law of sines
    protected static double zOffset() {
        return Math.sqrt(2) * (GameView.BOARD_SIZE / 2.0) * Math.sin(Math.toRadians(GameView.BOARD_TILT));
    }

    // Setup GridPane on board surface
    protected void setupGrid() {
        this.grid = new GridPane();

        this.grid.setMinHeight(GameView.BOARD_SIZE);
        this.grid.setMinWidth(GameView.BOARD_SIZE);
        this.grid.setMaxHeight(GameView.BOARD_SIZE);
        this.grid.setMaxWidth(GameView.BOARD_SIZE);
        this.grid.setTranslateZ(-GameView.DEPTH / 2.0);

        // Invert y-axis leaving position (1,1) at bottom-left
        this.grid.setRotationAxis(Rotate.X_AXIS);
        this.grid.setRotate(180);

        // Pass through click events and remove shadow
        this.grid.setPickOnBounds(false);
        this.grid.setStyle("-fx-effect: null;");

        // Add grid to board
        this.surfacePane.getChildren().add(this.grid);
    }

    // Setup board
    protected void setupSurface() {
        this.surfacePane = new StackPane();
        this.surfacePane.setPickOnBounds(false);
        this.surfacePane.setStyle("-fx-effect: null;");

        // Setup box below board surface
        Box box = new Box();
        box.setWidth(GameView.BOARD_SIZE);
        box.setHeight(GameView.BOARD_SIZE);
        box.setDepth(GameView.DEPTH);

        // Pass through click events and remove shadow
        box.setPickOnBounds(false);
        box.setStyle("-fx-effect: null;");

        // Add wood texture to box
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(getClass().getResourceAsStream(GameView.ASSET_GRID)));
        box.setMaterial(material);

        // Alignment
        StackPane.setAlignment(box, Pos.CENTER);

        // Setup rotation
        this.surfacePaneRotation = new RotateTransition();
        this.surfacePaneRotation.setAxis(Rotate.Z_AXIS);
        this.surfacePaneRotation.setByAngle(180);
        this.surfacePaneRotation.setCycleCount(1);
        this.surfacePaneRotation.setDuration(Duration.millis(1000));
        this.surfacePaneRotation.setNode(this.surfacePane);

        // Add to surfacePane
        this.surfacePane.getChildren().add(box);

        // Setup grid
        this.setupGrid();
    }

    // Setup win scene and display it
    public void displayWin(String whoWon) {
        MainView.changeToMainMenuScene();

        Stage dialog = new Stage();
        dialog.setTitle("You won!");

        StackPane root = new StackPane();

        Button button = new Button("Close");
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;");
        button.setOnMouseClicked(e -> dialog.close());

        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: antiquewhite; -fx-border-color: #DAA520; -fx-border-width: 5px;");
        pane.setMinSize(GameView.POPUP_SIZE, GameView.POPUP_SIZE);
        pane.setMaxSize(GameView.POPUP_SIZE, GameView.POPUP_SIZE);

        Text text = new Text();
        text.setText(whoWon);
        text.setStyle("-fx-font: 70px Arial");

        StackPane.setAlignment(text, Pos.CENTER);
        StackPane.setAlignment(pane, Pos.CENTER);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);

        pane.getChildren().addAll(text, button);
        root.getChildren().add(pane);

        Scene scene = new Scene(root, GameView.WIDTH, GameView.HEIGHT);
        dialog.setScene(scene);
        dialog.show();
    }

    // Get size (in pixels) of one field in board
    public double getFieldSize() {
        return ((double) GameView.BOARD_SIZE) / this.dimension;
    }

    // Add highlight to black field
    public void highlightPane(StackPane pane) {
        int borderWidth = this.getFieldSize() < 20 ? 2 : 5;
        pane.setStyle(GameView.BACKGROUND_FIELD + " -fx-border-color: green; -fx-border-width: " + borderWidth + ";");
    }

    // Remove highlight from black field
    public void normalizePane(StackPane pane) {
        pane.setStyle(GameView.BACKGROUND_FIELD);
    }

    // Rotate board
    public void rotate() {
        this.surfacePaneRotation.play();
    }

    // Set text based on turn
    public void setupDisplayTurn(boolean isWhiteTurn) {
        this.displayTurn.setText(isWhiteTurn ? "White's turn" : "Black's turn");
    }

    // Setup one black field
    public void setupField(StackPane field, Point position) {
        field.setStyle(GameView.BACKGROUND_FIELD);
        field.setPrefSize(this.getFieldSize(), this.getFieldSize());

        // Add it to the grid
        this.grid.add(field, position.x - 1, position.y - 1);

        // Bring field background to front
        field.setTranslateZ(0.01);
    }

    // Handle dimension argument and setup View.View elements
    public Scene setupGameScene() {

        // Handle n-argument
        if (GameView.args.length == 1) {
            int newN = Integer.parseInt(GameView.args[0]);

            if (newN >= 3 && newN <= 100) {
                this.dimension = newN;
            }
        }

        // Setup root pane
        StackPane root = new StackPane();
        root.setMinSize(GameView.WIDTH, GameView.HEIGHT);
        root.setMaxSize(GameView.WIDTH, GameView.HEIGHT);

        // Setup turn text and its container
        this.displayTurn = new Text();
        this.displayTurn.setStyle("-fx-font: 50 Arial;");
        this.displayTurn.setFill(Color.BLACK);
        this.setupDisplayTurn(true);

        StackPane displayTurnContainer = new StackPane();
        displayTurnContainer.setMinHeight(80);
        displayTurnContainer.setMinWidth(20);
        displayTurnContainer.setMaxHeight(20);
        displayTurnContainer.setMaxWidth(300);
        displayTurnContainer.setStyle("-fx-border-color: gray; -fx-border-width: 4;");
        displayTurnContainer.getChildren().add(this.displayTurn);
        displayTurnContainer.setTranslateZ(-GameView.zOffset());

        // Setup background and move it behind the board
        Rectangle background = new Rectangle(GameView.WIDTH * 2, GameView.HEIGHT * 2);
        background.setFill(Color.web("antiquewhite"));

        // Setup container for board and rotate it according to BOARD_TILT
        StackPane boardContainer = new StackPane();
        boardContainer.setPrefSize(GameView.WIDTH, GameView.HEIGHT);
        boardContainer.setRotationAxis(Rotate.X_AXIS);
        boardContainer.setRotate(-GameView.BOARD_TILT);
        boardContainer.setPickOnBounds(false);
        boardContainer.setStyle("-fx-effect: null;");
        boardContainer.setTranslateZ(-GameView.zOffset());

        // Setup board surface and add it to board container
        this.setupSurface();
        boardContainer.getChildren().add(this.surfacePane);

        // Add aforementioned elements to root
        root.getChildren().addAll(background, boardContainer, displayTurnContainer);

        // Pass through click events and disable shadows for root
        root.setPickOnBounds(false);
        root.setStyle("-fx-effect: null;");

        // Set alignments for elements
        StackPane.setAlignment(background, Pos.CENTER);
        StackPane.setAlignment(displayTurnContainer, Pos.TOP_CENTER);
        StackPane.setAlignment(this.surfacePane, Pos.CENTER);
        StackPane.setAlignment(this.displayTurn, Pos.CENTER);

        // Setup controller
        this.controller = new RegularCheckersController(this, this.dimension, this.grid);

        // Setup black fields (with click events) and game pieces
        this.controller.setupFields();
        this.controller.setupPieces();

        // Setup scene (with depthBuffer to avoid z-fighting and unexpected behaviour) and apply it
        Scene scene = new Scene(root, GameView.WIDTH, GameView.HEIGHT, true, null);

        // Setup camera for scene
        PerspectiveCamera pc = new PerspectiveCamera();
        pc.setTranslateZ(-GameView.zOffset());
        scene.setCamera(pc);

        return scene;
    }

}
