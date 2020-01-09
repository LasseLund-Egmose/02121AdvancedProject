package View;

import Model.Setting;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class MainView extends Application {
   protected mainmenuView mainmenuView;
   protected boolean menuView = true;
   protected static View view;
   protected Setting settings;
   protected static Stage primaryStage;

    public static void main(String[] args) {
        View.args = args;

        launch(args);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.mainmenuView = new mainmenuView();
        this.view = new View();
        primaryStage.setScene(mainmenuView.setupMainmenuScene());
        //primaryStage.setScene(view.setupGameScene());
        primaryStage.show();
    }

    public Scene changeScene() {
        if (menuView) {
            this.primaryStage.setTitle("Mainmenu");
            return mainmenuView.setupMainmenuScene();
        } else {
            this.primaryStage.setTitle("SimpDam");
            return view.setupGameScene();
        }
    }

    public static void changeToGameScene(){
        primaryStage.setScene(view.setupGameScene());
        primaryStage.show();
    }

    public void flipMenuView(){
        this.menuView = !this.menuView;
    }

    public void setSettings(Setting settings) {
        this.settings = settings;
    }

}
