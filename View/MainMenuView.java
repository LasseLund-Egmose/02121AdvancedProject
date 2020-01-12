package View;

import Boot.Main;
import Controller.RegularCheckersController;
import Controller.SimpDamController;
import Enum.Setting;
import Model.Settings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class MainMenuView extends AbstractView {

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

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(50);
        grid.setVgap(30);
        grid.setPadding(new Insets(35, 35, 35, 35));

        Button SimpDam = constructButton("Simpdam");
        SimpDam.setOnMouseClicked( e ->{
            Settings.set(Setting.Controller, new SimpDamController(Main.gameView,(int)Settings.get(Setting.Dimension),Main.gameView.grid));
            Main.setView(Main.gameView);
        });

        Button twoPlayer = constructButton("Two player dam");
        int finalDimension = dimension;
        twoPlayer.setOnMouseClicked(e ->{
            Settings.set(Setting.Controller, new RegularCheckersController(Main.gameView,(int)Settings.get(Setting.Dimension),Main.gameView.grid));
            Main.setView(Main.gameView);
        });

        Button vsAI = constructButton("Single player dam");
        vsAI.setOnMouseClicked( e ->{
            System.out.println("Not made yet");
        });

        Button international = constructButton("International dam");
        international.setOnMouseClicked( e ->{
            System.out.println("Not made yet");
        });

        VBox containSlider = new VBox();
        containSlider.setStyle("-fx-border-image-source: url(/assets/dark_wood.jpg);" +
                " -fx-border-image-width: 10; -fx-border-image-slice: 10");


        Slider tileSize = new Slider(3, 100, dimension);
        tileSize.setShowTickMarks(true);
        tileSize.setShowTickLabels(true);
        tileSize.setPrefSize(1000,50);
        tileSize.setBlockIncrement(1);
        tileSize.setMajorTickUnit(1);
        tileSize.setMinorTickCount(0);
        tileSize.setSnapToTicks(true);

        TextField showTileSize = new TextField("" + (int)tileSize.getValue());
        showTileSize.setMaxSize(50,50);

        Settings.set(Setting.Dimension, dimension);



        tileSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            showTileSize.setText("" + (int)tileSize.getValue());
            Settings.set(Setting.Dimension,(int)tileSize.getValue());
        });


        showTileSize.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double value = Double.parseDouble(newValue);
                    tileSize.setValue(value);
                    Settings.set(Setting.Dimension,(int)value);
                } catch ( Exception e) {
                    tileSize.setValue(3);
                    Settings.set(Setting.Dimension,3);
                }
        });


        grid.getChildren().addAll(SimpDam,twoPlayer,vsAI,international,tileSize,containSlider);
        containSlider.getChildren().addAll(showTileSize,tileSize);
        grid.setStyle("-fx-background-color: antiquewhite; -fx-border-color: #DAA520; -fx-border-width: 5px;");

        GridPane.setConstraints(twoPlayer,0,1);
        GridPane.setConstraints(vsAI, 0, 2);
        GridPane.setConstraints(international, 0, 3);
        GridPane.setConstraints(containSlider,0,8);




        return new Scene(grid, GameView.WIDTH, GameView.HEIGHT, true, null);
    }

    public Button constructButton(String name) {
        Button button = new Button(name);
        button.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                "-fx-border-color: #DAA520; -fx-border-width: 5px;");
        return button;
    }

    public void constructSettingsRoot(){

    }

}
