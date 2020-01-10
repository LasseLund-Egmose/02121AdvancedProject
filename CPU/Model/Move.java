package CPU.Model;

import Model.CheckerPiece;

import java.awt.*;

public class Move {

    protected CheckerPiece piece;
    protected Point toPosition;

    public CheckerPiece getPiece() {
        return piece;
    }

    public Point getToPosition() {
        return toPosition;
    }
}
