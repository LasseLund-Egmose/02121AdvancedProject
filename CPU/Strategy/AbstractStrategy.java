package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import Model.Move;

import java.util.ArrayList;

abstract public class AbstractStrategy {

    abstract public Move getMoveOrNull(); // Main method called from CPU

    protected ArrayList<Move> allLegalMoves; // A list of all legal moves (once updated)
    protected CPURegularCheckersController controller; // Reference to game controller instance

    // Get all possible jumps for a given piece from a given field (position)
    protected ArrayList<Move> jumpsFromPosition(CheckerPiece piece, Field fromField) {
        ArrayList<Move> jumps = new ArrayList<>();

        for(Field jumpedField : this.controller.surroundingFields(fromField)) {
            // Check if piece on field to jump is from opponent's team
            CheckerPiece attachedPiece = jumpedField.getAttachedPieceSecure();
            if(attachedPiece == null || attachedPiece.getTeam() == piece.getTeam()) {
                continue;
            }

            // Check if jumpedField should be consider at all
            if(!this.controller.fieldShouldNotBeConsidered(attachedPiece, fromField.getPosition(), jumpedField.getPosition())) {
                continue;
            }

            // Check if field behind jumpedField is not occupied
            Field oppositeField = this.controller.oppositeDiagonalField(jumpedField, fromField);
            if(oppositeField == null || oppositeField.getAttachedPieceSecure() != null) {
                continue;
            }

            // Add to list of all possible jumps
            jumps.add(new Move(piece, oppositeField, jumpedField));
        }

        return jumps;
    }

    // Fetch all legal moves from game controller
    protected void updateAllLegalMoves() {
        this.allLegalMoves = new ArrayList<>();

        for(CheckerPiece piece : this.controller.getCheckerPieces()) {
            if(!piece.isActive() || piece.getTeam() == Team.WHITE) {
                continue;
            }

            ArrayList<Move> legalMovesForPiece = this.controller.legalMovesForPiece(piece);

            if(legalMovesForPiece.size() > 0) {
                this.allLegalMoves.addAll(legalMovesForPiece);
            }
        }
    }

    public AbstractStrategy(CPURegularCheckersController controller) {
        this.controller = controller;
    }

}
