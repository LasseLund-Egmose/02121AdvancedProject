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
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button SimpDam = constructButton("Two player dam");
        SimpDam.setOnMouseClicked( e ->{
            Main.setView(Main.gameView);
        });
        grid.getChildren().add(SimpDam);

        grid.setStyle("-fx-background-color: antiquewhite; -fx-border-color: #DAA520; -fx-border-width: 5px;");

        return new Scene(grid, GameView.WIDTH, GameView.HEIGHT, true, null);
    }

    public Button constructButton(String name) {
        Button button = new Button(name);
        button.setStyle("-fx-background-image: url(/assets/dark_wood.jpg); -fx-cursor: hand;" +
                " -fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DAA520;" +
                "-fx-border-color: #DAA520; -fx-border-width: 5px;");
        return button;
    }


    public void setSettings(){

    }



}
