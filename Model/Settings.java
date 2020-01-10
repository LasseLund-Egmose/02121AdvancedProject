package Model;

import Controller.AbstractController;
import Enum.Setting;

import java.util.HashMap;

public class Settings {
    protected AbstractController controller;
    protected static HashMap<Setting,Object> settings = new HashMap<>();

    public void setController(AbstractController controller) {
        this.controller=controller;
    }
    public AbstractController getController(){
        return this.controller;
    }

    public static Object get(Setting key) {
        return Settings.settings.getOrDefault(key,null);
    }

    public static void set(Setting key, Object value) {
        Settings.settings.put(key,value);
    }
}

