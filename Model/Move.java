package Model;

import Enum.MoveType;

public class Move {

    protected Field jumpedField = null;
    protected CheckerPiece piece;
    protected Field toField;

    public Move(CheckerPiece piece, Field toField) {
        this.piece = piece;
        this.toField = toField;
    }

    public Move(CheckerPiece piece, Field toField, Field jumpedField) {
        this(piece, toField);
        this.jumpedField = jumpedField;
    }

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
}
