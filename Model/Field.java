package Model;

import javafx.scene.layout.StackPane;

import java.awt.*;

public class Field extends StackPane {

    protected CheckerPiece attachedPiece = null;
    protected Point position = null;

    public CheckerPiece getAttachedPiece() {
        return this.attachedPiece;
    }

    public Point getPosition() {
        return position;
    }

    public Field(Point position) {
        this.position = position;
    }

    public void setAttachedPiece(CheckerPiece piece) {
        this.attachedPiece = piece;
    }

}
