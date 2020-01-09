import javafx.animation.RotateTransition;
import javafx.application.Application;
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

public class View extends Application {

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

    protected Controller controller; // Controller instance
    protected int dimension = 8; // Board dimension
    protected Text displayTurn; // Text element displaying turn
    protected GridPane grid;
    protected Pane surfacePane;
    protected RotateTransition surfacePaneRotation; // Transition rotating board after each turn
    protected Stage primaryStage;

    // Set received args and launch application
    public static void main(String[] args) {
        View.args = args;

        launch(args);
    }

    // Setup one black field
    protected void setupField(int i, int j) {
        StackPane field = new StackPane();
        field.setStyle(View.BACKGROUND_FIELD);
        field.setPrefSize(this.getSize(), this.getSize());

        // Add it to the grid
        this.grid.add(field, i, j);

        // Bring field background to front
        field.setTranslateZ(0.01);

        // Add it to HashMap in controller
        this.controller.addField(new Point(i + 1, j + 1), field);
    }

    // Setup black fields
    protected void setupFields() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = i % 2; j < this.dimension; j += 2) {
                this.setupField(i, j);
            }
        }
    }

    // Setup GridPane on board surface
    protected void setupGrid() {
        this.grid = new GridPane();

        this.grid.setMinHeight(View.BOARD_SIZE);
        this.grid.setMinWidth(View.BOARD_SIZE);
        this.grid.setMaxHeight(View.BOARD_SIZE);
        this.grid.setMaxWidth(View.BOARD_SIZE);
        this.grid.setTranslateZ(-View.DEPTH / 2.0);

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
        box.setWidth(View.BOARD_SIZE);
        box.setHeight(View.BOARD_SIZE);
        box.setDepth(View.DEPTH);
        box.setPickOnBounds(false);
        box.setStyle("-fx-effect: null;");

        // Add wood texture to box
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(getClass().getResourceAsStream(View.ASSET_GRID)));
        box.setMaterial(material);

        // Alignment
        StackPane.setAlignment(box, Pos.CENTER);

        // Setup rotation
        this.surfacePaneRotation = new RotateTransition();
        this.surfacePaneRotation.setAxis(Rotate.Z_AXIS);
        this.surfacePaneRotation.setByAngle(180);
        this.surfacePaneRotation.setCycleCount(1);
        this.surfacePaneRotation.setDuration(Duration.millis(1000));
        this.surfacePaneRotation.setAutoReverse(false);
        this.surfacePaneRotation.setNode(this.surfacePane);

        this.surfacePane.getChildren().add(box);

        // Setup grid
        this.setupGrid();
    }

    // Setup win scene and display it
    public void displayWin(String whoWon) {
        this.primaryStage.setTitle("You won!");

        StackPane root = new StackPane();

        Button button = new Button("Close");
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;");
        button.setOnMouseClicked(e -> this.primaryStage.close());

        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: antiquewhite; -fx-border-color: #DAA520; -fx-border-width: 5px;");
        pane.setMinSize(View.POPUP_SIZE, View.POPUP_SIZE);
        pane.setMaxSize(View.POPUP_SIZE, View.POPUP_SIZE);

        Text text = new Text();
        text.setText(whoWon);
        text.setStyle("-fx-font: 70px Arial");

        StackPane.setAlignment(text, Pos.CENTER);
        StackPane.setAlignment(pane, Pos.CENTER);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);

        pane.getChildren().addAll(text, button);
        root.getChildren().add(pane);

        Scene scene = new Scene(root, View.WIDTH, View.HEIGHT);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    // Get size (in pixels) of one field in board
    public double getSize() {
        return ((double) View.BOARD_SIZE) / this.dimension;
    }

    // Add highlight to black field
    public void highlightPane(StackPane pane) {
        int borderWidth = this.getSize() < 20 ? 2 : 5;
        pane.setStyle(View.BACKGROUND_FIELD + " -fx-border-color: green; -fx-border-width: " + borderWidth + ";");
    }

    // Remove highlight from black field
    public void normalizePane(StackPane pane) {
        pane.setStyle(View.BACKGROUND_FIELD);
    }

    // Rotate board
    public void rotate() {
        this.surfacePaneRotation.play();
    }

    // Set text based on turn
    public void setupDisplayTurn(boolean isWhiteTurn) {
        this.displayTurn.setText(isWhiteTurn ? "White's turn" : "Black's turn");
    }

    // Handle dimension argument and setup View elements
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SimpDam");

        // Handle n-argument
        if (View.args.length == 1) {
            int newN = Integer.parseInt(View.args[0]);

            if (newN >= 3 && newN <= 100) {
                this.dimension = newN;
            }
        }

        // Setup root pane
        StackPane root = new StackPane();
        root.setMinSize(View.WIDTH, View.HEIGHT);
        root.setMaxSize(View.WIDTH, View.HEIGHT);

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

        // Setup background and move it behind the board
        Rectangle background = new Rectangle(View.WIDTH * 2, View.HEIGHT * 2);
        background.setFill(Color.web("antiquewhite"));

        // Calculate how far away the background should be moved (using the Pythagorean theorem and the law of sines)
        double boardDiagonal = Math.sqrt(2) * (View.BOARD_SIZE / 2.0);
        double backgroundOffset = boardDiagonal * Math.sin(Math.toRadians(View.BOARD_TILT));
        background.setTranslateZ(backgroundOffset);

        // Setup container for board and rotate it according to BOARD_TILT
        StackPane boardContainer = new StackPane();
        boardContainer.setPrefSize(View.WIDTH, View.HEIGHT);
        boardContainer.setRotationAxis(Rotate.X_AXIS);
        boardContainer.setRotate(-View.BOARD_TILT);
        boardContainer.setPickOnBounds(false);
        boardContainer.setStyle("-fx-effect: null;");

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
        this.controller = new Controller(this, this.dimension, this.grid);

        // Setup black fields (with click events) and game pieces
        this.setupFields();
        this.controller.setupPieces();

        // Setup scene (with depthBuffer to avoid z-fighting and unexpected behaviour) and apply it
        Scene scene = new Scene(root, View.WIDTH, View.HEIGHT, true, null);
        scene.setCamera(new PerspectiveCamera());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
