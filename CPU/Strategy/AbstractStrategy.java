package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import Model.Move;

import java.util.ArrayList;
import java.util.HashMap;

abstract public class AbstractStrategy {

    abstract public Move getMoveOrNull();

    protected ArrayList<Move> allLegalMoves;
    protected CPURegularCheckersController controller;

    protected ArrayList<Move> jumpsFromPosition(CheckerPiece piece, Field fromField) {
        ArrayList<Move> jumps = new ArrayList<>();

        for(Field jumpedField : this.controller.getSurroundingFields(fromField)) {
            CheckerPiece attachedPiece = jumpedField.getAttachedPiece();
            if(attachedPiece == null || attachedPiece.getTeam() == piece.getTeam()) {
                continue;
            }

            if(!this.controller.fieldShouldNotBeConsidered(attachedPiece, fromField.getPosition(), jumpedField.getPosition())) {
                continue;
            }

            Field oppositeField = this.controller.getOppositeDiagonalField(jumpedField, fromField);
            if(oppositeField == null || oppositeField.getAttachedPiece() != null) {
                continue;
            }

            jumps.add(new Move(piece, oppositeField, jumpedField));
        }

        return jumps;
    }

    protected void updateAllLegalMoves() {
        this.allLegalMoves = new ArrayList<>();

        for(CheckerPiece piece : this.controller.getCheckerPieces()) {
            if(piece.getTeam() == Team.WHITE) {
                continue;
            }

            ArrayList<Move> legalMovesForPiece = this.controller.getLegalMovesForPiece(piece);

            if(legalMovesForPiece.size() > 0) {
                this.allLegalMoves.addAll(legalMovesForPiece);
            }
        }
    }

    public AbstractStrategy(CPURegularCheckersController controller) {
        this.controller = controller;
    }

}
