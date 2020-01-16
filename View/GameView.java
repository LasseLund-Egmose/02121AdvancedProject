package View;

import Boot.Main;

import Controller.AbstractController;

import Enum.Setting;
import Enum.Team;

import Model.CheckerPiece;
import Model.Field;
import Model.Settings;

import Persistence.ObjectDB;

import Util.Content;
import Util.StyleCollection;
import Util.StyleProp;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.util.HashMap;

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

    // Root element
    protected StackPane root = new StackPane();

    // GameControllers.Controller instance
    protected AbstractController controller;

    // Timeline for handling baground music
    protected Timeline musicTimeline;

    // Board
    protected int dimension = 8; // Board dimension
    protected GridPane grid;
    protected Pane surfacePane;
    protected RotateTransition surfacePaneRotation; // Transition rotating board after each turn

    // Graphical elements
    protected Text displayTurn; // Text element displaying turn
    public static Text displayWhiteTimeLeft;
    public static Text displayBlackTimeLeft;

    // Pause game
    protected boolean isPauseButtonActive = true;
    protected StackPane pausePane;
    protected StackPane stopGamePane = new StackPane();


    // Calculate how far away elements should be moved to avoid colliding with background
    // Using the Pythagorean theorem and the law of sines
    protected static double zOffset() {
        return Math.sqrt(2) * (GameView.BOARD_SIZE / 2.0) * Math.sin(Math.toRadians(GameView.BOARD_TILT));
    }

    // Construct text field containing time left for player
    protected Text constructTimeLeftText(String prefix, int time) {
        Text timeLeft = new Text();
        timeLeft.setText(prefix + AbstractController.formatTime(time));

        StyleCollection.build(
                timeLeft,
                StyleProp.FONT("28 Arial"),
                StyleProp.FILL("#B8860B")
        );

        return timeLeft;
    }

    // Create scene
    protected Scene makeScene(boolean isWhiteTurn) {
        this.dimension = Settings.getInt(Setting.Dimension);

        // Setup root pane
        StackPane root = new StackPane();
        root.setMinSize(GameView.WIDTH, GameView.HEIGHT);
        root.setMaxSize(GameView.WIDTH, GameView.HEIGHT);

        // Setup turn text and its container
        this.displayTurn = new Text();
        StyleCollection.build(
                this.displayTurn,
                StyleProp.FONT("50 Arial"),
                StyleProp.FILL("#DAA520")
        );

        this.setupDisplayTurn(isWhiteTurn);

        StackPane displayTurnContainer = new StackPane();
        setupContainer(displayTurnContainer);
        displayTurnContainer.getChildren().add(this.displayTurn);

        // Setup white time text and its container
        GameView.displayWhiteTimeLeft = this.constructTimeLeftText(Content.WHITE_TIME_LEFT, controller.timeWhite--);

        StackPane displayWhiteTimeContainer = new StackPane();
        setupContainer(displayWhiteTimeContainer);
        displayWhiteTimeContainer.getChildren().add(displayWhiteTimeLeft);

        // Setup black time text and its container
        GameView.displayBlackTimeLeft = this.constructTimeLeftText(Content.BLACK_TIME_LEFT, controller.timeBlack--);

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
        boardContainer.setTranslateZ(-GameView.zOffset());

        StyleCollection.build(
                boardContainer,
                StyleProp.EFFECT("null")
        );

        // Setup board surface and add it to board container
        this.setupSurface();
        boardContainer.getChildren().add(this.surfacePane);

        // Styling for the pane that prevents player interaction while the game is paused
        this.stopGamePane.setMinSize(GameView.WIDTH, GameView.HEIGHT);
        this.stopGamePane.setMaxSize(GameView.WIDTH, GameView.HEIGHT);
        this.stopGamePane.setTranslateZ(2 * -GameView.zOffset());

        StyleCollection.build(
                this.stopGamePane,
                StyleProp.BACKGROUND_COLOR("#555555a0")
        );

        // Setup and style pause button
        this.pausePane = new StackPane();
        this.pausePane.setMinHeight(40);
        this.pausePane.setMinWidth(10);
        this.pausePane.setMaxHeight(20);
        this.pausePane.setMaxWidth(100);

        // Setup and style text for pause button
        Text pauseText = new Text();
        pauseText.setText(Content.PAUSE);
        pauseText.setFill(Color.DARKGOLDENROD);

        StyleCollection.build(
                pauseText,
                StyleProp.FONT("20 Arial")
        );

        this.pausePane.getChildren().add(pauseText);

        // Add click event to pause button, adds stopGamePane to root in front of the other game elements.
        this.setPauseButtonActive(true);

        this.pausePane.setOnMouseClicked(e -> {
            if (this.isPauseButtonActive) {
                this.musicTimeline.pause();
                root.getChildren().add(this.stopGamePane);
                displayPauseScreen();
            }
        });

        // Add aforementioned elements to root
        root.getChildren().addAll(background, boardContainer, displayTurnContainer, displayWhiteTimeContainer, displayBlackTimeContainer, this.pausePane);

        // Pass through click events and disable shadows for root
        root.setPickOnBounds(false);
        StyleCollection.build(
                root,
                StyleProp.EFFECT("null")
        );

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

        // Set root and return scene
        this.root = root;
        return scene;
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
        StyleCollection.build(
                this.grid,
                StyleProp.EFFECT("null")
        );

        // Add grid to board
        this.surfacePane.getChildren().add(this.grid);
    }

    // Setup board
    protected void setupSurface() {
        this.surfacePane = new StackPane();
        this.surfacePane.setPickOnBounds(false);
        StyleCollection.build(
                this.surfacePane,
                StyleProp.EFFECT("null")
        );

        // Setup box below board surface
        Box box = new Box();
        box.setWidth(GameView.BOARD_SIZE);
        box.setHeight(GameView.BOARD_SIZE);
        box.setDepth(GameView.DEPTH);

        // Pass through click events and remove shadow
        box.setPickOnBounds(false);
        StyleCollection.build(
                box,
                StyleProp.EFFECT("null")
        );

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

    // Setup pause scene and display it
    public void displayPauseScreen() {
        this.controller.pauseTime();

        // Setup dialog and root element
        Stage dialog = new Stage();
        dialog.setTitle(Content.GAME_PAUSED);

        StackPane root = new StackPane();
        root.setMinSize(GameView.WIDTH, GameView.HEIGHT);
        root.setMaxSize(GameView.WIDTH, GameView.HEIGHT);
        StyleCollection.build(
                root,
                StyleProp.BORDER_COLOR("#DAA520"),
                StyleProp.BORDER_WIDTH("5px"),
                StyleProp.BACKGROUND_COLOR("antiquewhite")
        );

        // Setup resume button with click event
        Button resumeButton = new Button(Content.RESUME_GAME);
        StyleCollection.buttonStyle(resumeButton);

        resumeButton.setOnMouseClicked(e -> {
            this.musicTimeline.play();
            this.root.getChildren().remove(stopGamePane);
            dialog.close();
            this.controller.startTime();
        });

        // Setup save button with click event
        Button saveButton = new Button(Content.SAVE_GAME);
        StyleCollection.buttonStyle(saveButton);

        saveButton.setOnMouseClicked(e -> {

            // New ObjectDB instance
            ObjectDB saveGame = new ObjectDB();

            // Add variables
            saveGame.setActiveCount(controller.getActiveCount());
            saveGame.setCheckerPieces(controller.getCheckerPieces());
            saveGame.setFields(controller.getFields());
            saveGame.setDimension(Settings.getInt(Setting.Dimension));
            saveGame.setWhiteTurn(controller.isWhiteTurn());
            saveGame.setTimeWhite(controller.timeWhite);
            saveGame.setTimeBlack(controller.timeBlack);
            saveGame.setTotalTime(controller.totalTime);

            // Save, check if it's succesful and display message accordingly
            if (saveGame.saveState(MainMenuView.selectedGameType.name())) {
                saveButton.setText(Content.GAME_SAVE_SUCCESS);
            } else {
                saveButton.setText(Content.GAME_SAVE_ERROR);
            }
        });

        // Setup quit game button with click event
        Button quitButton = new Button(Content.QUIT_GAME);
        StyleCollection.buttonStyle(quitButton);

        quitButton.setOnMouseClicked(e -> {
            Main.setView(Main.mainMenuView);
            dialog.close();
        });

        // Return to game if the pause window is closed
        dialog.setOnCloseRequest(event -> {
            this.root.getChildren().remove(stopGamePane);
            this.controller.startTime();
        });


        // Create pause menu container and add it to root
        VBox pauseMenuVBox = new VBox();
        pauseMenuVBox.setSpacing(40);
        pauseMenuVBox.setAlignment(Pos.CENTER);

        pauseMenuVBox.getChildren().addAll(resumeButton, saveButton, quitButton);
        root.getChildren().add(pauseMenuVBox);

        StackPane.setAlignment(pauseMenuVBox, Pos.CENTER);

        // Setup scene and show stage
        Scene scene = new Scene(root, GameView.WIDTH, GameView.HEIGHT);
        dialog.setScene(scene);
        dialog.show();
    }

    // Setup win scene and display it
    public void displayWin(Team winningTeam) {
        Main.setView(Main.mainMenuView); // Go back to main menu

        // Setup new dialog
        Stage dialog = new Stage();
        dialog.setTitle(Content.YOU_WON);

        StackPane root = new StackPane();

        // Setup close button
        Button button = new Button(Content.CLOSE);
        StyleCollection.build(
                button,
                StyleProp.BACKGROUND_COLOR("transparent"),
                StyleProp.CURSOR("hand"),
                StyleProp.FONT_SIZE("30px"),
                StyleProp.FONT_WEIGHT("bold"),
                StyleProp.TEXT_FILL("#DAA520")
        );
        button.setOnMouseClicked(e -> dialog.close());

        // Setup main pane
        StackPane pane = new StackPane();
        StyleCollection.build(
                pane,
                StyleProp.BORDER_COLOR("#DAA520"),
                StyleProp.BORDER_WIDTH("5px"),
                StyleProp.BACKGROUND_COLOR("antiquewhite")
        );
        pane.setMinSize(GameView.POPUP_SIZE, GameView.POPUP_SIZE);
        pane.setMaxSize(GameView.POPUP_SIZE, GameView.POPUP_SIZE);

        // Setup who won text
        Text text = new Text();
        text.setText(winningTeam == Team.BLACK ? Content.BLACK_WON : Content.WHITE_WON);
        StyleCollection.build(
                text,
                StyleProp.FONT("70px Arial")
        );


        // Setup total game time container
        StackPane timePane = new StackPane();
        timePane.setMinSize(GameView.POPUP_SIZE, GameView.POPUP_SIZE / 1.6);
        timePane.setMaxSize(GameView.POPUP_SIZE, GameView.POPUP_SIZE / 1.6);

        // Display total game time
        Text timeText = new Text();
        timeText.setText(Content.GAME_LENGTH + AbstractController.formatTime(controller.totalTime));
        StyleCollection.build(
                timeText,
                StyleProp.FONT("30px Arial")
        );

        // Setup alignments
        StackPane.setAlignment(text, Pos.CENTER);
        StackPane.setAlignment(pane, Pos.CENTER);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(timePane, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(timeText, Pos.CENTER);

        // Add children to parents
        pane.getChildren().addAll(text, timePane, button);
        timePane.getChildren().add(timePane);
        root.getChildren().add(pane);

        // Setup scene and show stage
        Scene scene = new Scene(root, GameView.WIDTH, GameView.HEIGHT);
        dialog.setScene(scene);
        dialog.show();
    }

    // Get size (in pixels) of one field in board
    public double getFieldSize() {
        return ((double) GameView.BOARD_SIZE) / this.dimension;
    }

    // Title
    public String getTitle() {
        return Content.TITLE_GAME_VIEW;
    }

    // Add highlight to black field
    public void highlightPane(StackPane pane) {
        StyleCollection.modifyProps(
            pane,
            StyleProp.BORDER_COLOR("green"),
            StyleProp.BORDER_WIDTH(this.getFieldSize() < 20 ? "2px" : "5px")
        );
    }

    // Add highlight to black field (for CPU move)
    public void highlightPaneCPU(StackPane pane) {
        StyleCollection.modifyProps(
            pane,
            StyleProp.BORDER_COLOR("blue"),
            StyleProp.BORDER_WIDTH(this.getFieldSize() < 20 ? "2px" : "5px")
        );
    }

    // Remove highlight from black field
    public void normalizePane(StackPane pane) {
        StyleCollection.modifyProps(
            pane,
            StyleProp.BORDER_COLOR(null),
            StyleProp.BORDER_WIDTH(null)
        );
    }

    // Rotate board
    public void rotate() {
        this.surfacePaneRotation.play();
    }

    // Update isPauseButtonActive value
    public void setPauseButtonActive(boolean pauseButtonActive) {
        this.isPauseButtonActive = pauseButtonActive;

        // Visualise when button is inactive
        StyleCollection.build(
                this.pausePane,
                StyleProp.BACKGROUND_IMAGE("url(/assets/dark_wood.jpg)"),
                StyleProp.CURSOR(pauseButtonActive ? "hand" : null),
                StyleProp.OPACITY(pauseButtonActive ? "1" : "0.5")
        );
    }

    // Setup container element
    public void setupContainer(StackPane container) {
        container.setMinHeight(80);
        container.setMinWidth(20);
        container.setMaxHeight(20);
        container.setMaxWidth(300);
        container.setTranslateZ(-GameView.zOffset());
        StyleCollection.build(
                container,
                StyleProp.BACKGROUND_IMAGE("url(/assets/dark_wood.jpg)"),
                StyleProp.BORDER_COLOR("#DAA520"),
                StyleProp.BORDER_WIDTH("5px"),
                StyleProp.TEXT_FILL("#DAA520")
        );
    }

    // Set text based on turn
    public void setupDisplayTurn(boolean isWhiteTurn) {
        this.displayTurn.setText(isWhiteTurn ? Content.WHITE_TURN : Content.BLACK_TURN);
    }

    // Setup one black field
    public void setupField(StackPane field, Point position) {
        StyleCollection.build(
            field,
            StyleProp.BACKGROUND_IMAGE("url(/assets/dark_wood.jpg)")
        );
        field.setPrefSize(this.getFieldSize(), this.getFieldSize());

        // Add it to the grid
        this.grid.add(field, position.x - 1, position.y - 1);

        // Bring field background to front
        field.setTranslateZ(0.01);
    }

    // Setup background music
    protected void setupMusic() {
        // Load and play music file
        String path = "/assets/hey.mp3";
        Media media = new Media(this.getClass().getResource(path).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();

        this.musicTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), ev -> {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        }));
        this.musicTimeline.setCycleCount(Animation.INDEFINITE);
        this.musicTimeline.play();
    }

    // Setup scene
    public Scene setupScene() {

        setupMusic();

        // Setup controller
        this.controller = (AbstractController) Settings.get(Setting.Controller);

        //reset time values
        controller.setTotalTime();
        controller.setTime();

        Scene scene = this.makeScene(true);

        // Setup black fields (with click events), game pieces and start game
        this.controller.setupFields();
        this.controller.setupPieces();
        this.controller.countDownTimer();
        this.controller.onTurnStart();

        return scene;
    }

    // Setup scene from saved game
    public Scene setupScene(ObjectDB db) {

        setupMusic();

        // Setup controller
        this.controller = (AbstractController) Settings.get(Setting.Controller);

        // Set time values
        this.controller.setTotalTime(db.getTotalTime());
        this.controller.setTimeWhite(db.getTimeWhite());
        this.controller.setTimeBlack(db.getTimeBlack());

        Scene scene = makeScene(db.isWhiteTurn());

        // Loop over all the fields in the fields HashMap
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

        // Rotate surfacePane if it's blacks turn
        if (!db.isWhiteTurn()) {
            this.surfacePaneRotation.play();
        }

        // Start the turn
        this.controller.onTurnStart();
        this.controller.countDownTimer();

        return scene;
    }

}
