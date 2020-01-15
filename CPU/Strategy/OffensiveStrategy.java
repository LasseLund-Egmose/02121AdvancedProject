package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Model.CheckerPiece;
import Model.Field;
import Model.Move;

import java.util.ArrayList;
import java.util.Random;

public class OffensiveStrategy extends AbstractStrategy {

    protected ArrayList<Move> assessedMoves = new ArrayList<>(); // A list of gain assessed moves
    protected int highestGain; // Highest gain

    // Gain assess (how good is a move) a jump move
    // One jump equals 2 gain
    protected int jumpMoveGainAssessment(Move move) {
        int gain = 2;

        ArrayList<Move> jumpMoves = this.jumpsFromPosition(move.getPiece(), move.getToField());

        for(Move jumpMove : jumpMoves) {
            gain += this.jumpMoveGainAssessment(jumpMove);
        }

        return gain;
    }

    // Gain assess a move
    protected int gainAssessment(Move move) {
        return this.controller.getForcedJumpMoves().size() > 0 ?
            this.jumpMoveGainAssessment(move) : this.regularMoveGainAssessment(move);
    }

    // Gain assess a regular move
    // One possible jump over opponent in next round equals one gain
    // But if opponent is able to jump piece in move in next round, gain is -1
    protected int regularMoveGainAssessment(Move move) {
        int gain = 0;
        Field field = move.getToField();

        for(Field diagonalField : this.controller.surroundingFields(field)) {
            CheckerPiece surroundingPiece = diagonalField.getAttachedPieceSecure();

            if(surroundingPiece == null || surroundingPiece.getTeam() == move.getPiece().getTeam()) {
                continue;
            }

            // Piece exists and is on opponent's team
            Field jToField = this.controller.oppositeDiagonalField(field, diagonalField);
            if(jToField != null && (jToField.getAttachedPieceSecure() == null || jToField.getAttachedPieceSecure() == move.getPiece())) {
                // Can be jumped afterwards - this move is not advised
                return -1;
            }

            // The more jumps available next turn at new position (risk free because of above), the better
            gain += this.jumpsFromPosition(move.getPiece(), move.getToField()).size();
        }

        return gain;
    }

    // Return a random move from a move list
    protected Move returnRandom(ArrayList<Move> moves) {
        if(moves.size() == 0) {
            return null;
        }

        Random generator = new Random();
        return moves.get(generator.nextInt(moves.size()));
    }

    public OffensiveStrategy(CPURegularCheckersController controller) {
        super(controller);
    }

    // Get a move
    // Highest gaining move is selected if multiple moves are available
    // If no moves are available, a random move is selected (as there are no more strategies after this and the CPU has to make a move)
    public Move getMoveOrNull() {
        // Clear data from last round and refresh legal moves
        this.assessedMoves.clear();
        this.highestGain = Integer.MIN_VALUE;
        this.updateAllLegalMoves();

        if(this.allLegalMoves.size() == 0) {
            return null;
        }

        // Create a list of moves with highest gain
        for(Move possibleMove : this.allLegalMoves) {
            int gain = this.gainAssessment(possibleMove);

            if(gain > this.highestGain) {
                this.assessedMoves.clear();
                this.highestGain = gain;
            }

            if(gain == this.highestGain) {
                this.assessedMoves.add(possibleMove);
            }
        }

        // Select a move from list of highest
        Move selectedMove = this.returnRandom(this.assessedMoves);
        if(selectedMove != null) {
            return selectedMove;
        }

        System.out.println("CPU: Cannot find suitable move! Returning random move.");

        // Select a random move as no qualified moves are available
        return this.returnRandom(this.allLegalMoves);
    }
}
