package View;

import javafx.scene.Scene;

abstract public class AbstractView {

    abstract public String getTitle();
    abstract public Scene setupScene();

    public String[] args; // Program arguments

    public AbstractView(String[] args) {
        this.args = args;
    }

}
