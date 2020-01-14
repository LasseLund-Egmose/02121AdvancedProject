package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Model.CheckerPiece;
import Model.Field;
import Model.Move;

import java.util.ArrayList;
import java.util.Random;

// TODO: Needs comments
public class OffensiveStrategy extends AbstractStrategy {

    protected ArrayList<Move> assessedMoves = new ArrayList<>();
    protected int highestGain;

    protected int jumpMoveGainAssessment(Move move) {
        int gain = 2;

        ArrayList<Move> jumpMoves = this.jumpsFromPosition(move.getPiece(), move.getToField());

        for(Move jumpMove : jumpMoves) {
            gain += this.jumpMoveGainAssessment(jumpMove);
        }

        return gain;
    }

    protected int gainAssessment(Move move) {
        return this.controller.getForcedJumpMoves().size() > 0 ?
            this.jumpMoveGainAssessment(move) : this.regularMoveGainAssessment(move);
    }

    protected int regularMoveGainAssessment(Move move) {
        int gain = 0;
        Field field = move.getToField();

        for(Field diagonalField : this.controller.getSurroundingFields(field)) {
            CheckerPiece surroundingPiece = diagonalField.getAttachedPieceSecure();

            if(surroundingPiece == null || surroundingPiece.getTeam() == move.getPiece().getTeam()) {
                continue;
            }

            // Piece exists and is on opponent's team
            Field opponentsFieldAfterPossibleJump = this.controller.getOppositeDiagonalField(field, diagonalField);
            if(opponentsFieldAfterPossibleJump != null && opponentsFieldAfterPossibleJump.getAttachedPieceSecure() == null) {
                // Can be jumped afterwards - this move is not advised
                return -1;
            }

            // The more jumps available next turn at new position (risk free because of above), the better
            gain += this.jumpsFromPosition(move.getPiece(), move.getToField()).size();
        }

        return gain;
    }

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

    public Move getMoveOrNull() {
        this.assessedMoves.clear();
        this.highestGain = Integer.MIN_VALUE;

        this.updateAllLegalMoves();

        if(this.allLegalMoves.size() == 0) {
            return null;
        }

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

        Move selectedMove = this.returnRandom(this.assessedMoves);
        if(selectedMove != null) {
            return selectedMove;
        }

        System.out.println("CPU: Cannot find suitable move! Returning random move.");

        return this.returnRandom(this.allLegalMoves);
    }
}
