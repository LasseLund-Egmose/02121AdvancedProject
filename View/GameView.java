package View;

import Boot.Main;
import Controller.AbstractController;
import Enum.Setting;
import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import Model.Settings;
import Persistence.ObjectDB;

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
import java.util.HashMap;
import java.util.Stack;

// TODO: Needs cleanup and comments
public class GameView extends AbstractView {

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

    // Pause button
    protected boolean isPauseButtonActive = true;
    protected StackPane pausePane;

    protected AbstractController controller; // GameControllers.Controller instance
    protected int dimension = 8; // Board dimension
    protected Text displayTurn; // Text element displaying turn
    public static Text displayWhiteTimeLeft;
    public static Text displayBlackTimeLeft;
    protected GridPane grid;
    protected Pane surfacePane;
    protected RotateTransition surfacePaneRotation; // Transition rotating board after each turn
    protected Settings settings;
    protected StackPane stopGamePane = new StackPane();
    protected StackPane root = new StackPane();

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

    // Construct
    public GameView(String[] args) {
        super(args);
    }

    // Setup win scene and display it
    public void displayWin(Team winningTeam) {
        Main.setView(Main.mainMenuView);

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
        text.setText(winningTeam == Team.BLACK ? "Black won" : "White won");
        text.setStyle("-fx-font: 70px Arial");

        //total game time
        StackPane timepane = new StackPane();
        timepane.setMinSize(GameView.POPUP_SIZE, GameView.POPUP_SIZE/1.6); //design choice
        timepane.setMaxSize(GameView.POPUP_SIZE, GameView.POPUP_SIZE/1.6);

        //displays the total time the game took
        Text timetext = new Text();
        timetext.setText("Game length: " + controller.formatTime(controller.totalTime));
        timetext.setStyle("-fx-font: 30px Arial");

        StackPane.setAlignment(text, Pos.CENTER);
        StackPane.setAlignment(pane, Pos.CENTER);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(timepane, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(timetext, Pos.CENTER);

        pane.getChildren().addAll(text, timepane, button); //add button last to have it on top
        timepane.getChildren().add(timetext);
        root.getChildren().add(pane);

        Scene scene = new Scene(root, GameView.WIDTH, GameView.HEIGHT);
        dialog.setScene(scene);
        dialog.show();
    }

    //Setup pause scene and display it
    public void displayPauseScreen() {
        this.controller.pauseTime();

        Stage dialog = new Stage();
        dialog.setTitle("Game paused");

        StackPane root = new StackPane();
        root.setMinSize(GameView.WIDTH, GameView.HEIGHT);
        root.setMaxSize(GameView.WIDTH, GameView.HEIGHT);
        root.setStyle("-fx-border-color: #DAA520; -fx-border-width: 5px; -fx-background-color: antiquewhite;");

        //removes stopGamePane from the game stage, and starts the time again.
        Button resumeButton = new Button("Resume game");
        resumeButton.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                "-fx-border-color: #DAA520; -fx-border-width: 5px;");
        resumeButton.setOnMouseClicked(e -> {
            this.root.getChildren().remove(stopGamePane);
            dialog.close();
            this.controller.startTime();
        });

        //calls on instance of ObjectDB to save every variable as is in the current gamestate
        Button saveButton = new Button("Save game");
        saveButton.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                "-fx-border-color: #DAA520; -fx-border-width: 5px;");
        saveButton.setOnMouseClicked(e -> {
            ObjectDB saveGame = new ObjectDB();
            saveGame.setActiveCount(controller.getActiveCount());
            saveGame.setCheckerPieces(controller.getCheckerPieces());
            saveGame.setFields(controller.getFields());
            saveGame.setDimension((int) Settings.get(Setting.Dimension));
            saveGame.setWhiteTurn(controller.isWhiteTurn());
            saveGame.setTimeWhite(controller.timeWhite);
            saveGame.setTimeBlack(controller.timeBlack);
            saveGame.setTotalTime(controller.totalTime);
            saveGame.setSelectedGameType(MainMenuView.selectedGameType);

            if (saveGame.saveState(MainMenuView.selectedGameType.name())) {
                saveButton.setText("Game Saved!");
            } else {
                saveButton.setText("Couldn't Save Game");
            }
        });

        //go to main menu
        Button quitButton = new Button("Quit game");
        quitButton.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                "-fx-border-color: #DAA520; -fx-border-width: 5px;");
        quitButton.setOnMouseClicked(e -> {
            Main.setView(Main.mainMenuView);
            dialog.close();
        });

        //return to game if the pausewindow is closed
        dialog.setOnCloseRequest(event -> {
            this.root.getChildren().remove(stopGamePane);
            this.controller.startTime();
        });

        VBox pauseMenuVBox = new VBox();
        pauseMenuVBox.setSpacing(40);
        pauseMenuVBox.setAlignment(Pos.CENTER);

        pauseMenuVBox.getChildren().addAll(resumeButton, saveButton, quitButton);
        root.getChildren().add(pauseMenuVBox);

        StackPane.setAlignment(pauseMenuVBox, Pos.CENTER);

        Scene scene = new Scene(root, GameView.WIDTH, GameView.HEIGHT);
        dialog.setScene(scene);
        dialog.show();
    }

    public void setPauseButtonActive(boolean pauseButtonActive) {
        isPauseButtonActive = pauseButtonActive;
        if (pauseButtonActive) {
            this.pausePane.setStyle("-fx-background-image: url(/assets/dark_wood.jpg);  -fx-cursor: hand");
        } else {
            this.pausePane.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-opacity: 0.5;");
        }
    }

    // Get size (in pixels) of one field in board
    public double getFieldSize() {
        return ((double) GameView.BOARD_SIZE) / this.dimension;
    }

    // Title
    public String getTitle() {
        return "Game";
    }

    // Add highlight to black field
    public void highlightPane(StackPane pane) {
        int borderWidth = this.getFieldSize() < 20 ? 2 : 5;
        pane.setStyle(GameView.BACKGROUND_FIELD + " -fx-border-color: green; -fx-border-width: " + borderWidth + ";");
    }

    // Add highlight to black field (for CPU move)
    public void highlightPaneCPU(StackPane pane) {
        int borderWidth = this.getFieldSize() < 20 ? 2 : 5;
        pane.setStyle(GameView.BACKGROUND_FIELD + " -fx-border-color: blue; -fx-border-width: " + borderWidth + ";");
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

    public void setupContainer(StackPane container) {
        container.setMinHeight(80);
        container.setMinWidth(20);
        container.setMaxHeight(20);
        container.setMaxWidth(300);
        //container.setStyle("-fx-border-color: gray; -fx-border-width: 4;");
        container.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-text-fill: #DAA520;" +
                "-fx-border-color: #DAA520; -fx-border-width: 5px;");
        container.setTranslateZ(-GameView.zOffset());
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

    // Setup scene
    public Scene setupScene() {

        // Setup controller
        this.controller = (AbstractController) Settings.get(Setting.Controller);

        //reset time values
        controller.setTotalTime();
        controller.setTime();

        Scene scene = makeScene(true);

        // Setup black fields (with click events), game pieces and start game
        this.controller.setupFields();
        this.controller.setupPieces();
        this.controller.countDownTimer();
        this.controller.onTurnStart();

        return scene;
    }

    // Setup scene from saved game
    public Scene setupScene(ObjectDB db) {

        controller.setTotalTime(db.getTotalTime());
        controller.setTimeWhite(db.getTimeWhite());
        controller.setTimeBlack(db.getTimeBlack());

        Scene scene = makeScene(db.isWhiteTurn());

        // Setup controller
        this.controller = (AbstractController) Settings.get(Setting.Controller);

        // Loop over all the fields in the fields hashmap
        for (HashMap.Entry<Integer, HashMap<Integer, Field>> x : db.getFields().entrySet()) {
            for (HashMap.Entry<Integer, Field> y : x.getValue().entrySet()) {
                // Setup black fields (with click events)
                this.controller.setupField(y.getValue());
            }
        }

        for (CheckerPiece piece : db.getCheckerPieces()) {
            // Initialize each checker piece with loaded state
            if (piece.getParent() != null) {
                piece.setupPiece();
                piece.setupEvent(this.controller);
                piece.attachToField(piece.getParent(), db.getActiveCount());
                piece.setCanHighlight(piece.getCanHighlight());
            }
        }

        this.controller.countDownTimer();

        // Rotate surfacePane if it's blacks turn
        if (!db.isWhiteTurn()) {
            this.surfacePaneRotation.play();
        }

        // Start the turn
        this.controller.onTurnStart();

        return scene;
    }

    protected Scene makeScene(boolean isWhiteTurn) {

        this.dimension = (int) Settings.get(Setting.Dimension);

        // Setup root pane
        StackPane root = new StackPane();
        root.setMinSize(GameView.WIDTH, GameView.HEIGHT);
        root.setMaxSize(GameView.WIDTH, GameView.HEIGHT);

        // Setup turn text and its container
        this.displayTurn = new Text();
        this.displayTurn.setStyle("-fx-font: 50 Arial;");
        this.displayTurn.setFill(Color.GOLDENROD);
        this.setupDisplayTurn(isWhiteTurn);

        StackPane displayTurnContainer = new StackPane();
        setupContainer(displayTurnContainer);
        displayTurnContainer.getChildren().add(this.displayTurn);

        //Setup white time text and its container
        displayWhiteTimeLeft = new Text();
        displayWhiteTimeLeft.setStyle("-fx-font: 30 Arial;");
        displayWhiteTimeLeft.setFill(Color.DARKGOLDENROD);
        displayWhiteTimeLeft.setText("White time left: " + controller.formatTime(controller.timeWhite--));

        StackPane displayWhiteTimeContainer = new StackPane();
        setupContainer(displayWhiteTimeContainer);
        displayWhiteTimeContainer.getChildren().add(displayWhiteTimeLeft);

        //Setup black time text and its container
        displayBlackTimeLeft = new Text();
        displayBlackTimeLeft.setStyle("-fx-font: 30 Arial;");
        displayBlackTimeLeft.setFill(Color.DARKGOLDENROD);
        displayBlackTimeLeft.setText("Black time left: " + controller.formatTime(controller.timeBlack--));

        StackPane displayBlackTimeContainer = new StackPane();
        setupContainer(displayBlackTimeContainer);
        displayBlackTimeContainer.getChildren().add(displayBlackTimeLeft);

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

        //styling for the pane the prevents player interaction while the game is paused.
        this.stopGamePane.setMinSize(GameView.WIDTH, GameView.HEIGHT);
        this.stopGamePane.setMaxSize(GameView.WIDTH, GameView.HEIGHT);
        this.stopGamePane.setStyle("-fx-background-color: #555555a0");
        this.stopGamePane.setTranslateZ(2 * -GameView.zOffset());

        //setup and style pause button
        this.pausePane = new StackPane();
        this.pausePane.setMinHeight(40);
        this.pausePane.setMinWidth(10);
        this.pausePane.setMaxHeight(20);
        this.pausePane.setMaxWidth(100);

        //setup and style text for pause button
        Text pauseText = new Text();
        pauseText.setText("Pause");
        pauseText.setStyle("-fx-font: 20 Arial;");
        pauseText.setFill(Color.DARKGOLDENROD);
        this.pausePane.getChildren().add(pauseText);

        //add clickevent to pausebutton, adds stopGamePane to root in front of the other game elements.
        this.pausePane.setStyle("-fx-background-image: url(/assets/dark_wood.jpg);  -fx-cursor: hand");
        this.pausePane.setOnMouseClicked(e -> {
            if (this.isPauseButtonActive) {
                root.getChildren().add(this.stopGamePane);
                displayPauseScreen();
            }
        });

        // Add aforementioned elements to root
        root.getChildren().addAll(background, boardContainer, displayTurnContainer, displayWhiteTimeContainer, displayBlackTimeContainer, this.pausePane);

        // Pass through click events and disable shadows for root
        root.setPickOnBounds(false);
        root.setStyle("-fx-effect: null;");

        // Set alignments for elements
        StackPane.setAlignment(background, Pos.CENTER);
        StackPane.setAlignment(displayTurnContainer, Pos.TOP_CENTER);
        StackPane.setAlignment(displayWhiteTimeContainer, Pos.TOP_LEFT);
        StackPane.setAlignment(displayBlackTimeContainer, Pos.TOP_RIGHT);
        StackPane.setAlignment(this.surfacePane, Pos.CENTER);
        StackPane.setAlignment(this.displayTurn, Pos.CENTER);
        StackPane.setAlignment(this.pausePane, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(this.pausePane, Pos.TOP_CENTER);

        // Setup scene (with depthBuffer to avoid z-fighting and unexpected behaviour) and apply it
        Scene scene = new Scene(root, GameView.WIDTH, GameView.HEIGHT, true, null);

        // Setup camera for scene
        PerspectiveCamera pc = new PerspectiveCamera();
        pc.setTranslateZ(-GameView.zOffset());
        scene.setCamera(pc);

        this.root = root;

        return scene;
    }

}
