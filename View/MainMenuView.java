package View;

import Boot.Main;
import Controller.RegularCheckersController;
import Controller.SimpDamController;
import Enum.Setting;
import Model.Settings;
import Persistence.ObjectDB;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MainMenuView extends AbstractView {

    protected enum controller {
        SimpDamController,
        RegularCheckersController,
        FlexibleKingController
    }

    protected controller selectedController = controller.SimpDamController;
    protected ToggleButton playButton;
    protected Slider dimensionSlider;
    protected VBox containSlider;
    protected GridPane grid;
    protected Text information;
    protected String[] loadNames = new String[]{"SimpDam","TwoPlayer","SingelPlayer","FlexibleKing"};
    protected static String selectedButton = null;
    protected ObjectDB[] gameStates = new ObjectDB[4];


    // Title
    public String title = "Main Menu";

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
        dimensionSlider.setPrefSize(1000,50);
        dimensionSlider.setBlockIncrement(1);
        dimensionSlider.setMajorTickUnit(1);
        dimensionSlider.setMinorTickCount(0);
        dimensionSlider.setSnapToTicks(true);

        ToggleGroup toggleGroup = new ToggleGroup();

        //Play button setting dimension, controller and changing game view
        this.playButton = constructButton("New Game",grid,null);
        this.playButton.setVisible(false);
        this.playButton.setOnMouseClicked( e -> {
            Settings.set(Setting.Dimension,(int)dimensionSlider.getValue());
            if (MainMenuView.selectedButton.equals(this.loadNames[0])) {
                Settings.set(Setting.Controller,
                        new SimpDamController(Main.gameView,(int) Settings.get(Setting.Dimension),Main.gameView.grid));
            } else if (MainMenuView.selectedButton.equals(this.loadNames[1])) {
                Settings.set(Setting.Controller, new RegularCheckersController(Main.gameView,
                        (int) Settings.get(Setting.Dimension),Main.gameView.grid));
            }
            Main.setView((Main.gameView));
        });

        //simpel checkers
        ToggleButton SimpDam = constructButton(loadNames[0],grid,toggleGroup);
        SimpDam.setOnMouseClicked( e ->{
            this.selectedController = controller.SimpDamController;
            changetext();
        });

        //regular checkers
        ToggleButton twoPlayer = constructButton(loadNames[1],grid,toggleGroup);
        twoPlayer.setOnMouseClicked(e ->{
            this.selectedController = controller.RegularCheckersController;
            changetext();
        });
        
        ToggleButton vsAI = constructButton(loadNames[2],grid,toggleGroup);
        vsAI.setOnMouseClicked( e ->{
            System.out.println("Not made yet");
            changetext();
        });

        ToggleButton international = constructButton(loadNames[3],grid,toggleGroup);
        international.setOnMouseClicked( e ->{
            System.out.println("Not made yet");
            changetext();
        });


        //loader buttons:
        for(int i=0; i<loadNames.length; i++){
            constructLoadButton(loadNames[i],i);
        }

        TextField showSlider = new TextField("" + (int)dimensionSlider.getValue());
        showSlider.setMaxSize(50,50);


        dimensionSlider.setOnMouseDragged(e -> {
            showSlider.setText("" + (int)dimensionSlider.getValue());
        });

        dimensionSlider.setOnMouseClicked(e -> {
            showSlider.setText("" + (int)dimensionSlider.getValue());
        });


        showSlider.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double value = Double.parseDouble(newValue);
                    dimensionSlider.setValue(value);
                } catch ( Exception e) {
                    if (this.selectedController == controller.SimpDamController) {
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
        containSlider.getChildren().addAll(dimensionOfField,showSlider,dimensionSlider);
        root.getChildren().addAll(grid,informationContainer,containSlider);

        //align the root
        GridPane.setConstraints(grid,0,0);
        GridPane.setConstraints(informationContainer,1,0);
        GridPane.setConstraints(containSlider,0,1);

        //align the grid
        GridPane.setConstraints(twoPlayer,0,1);
        GridPane.setConstraints(vsAI, 0, 2);
        GridPane.setConstraints(international, 0, 3);
        GridPane.setConstraints(playButton,0,5);


        return new Scene(root, GameView.WIDTH, GameView.HEIGHT, true, null);
    }

    public ToggleButton constructButton(String name,GridPane grid,Object toggleGroupObject) {
        ToggleButton button = new ToggleButton(name);
        button.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                "-fx-border-color: #DAA520; -fx-border-width: 5px;");

        if(toggleGroupObject instanceof ToggleGroup) {
            ToggleGroup toggleGroup = (ToggleGroup) toggleGroupObject;
            toggleGroup.getToggles().add(button);

            button.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    MainMenuView.selectedButton=name;
                    button.setStyle("-fx-background-color: Green; -fx-cursor: hand;" +
                            " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                            "-fx-border-color: #DAA520; -fx-border-width: 5px;");
                } else {
                    MainMenuView.selectedButton=null;
                    button.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                            " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                            "-fx-border-color: #DAA520; -fx-border-width: 5px;");
                }
                if (toggleGroup.getSelectedToggle()==null) {
                    this.playButton.setVisible(false);
                    this.containSlider.setVisible(false);
                } else {
                    this.playButton.setVisible(true);
                    this.containSlider.setVisible(true);
                }
                if(name.equals(loadNames[0]) || name.equals("New Game")) {
                    this.dimensionSlider.setMin(3);

                } else {
                    this.dimensionSlider.setMin(8);
                }

            });
        }


        this.grid.getChildren().add(button);

        return button;
    }

    public void constructLoadButton(String fileName, int place) {
        Button button = new Button("Load Game");

        ObjectDB db =new ObjectDB();
        ObjectDB state = db.loadState(fileName);

        if (state==null) {
            return;
        }

        button.setOnMouseClicked( e -> {
            Main.setGameView(state);
        });



        this.grid.getChildren().add(button);
        GridPane.setConstraints(button,1,place);
    }

    protected void changetext() {
        if (MainMenuView.selectedButton == null) {
            this.information.setText("You don´t wanna play with me?? OwO");
        } else if (MainMenuView.selectedButton.equals(this.loadNames[0])) { //Simpel Checkers
            this.information.setText("This is the simplest version of checker. In this version each player has exactly " +
                    "one piece each, starting at opposite corners of the board. However, in this version every " +
                    "checker piece acts as a king piece, meaning it is not locked to only moving forward.");
        } else if (MainMenuView.selectedButton.equals(this.loadNames[1])) { //RegularCheckers
            this.information.setText("This is the normal version of checkers. For two players." +
                    " Every player has pieces on the three first rows of the board on their own respective side. " +
                    "All piece can only move forward, unless they become king pieces " +
                    "(reach the end of the opposing players side).");
        } else if (MainMenuView.selectedButton.equals(this.loadNames[2])) { //AI
            this.information.setText("This is the Single player version of checkers, where you play against an AI. " +
                    "The rules are the same as for regular checkers.");
        } else if (MainMenuView.selectedButton.equals(this.loadNames[3])) { //International checkers
            this.information.setText("This is the international version of checkers. For two players. " +
                    "This version has the international rules " +
                    "meaning you can move the pieces an arbitrary amount of spaces. ");
        }
    }

}
