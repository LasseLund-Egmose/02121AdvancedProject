package View;

import Boot.Main;
import Controller.*;
import Enum.GameType;
import Enum.Setting;
import Model.Settings;
import Persistence.ObjectDB;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

// TODO: Needs cleanup and comments
public class MainMenuView extends AbstractView {

    protected ToggleButton playButton;
    protected Slider dimensionSlider;
    protected VBox containSlider;
    protected GridPane grid;
    protected Text information;
    protected EventHandler<MouseEvent> onMenuGameTypeButtonClicked = e -> this.changeText();
    protected static GameType selectedGameType = null;

    protected void changeText() {
        String text = "Something went wrong";

        switch (MainMenuView.selectedGameType) {
            case SimpDam:
                text = "This is the simplest version of checker. In this version each player has exactly " +
                        "one piece each, starting at opposite corners of the board. However, in this version every " +
                        "checker piece acts as a king piece, meaning it is not locked to only moving forward.";
                break;
            case TwoPlayer:
                text = "This is the normal version of checkers. For two players." +
                        " Every player has pieces on the three first rows of the board on their own respective side. " +
                        "All piece can only move forward, unless they become king pieces ";
                break;
            case SinglePlayer:
                text = "This is the Single player version of checkers, where you play against an AI. " +
                        "The rules are the same as for regular checkers.";
                break;
            case FlexibleKingTwoPlayer:
                text = "This is the international version of checkers. For two players. " +
                        "This version has the international rules " +
                        "meaning you can move the king pieces an arbitrary amount of spaces. ";
                break;
        }

        this.information.setText(text);
    }

    // Construct
    public MainMenuView(String[] args) {
        super(args);
    }

    // Title
    public String getTitle() {
        return "Main Menu";
    }

    // Setup scene
    public Scene setupScene() {
        int dimension = 8;

        if (this.args.length == 1) {
            int newN = Integer.parseInt(this.args[0]);

            if (newN >= 3 && newN <= 100) {
                dimension = newN;
            }
        }

        GridPane root = new GridPane();
        root.setStyle("-fx-border-color: #DAA520; -fx-border-width: 5px; -fx-background-color: antiquewhite;");
        root.setAlignment(Pos.TOP_LEFT);
        root.setHgap(30);
        root.setVgap(30);
        root.setPadding(new Insets(35));

        this.grid = new GridPane();
        this.grid.setHgap(50);
        this.grid.setVgap(30);

        this.containSlider = new VBox(5);
        this.containSlider.setStyle("-fx-border-image-source: url(/assets/dark_wood.jpg);" +
                " -fx-border-image-width: 10; -fx-border-image-slice: 10");
        this.containSlider.setPadding(new Insets(10));
        this.containSlider.setVisible(false);

        HBox informationContainer = new HBox();
        informationContainer.setStyle("-fx-border-image-source: url(/assets/dark_wood.jpg);" +
                " -fx-border-image-width: 10; -fx-border-image-slice: 10");
        informationContainer.setPadding(new Insets(10));

        TextFlow textFlow = new TextFlow();
        textFlow.setMaxSize(200,50);
        textFlow.setMinSize(200,50);
        this.information = new Text("Solong and thanks for all the fish!");
        textFlow.getChildren().add(this.information);
        informationContainer.getChildren().add(textFlow);


        dimensionSlider = new Slider(8, 100, dimension);
        dimensionSlider.setShowTickMarks(true);
        dimensionSlider.setShowTickLabels(true);
        dimensionSlider.setPrefSize(1000, 50);
        dimensionSlider.setBlockIncrement(1);
        dimensionSlider.setMajorTickUnit(1);
        dimensionSlider.setMinorTickCount(0);
        dimensionSlider.setSnapToTicks(true);

        ToggleGroup toggleGroup = new ToggleGroup();

        //Play button setting dimension, controller and changing game view
        this.playButton = constructButton("New Game", null);
        this.playButton.setVisible(false);
        this.playButton.setOnMouseClicked(e -> {
            int dim = (int) dimensionSlider.getValue();
            Settings.set(Setting.Dimension, dim);

            AbstractController controller;
            switch (MainMenuView.selectedGameType) {
                case SimpDam:
                    controller = new SimpDamController(Main.gameView, dim, Main.gameView.grid);
                    break;
                case SinglePlayer:
                    controller = new CPURegularCheckersController(Main.gameView, dim, Main.gameView.grid);
                    break;
                case FlexibleKingTwoPlayer:
                    controller = new FlexibleKingController(Main.gameView, dim, Main.gameView.grid);
                    break;
                case TwoPlayer:
                default:
                    controller = new RegularCheckersController(Main.gameView, dim, Main.gameView.grid);
                    break;

            }

            Settings.set(Setting.Controller, controller);
            Main.setView((Main.gameView));
        });

        // Game type buttons
        ToggleButton simpDamBtn = constructGameButton(GameType.SimpDam, toggleGroup);
        ToggleButton twoPlayerBtn = constructGameButton(GameType.TwoPlayer, toggleGroup);
        ToggleButton singlePlayerBtn = constructGameButton(GameType.SinglePlayer, toggleGroup);
        ToggleButton flexibleKingBtn = constructGameButton(GameType.FlexibleKingTwoPlayer, toggleGroup);


        //loader buttons:
        GameType[] gameTypes = GameType.values();
        for (int i = 0; i < gameTypes.length; i++) {
            this.constructLoadButton(gameTypes[i], i);
        }

        TextField showSlider = new TextField(String.valueOf(dimensionSlider.getValue()));
        showSlider.setMaxSize(50, 50);

        dimensionSlider.setOnMouseClicked(e -> showSlider.setText(String.valueOf(dimensionSlider.getValue())));
        dimensionSlider.setOnMouseDragged(e -> showSlider.setText(String.valueOf(dimensionSlider.getValue())));

        showSlider.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double value = Double.parseDouble(newValue);
                dimensionSlider.setValue(value);
            } catch (Exception e) {
                if (MainMenuView.selectedGameType == GameType.SimpDam) {
                    dimensionSlider.setValue(3);
                } else {
                    dimensionSlider.setValue(8);
                }
            }
        });

        Label dimensionOfField = new Label("Set the field dimension:");
        dimensionOfField.setStyle(" -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                " -fx-background-image: url(/assets/dark_wood.jpg); -fx-border-color: #DAA520; -fx-border-width: 5px;");
        dimensionOfField.setAlignment(Pos.CENTER);

        //Get the children for the slaugther!
        grid.getChildren().addAll(dimensionSlider);
        containSlider.getChildren().addAll(dimensionOfField, showSlider, dimensionSlider);
        root.getChildren().addAll(grid, informationContainer, containSlider);

        //align the root
        GridPane.setConstraints(grid, 0, 0);
        GridPane.setConstraints(informationContainer, 1, 0);
        GridPane.setConstraints(containSlider, 0, 1);

        //align the grid
        GridPane.setConstraints(simpDamBtn, 0, 0);
        GridPane.setConstraints(twoPlayerBtn, 0, 1);
        GridPane.setConstraints(singlePlayerBtn, 0, 2);
        GridPane.setConstraints(flexibleKingBtn, 0, 3);
        GridPane.setConstraints(playButton, 0, 5);


        return new Scene(root, GameView.WIDTH, GameView.HEIGHT, true, null);
    }

    public ToggleButton constructButton(String name, Object toggleGroupObject) {
        ToggleButton button = new ToggleButton(name);
        button.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                "-fx-border-color: #DAA520; -fx-border-width: 5px;");

        if (toggleGroupObject instanceof ToggleGroup) {
            ToggleGroup toggleGroup = (ToggleGroup) toggleGroupObject;
            toggleGroup.getToggles().add(button);
        }


        this.grid.getChildren().add(button);

        return button;
    }

    public ToggleButton constructGameButton(GameType gameType, Object toggleGroupObject) {
        ToggleButton button = this.constructButton(gameType.name(), toggleGroupObject);

        if (toggleGroupObject instanceof ToggleGroup) {
            ToggleGroup toggleGroup = (ToggleGroup) toggleGroupObject;

            button.setOnMouseClicked(this.onMenuGameTypeButtonClicked);
            button.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    MainMenuView.selectedGameType = gameType;
                    button.setStyle("-fx-background-color: Green; -fx-cursor: hand;" +
                        " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                        "-fx-border-color: #DAA520; -fx-border-width: 5px;");
                } else {
                    MainMenuView.selectedGameType = null;
                    button.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                        " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                        "-fx-border-color: #DAA520; -fx-border-width: 5px;");
                }

                if (toggleGroup.getSelectedToggle() == null) {
                    this.playButton.setVisible(false);
                    this.containSlider.setVisible(false);
                } else {
                    this.playButton.setVisible(true);
                    this.containSlider.setVisible(true);
                }


                this.dimensionSlider.setMin(gameType == GameType.SimpDam ? 3 : 8);
            });
        }

        return button;
    }

    public void constructLoadButton(GameType gameType, int place) {
        Button button = new Button("Load Game");

        button.setStyle("-fx-background-image: url(/assets/grid.png); -fx-cursor: hand;" +
                " -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #7d6425;" +
                "-fx-border-color: #7d6425; -fx-border-width: 5px;");

        ObjectDB db = new ObjectDB();
        ObjectDB state = db.loadState(gameType.name());

        if (state == null) {
            return;
        }

        button.setOnMouseClicked(e -> {
            MainMenuView.selectedGameType = gameType;

            int dimension = (int) Settings.get(Setting.Dimension);

            AbstractController controller;
            switch (state.getSelectedGameType()) {
                case SimpDam:
                    controller = new SimpDamController(Main.gameView, dimension, Main.gameView.grid, state.getCheckerPieces(), state.getFields(), state.isWhiteTurn(), state.getActiveCount());
                    break;
                case SinglePlayer:
                    controller = new CPURegularCheckersController(Main.gameView, dimension, Main.gameView.grid, state.getCheckerPieces(), state.getFields(), state.isWhiteTurn(), state.getActiveCount());
                    break;
                case FlexibleKingTwoPlayer:
                    controller = new FlexibleKingController(Main.gameView, dimension, Main.gameView.grid, state.getCheckerPieces(), state.getFields(), state.isWhiteTurn(), state.getActiveCount());
                    break;
                case TwoPlayer:
                default:
                    controller = new RegularCheckersController(Main.gameView, dimension, Main.gameView.grid, state.getCheckerPieces(), state.getFields(), state.isWhiteTurn(), state.getActiveCount());
                    break;
            }

            Settings.set(Setting.Controller, controller);
            Main.setView(Main.gameView, state);
        });


        this.grid.getChildren().add(button);
        GridPane.setConstraints(button, 1, place);
    }

}
