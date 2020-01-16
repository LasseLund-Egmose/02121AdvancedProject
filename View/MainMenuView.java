package View;

import Boot.Main;
import Controller.*;
import Enum.GameType;
import Enum.Setting;
import Model.Settings;
import Persistence.ObjectDB;
import Util.Content;
import Util.StyleCollection;
import Util.StyleProp;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainMenuView extends AbstractView {

    // Selected game type (controller type)
    protected static GameType selectedGameType = null;

    // On game type clicked handler
    protected EventHandler<MouseEvent> onMenuGameTypeButtonClicked = e -> this.updateGameText();

    // Container elements
    protected GridPane buttonGrid;
    protected VBox dimensionSliderContainer;
    protected HBox informationContainer;
    protected VBox timeSliderContainer;

    // Visible (interactive) elements
    protected Slider dimensionSlider;
    protected Label dimensionSliderLabel;
    protected TextField dimensionSliderStatus;
    protected Text information;
    protected ToggleButton playButton;
    protected Slider timeSlider;
    protected Label timeSliderLabel;
    protected TextField timeSliderStatus;

    // Construct a button
    protected ToggleButton constructButton(String name, Object toggleGroupObject) {
        ToggleButton button = new ToggleButton(name);

        StyleCollection.buttonStyle(button);

        if (toggleGroupObject instanceof ToggleGroup) {
            ToggleGroup toggleGroup = (ToggleGroup) toggleGroupObject;
            toggleGroup.getToggles().add(button);
        }

        this.buttonGrid.getChildren().add(button);

        return button;
    }

    // Construct a game selection button
    protected ToggleButton constructGameButton(GameType gameType, Object toggleGroupObject) {
        ToggleButton button = this.constructButton(gameType.name(), toggleGroupObject);

        if (!(toggleGroupObject instanceof ToggleGroup)) {
            return button;
        }

        ToggleGroup toggleGroup = (ToggleGroup) toggleGroupObject;

        button.setOnMouseClicked(this.onMenuGameTypeButtonClicked);
        button.selectedProperty().addListener((observable, oldValue, newValue) -> {
            MainMenuView.selectedGameType = newValue ? gameType : GameType.Unselected;

            StyleCollection.modifyProps(
                    button,
                    StyleProp.BACKGROUND_COLOR(newValue ? "green" : null),
                    StyleProp.BACKGROUND_IMAGE(!newValue ? "url(/assets/dark_wood.jpg)" : null)
            );

            if (toggleGroup.getSelectedToggle() == null) {
                this.playButton.setVisible(false);
                this.dimensionSliderContainer.setVisible(false);
                this.timeSliderContainer.setVisible(false);
            } else {
                this.playButton.setVisible(true);
                this.dimensionSliderContainer.setVisible(true);
                this.timeSliderContainer.setVisible(true);
            }


            this.dimensionSlider.setMin(gameType == GameType.SimpDam ? 3 : 8);
        });

        return button;
    }

    // Construct a load game button
    protected void constructLoadButton(GameType gameType, int place) {
        Button button = new Button(Content.LOAD_GAME);
        StyleCollection.buttonStyleLight(button);

        ObjectDB db = new ObjectDB();
        ObjectDB state = db.loadState(gameType.name());

        if (state == null) {
            return;
        }

        button.setOnMouseClicked(e -> {
            MainMenuView.selectedGameType = gameType;

            int dimension = state.getDimension();

            // Set dimension in settings
            Settings.set(Setting.Dimension, dimension);

            AbstractController controller = this.initController(gameType, state);
            Settings.set(Setting.Controller, controller);

            Main.setView(Main.gameView, state);
        });


        this.buttonGrid.getChildren().add(button);
        GridPane.setConstraints(button, 1, place);
    }

    // Construct a number slider
    protected Slider constructSlider(int min, int max, int value, int prefWidth) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setPrefSize(prefWidth, 50);
        slider.setBlockIncrement(1);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        return slider;
    }

    // Construct a container for a number slider
    protected VBox constructSliderContainer() {
        VBox container = new VBox(5);
        container.setVisible(false);

        StyleCollection.mainMenuContainer(container);

        return container;
    }

    // Construct a label for a number slider
    protected Label constructSliderLabel(String text) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER);

        StyleCollection.labelStyle(label);

        return label;
    }

    // Return a double is String can be parsed or return default value instead
    protected double getDoubleOrDefault(String s, double d) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return d;
        }
    }

    // Simple initialization of a game controller based on selected
    protected AbstractController initController(GameType gameType) {
        return this.initController(gameType, null);
    }

    // Initialization of a game controller from a loaded game
    protected AbstractController initController(GameType gameType, Object stateObject) {
        boolean hasState = false;
        ObjectDB state = null;

        if(stateObject instanceof ObjectDB) {
            hasState = true;
            state = (ObjectDB) stateObject;
        }

        switch (gameType) {
            case SimpDam:
                return hasState ?
                        new SimpDamController(Main.gameView, Main.gameView.grid, state.getCheckerPieces(), state.getFields(), state.isWhiteTurn(), state.getActiveCount()) :
                        new SimpDamController(Main.gameView, Main.gameView.grid);
            case SinglePlayer:
                return hasState ?
                        new CPURegularCheckersController(Main.gameView, Main.gameView.grid, state.getCheckerPieces(), state.getFields(), state.isWhiteTurn(), state.getActiveCount()) :
                        new CPURegularCheckersController(Main.gameView, Main.gameView.grid);
            case FlexibleKingTwoPlayer:
                return hasState ?
                        new FlexibleKingController(Main.gameView, Main.gameView.grid, state.getCheckerPieces(), state.getFields(), state.isWhiteTurn(), state.getActiveCount()) :
                        new FlexibleKingController(Main.gameView, Main.gameView.grid);
            case TwoPlayer:
            default:
                return hasState ?
                        new RegularCheckersController(Main.gameView, Main.gameView.grid, state.getCheckerPieces(), state.getFields(), state.isWhiteTurn(), state.getActiveCount()) :
                        new RegularCheckersController(Main.gameView, Main.gameView.grid);
        }
    }

    // Setup text box with information on the right
    protected void setupInfoContainer() {
        this.informationContainer = new HBox();

        StyleCollection.mainMenuContainer(this.informationContainer);

        TextFlow textFlow = new TextFlow();
        textFlow.setMaxSize(200,50);
        textFlow.setMinSize(200,50);

        this.information = new Text(Content.WELCOME);

        StyleCollection.build(
                this.information,
                StyleProp.FONT("20 Verdana")
        );

        textFlow.getChildren().add(this.information);
        this.informationContainer.getChildren().add(textFlow);
    }

    // Setup game type selection buttons
    protected void setupGameTypeButtons(ToggleGroup toggleGroup) {
        GameType[] gameTypes = GameType.values();
        for (int i = 0; i < gameTypes.length-1; i++) {
            GameType g = gameTypes[i];

            if(g == GameType.Unselected) {
                continue;
            }

            // Create game button
            ToggleButton gameButton = constructGameButton(g, toggleGroup);
            GridPane.setConstraints(gameButton, 0, i);

            // Create load game button
            this.constructLoadButton(gameTypes[i], i);
        }
    }

    // Setup play button setting dimension, controller and changing game view
    protected void setupPlayButton() {
        this.playButton = this.constructButton(Content.NEW_GAME, null);
        this.playButton.setVisible(false);
        this.playButton.setOnMouseClicked(e -> {
            Settings.set(Setting.Dimension, dimensionSlider.getValue());
            Settings.set(Setting.Time, timeSlider.getValue()*60);

            AbstractController controller = this.initController(MainMenuView.selectedGameType);
            Settings.set(Setting.Controller, controller);
            Main.setView((Main.gameView));
        });
    }

    // Setup sliders for board dimension and time limit in-game
    protected void setupSliders(int dimension) {
        this.dimensionSlider = this.constructSlider(8, 100, dimension, 1000);
        this.timeSlider = this.constructSlider(1, 30, 5, 400);

        this.dimensionSliderContainer = this.constructSliderContainer();
        this.timeSliderContainer = this.constructSliderContainer();

        // Setup static labels
        this.dimensionSliderLabel = this.constructSliderLabel("Set board dimensions:");
        this.timeSliderLabel = this.constructSliderLabel("Set time constraints:");

        // Setup status number fields
        this.dimensionSliderStatus = new TextField(String.valueOf((int)dimensionSlider.getValue()));
        this.timeSliderStatus = new TextField(String.valueOf((int)timeSlider.getValue()));

        // Setup number field constraints
        this.dimensionSliderStatus.setMaxSize(50, 50);
        this.timeSliderStatus.setMaxSize(50, 50);

        // Update status number field when dragging sliders
        dimensionSlider.setOnMouseClicked(e -> this.dimensionSliderStatus.setText(String.valueOf((int) dimensionSlider.getValue())));
        dimensionSlider.setOnMouseDragged(e -> this.dimensionSliderStatus.setText(String.valueOf((int) dimensionSlider.getValue())));
        timeSlider.setOnMouseClicked(e -> this.timeSliderStatus.setText(String.valueOf((int) timeSlider.getValue())));
        timeSlider.setOnMouseDragged(e -> this.timeSliderStatus.setText(String.valueOf((int) timeSlider.getValue())));

        // Update dimensionSlider when editing number field
        this.dimensionSliderStatus.textProperty().addListener((observable, oldValue, newValue) -> {
            double newDoubleValue = this.getDoubleOrDefault(newValue, 8);
            dimensionSlider.setValue(newDoubleValue);
        });

        // Update timeSlider when editing number field
        this.timeSliderStatus.textProperty().addListener((observable, oldValue, newValue) -> {
            double newDoubleValue = this.getDoubleOrDefault(newValue, 1);
            timeSlider.setValue(newDoubleValue);
        });
    }

    // Update descriptive text about currently selected game type
    protected void updateGameText() {
        String text;

        switch (MainMenuView.selectedGameType) {
            case SimpDam:
                text = Content.GAME_DESCRIPTION_SIMP_DAM;
                break;
            case TwoPlayer:
                text = Content.GAME_DESCRIPTION_TWO_PLAYER;
                break;
            case SinglePlayer:
                text = Content.GAME_DESCRIPTION_SINGLE_PLAYER;
                break;
            case FlexibleKingTwoPlayer:
                text = Content.GAME_DESCRIPTION_FLEXIBLE_KING_TWO_PLAYER;
                break;
            default:
                text = Content.GAME_DESCRIPTION_NONE;
                break;
        }

        this.information.setText(text);
    }

    // Construct
    public MainMenuView(String[] args) {
        super(args);
    }

    // Get board dimension from program arguments
    public int getDimensionFromArgs() {
        if (this.args.length == 1) {
            int newN = Integer.parseInt(this.args[0]);

            if (newN >= 3 && newN <= 100) {
                return newN;
            }
        }

        return 8;
    }

    // Window title
    public String getTitle() {
        return Content.TITLE_MAIN_MENU_VIEW;
    }

    // Setup scene
    public Scene setupScene() {
        int dimension = this.getDimensionFromArgs(); // Get default dimension from program args (8 if not present)

        // Setup root pane with styling
        GridPane root = new GridPane();
        root.setAlignment(Pos.TOP_LEFT);
        root.setHgap(30);
        root.setVgap(30);
        root.setPadding(new Insets(35));

        StyleCollection.build(
                root,
                StyleProp.BACKGROUND_COLOR("antiquewhite"),
                StyleProp.BORDER_COLOR("#DAA520"),
                StyleProp.BORDER_WIDTH("5px")
        );

        // Setup grid for game buttons
        this.buttonGrid = new GridPane();
        this.buttonGrid.setHgap(50);
        this.buttonGrid.setVgap(30);

        // Setup text box containing relevant text
        this.setupInfoContainer();

        // Setup buttons
        ToggleGroup toggleGroup = new ToggleGroup();
        this.setupGameTypeButtons(toggleGroup);
        this.setupPlayButton();

        // Setup sliders for board dimension and time limit
        this.setupSliders(dimension);

        // Add relevant children to parents
        this.dimensionSliderContainer.getChildren().addAll(this.dimensionSliderLabel, this.dimensionSliderStatus, dimensionSlider);
        this.timeSliderContainer.getChildren().addAll(this.timeSliderLabel, this.timeSliderStatus, timeSlider);
        root.getChildren().addAll(this.buttonGrid, this.informationContainer, this.dimensionSliderContainer, this.timeSliderContainer);

        // Set grid constraints for items in grids
        GridPane.setConstraints(this.buttonGrid, 0, 0);
        GridPane.setConstraints(this.dimensionSliderContainer, 0, 1);
        GridPane.setConstraints(this.informationContainer, 1, 0);
        GridPane.setConstraints(this.playButton, 0, GameType.values().length);
        GridPane.setConstraints(this.timeSliderContainer, 1, 1);

        // Return newly created scene
        return new Scene(root, GameView.WIDTH, GameView.HEIGHT, true, null);
    }

}
