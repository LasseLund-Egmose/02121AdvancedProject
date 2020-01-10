package View;

import Boot.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

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
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button SimpDam = new Button("SimpDam");
        SimpDam.setOnMouseClicked( e ->{
            Main.setView(Main.gameView);
        });
        grid.getChildren().add(SimpDam);

        grid.setStyle("-fx-background-color: red");

        return new Scene(grid, GameView.WIDTH, GameView.HEIGHT, true, null);
    }
}
