package Model;

import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.Serializable;

public class Field extends StackPane implements Serializable {

    private static final long serialVersionUID = -696440499944099891L;

    protected CheckerPiece attachedPiece = null; // The CheckerPiece attached to this field (or null if none)
    protected Point position; // The grid position of this field (does not change throughout game)

    // Short constructor
    public Field(Point position) {
        this.position = position;
    }

    // Long constructor
    public Field(CheckerPiece attachedPiece, Point position) {
        this.attachedPiece = attachedPiece;
        this.position = position;
    }

    /*
     * Getters and setters
     */

    // Return attached piece if it's active
    public CheckerPiece getAttachedPieceSecure() {
        if(this.attachedPiece == null) {
            return null;
        }

        return this.attachedPiece.getIsActive() ? this.attachedPiece : null;
    }

    public Point getPosition() {
        return position;
    }

    public void setAttachedPiece(CheckerPiece piece) {
        this.attachedPiece = piece;
    }

}
