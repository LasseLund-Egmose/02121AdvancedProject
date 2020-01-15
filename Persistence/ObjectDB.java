package Persistence;

import Enum.GameType;
import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import javafx.scene.layout.StackPane;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectDB implements Serializable {

    // TODO: Description of method of saving game

    private static final long serialVersionUID = -7307863873494379286L;

    protected ArrayList<CheckerPiece> checkerPieces = new ArrayList<>(); // A list of all pieces
    protected HashMap<Integer, HashMap<Integer, Field>> fields = new HashMap<>(); // A map (x -> y -> pane) of all fields

    protected HashMap<Team, Integer> activeCount = new HashMap<>(); // A map (Team -> int) of number of active pieces on each team

    protected int dimension;
    protected boolean isWhiteTurn; // Keep track of turn

    // Time
    protected int timeBlack;
    protected int timeWhite;
    protected int totalTime;

    /*
     * Setters
     */
    public void setActiveCount(HashMap<Team, Integer> activeCount) {
        this.activeCount = activeCount;
    }

    public void setCheckerPieces(ArrayList<CheckerPiece> checkerPieces) {
        this.checkerPieces = checkerPieces;
    }

    public void setFields(HashMap<Integer, HashMap<Integer, Field>> fields) {
        this.fields = fields;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        isWhiteTurn = whiteTurn;
    }

    public void setTimeWhite(int timeWhite) {
        this.timeWhite = timeWhite;
    }

    public void setTimeBlack(int timeBlack) {
        this.timeBlack = timeBlack;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /*
     * Getters
     */
    public HashMap<Team, Integer> getActiveCount() {
        return this.activeCount;
    }

    public ArrayList<CheckerPiece> getCheckerPieces() {
        return this.checkerPieces;
    }

    public HashMap<Integer, HashMap<Integer, Field>> getFields() {
        return this.fields;
    }

    public boolean isWhiteTurn() {
        return this.isWhiteTurn;
    }

    public int getTimeWhite() {
        return this.timeWhite;
    }

    public int getTimeBlack() {
        return this.timeBlack;
    }

    public int getTotalTime() {
        return this.totalTime;
    }

    public int getDimension() {
        return this.dimension;
    }

    // Serialize and save state to a file
    public boolean saveState(String filename) {

        File dir = new File(FileSystemView.getFileSystemView().getHomeDirectory() + "/CheckerSaves");
        if (!dir.exists()) {
            dir.mkdir();
        }

        // Create an object stream and write a file with the state of this
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FileSystemView.getFileSystemView().getHomeDirectory() + "/CheckerSaves/" + filename))) {
            oos.writeObject(this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // deserialize file and load data into state
    public ObjectDB loadState(String filename) {
        // Create an object stream from file and load that into an instance of ObjectDB
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileSystemView.getFileSystemView().getHomeDirectory() + "/CheckerSaves/" + filename))) {
            // Read file and initialise a new instance of ObjectDB
            ObjectDB db = (ObjectDB) ois.readObject();

            ArrayList<CheckerPiece> checkerPieces = db.getCheckerPieces();

            // For each checker piece -> set cylinder container to a new StackPane and setup piece
            for (CheckerPiece piece : checkerPieces) {
                piece.setCylinderContainer(new StackPane());
                piece.setupPiece();
            }

            // return the db instance
            return db;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}