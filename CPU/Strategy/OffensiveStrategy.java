package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Model.CheckerPiece;
import Model.Field;
import Model.Move;

import java.util.ArrayList;
import java.util.Random;

public class OffensiveStrategy extends AbstractStrategy {

    protected ArrayList<Move> assessedMoves = new ArrayList<>();
    protected int highestGain = Integer.MIN_VALUE;

    protected int jumpMoveGainAssessment(Move move) {
        int gain = 1;

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
        System.out.println("Move - From: " + move.getPiece().getPosition() + ", To: " + move.getToField().getPosition());

        for(Field diagonalField : this.controller.getSurroundingFields(field)) {
            System.out.println("Surrounding field: " + diagonalField.getPosition());

            CheckerPiece surroundingPiece = diagonalField.getAttachedPiece();

            if(surroundingPiece == null || surroundingPiece.getTeam() == move.getPiece().getTeam()) {
                continue;
            }

            Field reverseDiagonalField = this.controller.getOppositeDiagonalField(field, diagonalField);

            if(reverseDiagonalField != null && reverseDiagonalField.getAttachedPiece() == null) {
                System.out.println("Reverse diagonal field: " + reverseDiagonalField.getPosition());
                return -1;
            }

            Field fieldBehindOpponentNextTurn = this.controller.getOppositeDiagonalField(diagonalField, field);
            if(fieldBehindOpponentNextTurn != null && fieldBehindOpponentNextTurn.getAttachedPiece() == null) {
                System.out.println("Field behind: " + fieldBehindOpponentNextTurn.getPosition());
                gain = 1;
            }
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
        this.updateAllLegalMoves();

        ArrayList<Move> possibleMoves = new ArrayList<>(this.allLegalMoves);

        if(this.allLegalMoves.size() == 0) {
            return null;
        }

        for(Move possibleMove : possibleMoves) {
            int gain = this.gainAssessment(possibleMove);

            if(gain > this.highestGain) {
                this.assessedMoves.clear();
                this.highestGain = gain;
            }

            this.assessedMoves.add(possibleMove);
        }

        System.out.println("Random - Gain: " + this.highestGain);

        return this.returnRandom(this.assessedMoves);
    }
}
