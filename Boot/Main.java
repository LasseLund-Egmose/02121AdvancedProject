package Boot;

import Persistence.ObjectDB;
import View.AbstractView;
import View.GameView;
import View.MainMenuView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    protected static Stage primaryStage; // Application stage

    /*
     * Application views
     */

    public static GameView gameView;
    public static MainMenuView mainMenuView;

    /*
     * Static helpers
     */

    protected static void initViews(String[] args) {
        Main.mainMenuView = new MainMenuView(args);
        Main.gameView = new GameView(args);
    }

    protected static void setScene(Scene s) {
        Main.primaryStage.setScene(s);
        Main.primaryStage.show();
    }

    /*
     * Application view (scene) setters
     */

    public static void setView(AbstractView view) {
        Main.setView(view, null);
    }

    public static void setView(AbstractView view, ObjectDB state) {
        Scene scene;

        if(view instanceof GameView && state != null) {
            GameView gameView = (GameView) view;
            scene = gameView.setupScene(state);
        } else {
            scene = view.setupScene();
        }

        Main.setScene(scene);
        Main.primaryStage.setTitle(view.getTitle());
    }

    /*
     * Application core methods
     */

    // Setup application from static context
    public static void main(String[] args) {
        Main.initViews(args);
        Main.launch(args);
    }

    // Setup stage and scene
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        Main.setView(Main.mainMenuView);
    }
}
