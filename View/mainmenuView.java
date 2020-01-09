package View;

import Controller.SimpDamController;
import Model.Setting;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class mainmenuView {

    public static String[] args;

    public Scene setupMainmenuScene() {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button SimpDam = new Button("SimpDam");
        SimpDam.setOnMouseClicked( e ->{
            //mainview.setSettings(new SimpDamController(this, this.dimension, this.grid););
            MainView.changeToGameScene();
        });
        grid.getChildren().add(SimpDam);

        grid.setStyle("-fx-background-color: red");
        Scene scene = new Scene(grid, View.WIDTH, View.HEIGHT, true, null);
        return scene;
    }
}
