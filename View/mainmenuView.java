package View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class MainMenuView {

    public static String[] args;

    public Scene setupMainMenuScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button SimpDam = new Button("SimpDam");
        SimpDam.setOnMouseClicked( e ->{
            MainView.changeToGameScene();
        });
        grid.getChildren().add(SimpDam);

        grid.setStyle("-fx-background-color: red");

        return new Scene(grid, View.WIDTH, View.HEIGHT, true, null);
    }
}
