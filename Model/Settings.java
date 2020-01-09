package Model;

import Controller.AbstractController;

public class Settings {
    protected AbstractController controller;

    public void setController(AbstractController controller) {
        this.controller=controller;
    }
    public AbstractController getController(){
        return this.controller;
    }

}

