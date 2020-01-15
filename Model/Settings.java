package Model;

import Controller.AbstractController;
import Enum.Setting;

import java.util.HashMap;

public class Settings {

    protected AbstractController controller; // Reference to active game controller
    protected static HashMap<Setting, Object> settings = new HashMap<>(); // Key -> Value map of game settings

    /*
     * Static getters and setters
     */

    public static Object get(Setting key) {
        return Settings.settings.getOrDefault(key, null);
    }

    public static int getInt(Setting key) {
        return ((Number) Settings.settings.get(key)).intValue();
    }

    public static void set(Setting key, Object value) {
        Settings.settings.put(key, value);
    }

    /*
     * Getters and setters
     */

    public AbstractController getController() {
        return this.controller;
    }

    public void setController(AbstractController controller) {
        this.controller = controller;
    }

}

