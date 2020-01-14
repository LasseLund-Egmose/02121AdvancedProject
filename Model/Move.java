package Model;

import Enum.MoveType;

public class Move {

    protected Field jumpedField = null;
    protected CheckerPiece piece;
    protected Field toField;

    // Regular move constructor
    public Move(CheckerPiece piece, Field toField) {
        this.piece = piece;
        this.toField = toField;
    }

    // Jump move constructor
    public Move(CheckerPiece piece, Field toField, Field jumpedField) {
        this(piece, toField);
        this.jumpedField = jumpedField;
    }

    /*
     * Getters
     */

    public Field getJumpedField() {
        return this.jumpedField;
    }

    public MoveType getMoveType() {
        return this.jumpedField == null ? MoveType.REGULAR : MoveType.JUMP;
    }

    public CheckerPiece getPiece() {
        return this.piece;
    }

    public Field getToField() {
        return this.toField;
    }

    /*
     * End getters
     */
}
