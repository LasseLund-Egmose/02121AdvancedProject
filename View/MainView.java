package View;

import Model.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class MainView extends Application {

    protected static GameView gameView;
    protected static MainMenuView mainMenuView;
    protected static Stage primaryStage;

    protected boolean menuView = true;
    protected Settings settings;

    protected static void changeToScene(Scene s) {
        MainView.primaryStage.setScene(s);
        MainView.primaryStage.show();
    }

    public static void changeToGameScene() {
        Scene game = MainView.gameView.setupGameScene();
        MainView.changeToScene(game);
    }

    public static void changeToMainMenuScene() {
        Scene mainMenu = MainView.mainMenuView.setupMainMenuScene();
        MainView.changeToScene(mainMenu);
    }

    public static void main(String[] args) {
        GameView.args = args;

        launch(args);
    }

    public void start(Stage primaryStage) {
        MainView.primaryStage = primaryStage;

        MainView.mainMenuView = new MainMenuView();
        MainView.gameView = new GameView();

        MainView.changeToMainMenuScene();
    }

    public Scene changeScene() {
        MainView.primaryStage.setTitle(menuView ? "Main Menu" : "SimpDam");
        return menuView ? MainView.mainMenuView.setupMainMenuScene() : MainView.gameView.setupGameScene();
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void toggleMenuView(){
        this.menuView = !this.menuView;
    }
}
