package Persistence;

import Enum.Team;

import Model.CheckerPiece;
import Model.Field;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectDB implements Serializable {
    private static final long serialVersionUID = 5864896800675704551L;

    protected ArrayList<CheckerPiece> checkerPieces = new ArrayList<>(); // A list of all pieces
    protected HashMap<Integer, HashMap<Integer, Field>> fields = new HashMap<>(); // A map (x -> y -> pane) of all fields

    protected HashMap<Team, Integer> activeCount = new HashMap<>(); // A map (Team -> int) of number of active pieces on each team

    protected int dimension; // Dimension of board
    protected boolean isWhiteTurn = true; // Keep track of turn


    // SETTERS
    public void setActiveCount(HashMap<Team, Integer> activeCount) {
        this.activeCount = activeCount;
    }
    public void setCheckerPieces(ArrayList<CheckerPiece> checkerPieces) {
        this.checkerPieces = checkerPieces;
    }
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    public void setFields(HashMap<Integer, HashMap<Integer, Field>> fields) {
        this.fields = fields;
    }
    public void setWhiteTurn(boolean whiteTurn) {
        isWhiteTurn = whiteTurn;
    }

    // GETTERS
    public HashMap<Team, Integer> getActiveCount() {
        return activeCount;
    }
    public ArrayList<CheckerPiece> getCheckerPieces() {
        return checkerPieces;
    }
    public int getDimension() {
        return dimension;
    }
    public HashMap<Integer, HashMap<Integer, Field>> getFields() {
        return fields;
    }
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }


    // Serialize and save state to a file
    public void saveState(String filename) {

        ArrayList<CheckerPiece> copy = new ArrayList<>();
        HashMap<Integer, HashMap<Integer, Field>> fields = new HashMap<>();

        for (CheckerPiece piece : this.checkerPieces) {
            // Create new checker piece
            CheckerPiece p = new CheckerPiece(piece.isActive(), piece.getIsKing(), piece.getSize(), piece.getTeam());

            if (piece.getParent() != null) {
                // Get position of parent
                Point parentPos = piece.getParent().getPosition();

                // Make new parent (field) instance
                Field newParent = new Field(p, parentPos);

                // Insert new parent into fields arraylist
                if (!fields.containsKey(parentPos.x)) {
                    fields.put(parentPos.x, new HashMap<>());
                }
                fields.get(parentPos.x).put(parentPos.y, newParent);

                // Set the parent of p (the new checker piece) to the new parent
                p.setParent(newParent);
            }

            // Add p to copy
            copy.add(p);
        }

        if (this.fields != null) {
            // Loop over all the fields in the fields hashmap
            for (HashMap.Entry<Integer, HashMap<Integer, Field>> x : this.fields.entrySet()) {
                for (HashMap.Entry<Integer, Field> y : x.getValue().entrySet()) {
                    // Create a new field with the position of the original
                    Field newField = new Field(new Point(x.getKey(), y.getKey()));

                    // Insert the new field in the fields hashmap
                    if (!fields.containsKey(x.getKey())) {
                        fields.put(x.getKey(), new HashMap<>());
                    }
                    fields.get(x.getKey()).put(y.getKey(), newField);
                }
            }
        }

        // Set global variables
        this.checkerPieces = copy;
        this.fields = fields;

        // Remove the cylinder container in every checker piece as they are instances of StackPane
        // which is not serializable
        for (CheckerPiece piece : this.checkerPieces) {
            piece.setCylinderContainer(null);
        }

        // Create an object stream and write a file with the state of this
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // deserialize file and load data into state
    public ObjectDB loadState(String filename) {
        // Create an object stream from file and load that into an instance of ObjectDB
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            ObjectDB db = (ObjectDB) ois.readObject();

            ArrayList<CheckerPiece> checkerPieces = db.getCheckerPieces();

            // For each checker piece -> set cylinder continer to a new StackPane and setup piece
            for (CheckerPiece piece : checkerPieces) {
                piece.setCylinderContainer(new StackPane());
                piece.setupPiece();
            }

            // return the db instance
            return db;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}