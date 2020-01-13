package View;

import Boot.Main;
import Controller.AbstractController;
import Controller.RegularCheckersController;
import Controller.SimpDamController;
import Enum.Setting;
import Model.Settings;
import Persistence.ObjectDB;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class MainMenuView extends AbstractView {

    protected enum controller {
        SimpDamController,
        RegularCheckersController
    }

    protected controller selectedController = controller.SimpDamController;
    protected ToggleButton playButton;
    protected Slider dimensionSlider;
    protected VBox containSlider;
    protected GridPane grid;
    protected String[] loadNames = new String[]{"SimpDam","RegDam","AIDam","IntDam"};
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
        Text information = new Text("Hej med dig jeg er kaj \nKaj det er mig!");
        informationContainer.getChildren().add(information);


        dimensionSlider = new Slider(8, 100, dimension);
        dimensionSlider.setShowTickMarks(true);
        dimensionSlider.setShowTickLabels(true);
        dimensionSlider.setPrefSize(1000,50);
        dimensionSlider.setBlockIncrement(1);
        dimensionSlider.setMajorTickUnit(1);
        dimensionSlider.setMinorTickCount(0);
        dimensionSlider.setSnapToTicks(true);

        ToggleGroup toggleGroup = new ToggleGroup();

        this.playButton = constructButton("New Game",grid,toggleGroup);
        this.playButton.setVisible(false);
        this.playButton.setOnMouseClicked( e -> {
            Settings.set(Setting.Dimension,(int)dimensionSlider.getValue());
            if (this.selectedController == controller.SimpDamController) {
                Settings.set(Setting.Controller,
                        new SimpDamController(Main.gameView,(int) Settings.get(Setting.Dimension),Main.gameView.grid));
            } else if (this.selectedController == controller.RegularCheckersController) {
                Settings.set(Setting.Controller, new RegularCheckersController(Main.gameView,
                        (int) Settings.get(Setting.Dimension),Main.gameView.grid));
            }
            Main.setView((Main.gameView));
        });

        //regular checkers
        ToggleButton twoPlayer = constructButton("Two player dam",grid,toggleGroup);
        twoPlayer.setOnMouseClicked(e ->{
            this.selectedController = controller.RegularCheckersController;
        });
        
        ToggleButton vsAI = constructButton("Single player dam",grid,toggleGroup);
        vsAI.setOnMouseClicked( e ->{
            System.out.println("Not made yet");
        });

        ToggleButton international = constructButton("International dam",grid,toggleGroup);
        international.setOnMouseClicked( e ->{
            System.out.println("Not made yet");
        });

        //simpel checkers
        ToggleButton SimpDam = constructButton("Simpdam",grid,toggleGroup);
        SimpDam.setOnMouseClicked( e ->{
            this.selectedController = controller.SimpDamController;
            dimensionSlider.setMin(3);
        });

        //loader buttons:
        for(int i=0; i<loadNames.length; i++){
            constructLoadButton(loadNames[i],i);
        }

        TextField showSlider = new TextField("" + (int)dimensionSlider.getValue());
        showSlider.setMaxSize(50,50);

        Settings.set(Setting.Dimension, dimension);

        dimensionSlider.setOnMouseDragged(e -> {
            showSlider.setText("" + (int)dimensionSlider.getValue());
            Settings.set(Setting.Dimension,(int)dimensionSlider.getValue());
        });

        dimensionSlider.setOnMouseClicked(e -> {
            showSlider.setText("" + (int)dimensionSlider.getValue());
            Settings.set(Setting.Dimension,(int)dimensionSlider.getValue());
        });


        showSlider.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double value = Double.parseDouble(newValue);
                    dimensionSlider.setValue(value);
                    Settings.set(Setting.Dimension,(int)value);
                } catch ( Exception e) {
                    if (this.selectedController == controller.SimpDamController) {
                        dimensionSlider.setValue(3);
                        Settings.set(Setting.Dimension,3);
                    } else {
                        dimensionSlider.setValue(8);
                        Settings.set(Setting.Dimension,8);
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

    public ToggleButton constructButton(String name,GridPane grid,ToggleGroup toggleGroup) {
        ToggleButton button = new ToggleButton(name);
        button.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                "-fx-border-color: #DAA520; -fx-border-width: 5px;");
        button.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                button.setStyle("-fx-background-color: Green; -fx-cursor: hand;" +
                        " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                        "-fx-border-color: #DAA520; -fx-border-width: 5px;");
            } else {
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
            this.dimensionSlider.setMin(8);
        });
        toggleGroup.getToggles().add(button);
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

    public Pane constructSettingsRoot(GridPane grid){
        return  grid;
    }

}
