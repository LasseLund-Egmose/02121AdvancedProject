package Model;

import Controller.AbstractController;

public class Setting {
    protected AbstractController controller;

    public void setController(AbstractController controller) {
        this.controller=controller;
    }
    public AbstractController getController(){
        return this.controller;
    }

}

