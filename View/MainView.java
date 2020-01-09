package View;

import Model.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class MainView extends Application {
   protected MainMenuView mainMenuView;
   protected boolean menuView = true;
   protected static View view;
   protected Settings settings;
   protected static Stage primaryStage;

    public static void main(String[] args) {
        View.args = args;

        launch(args);
    }

    public void start(Stage primaryStage) {
        MainView.primaryStage = primaryStage;
        MainView.view = new View();

        this.mainMenuView = new MainMenuView();

        primaryStage.setScene(mainMenuView.setupMainMenuScene());
        primaryStage.show();
    }

    public Scene changeScene() {
        MainView.primaryStage.setTitle(menuView ? "Main Menu" : "SimpDam");
        return menuView ? mainMenuView.setupMainMenuScene() : view.setupGameScene();
    }

    public static void changeToGameScene(){
        MainView.primaryStage.setScene(view.setupGameScene());
        MainView.primaryStage.show();
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void toggleMenuView(){
        this.menuView = !this.menuView;
    }
}
