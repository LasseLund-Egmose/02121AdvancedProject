package Boot;

import Model.Settings;
import Persistence.ObjectDB;
import View.AbstractView;
import View.GameView;
import View.MainMenuView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    protected static Stage primaryStage;

    public static GameView gameView;
    public static MainMenuView mainMenuView;

    protected static void setScene(Scene s) {
        Main.primaryStage.setScene(s);
        Main.primaryStage.show();
    }

    public static void initViews(String[] args) {
        Main.mainMenuView = new MainMenuView(args);
        Main.gameView = new GameView(args);
    }

    public static void main(String[] args) {
        Main.initViews(args);
        Main.launch(args);
    }

    public static void setView(AbstractView view) {
        Main.setScene(view.setupScene());
        Main.primaryStage.setTitle(view.getTitle());
    }

    public static void setGameView(ObjectDB state) {
        Main.setScene(Main.gameView.setupScene(state));
    }

    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;

        // Set initial view
        Main.setView(Main.mainMenuView);
    }
}
