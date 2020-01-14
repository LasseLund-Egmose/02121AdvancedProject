package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Enum.MoveType;
import Model.CheckerPiece;
import Model.Move;

import java.util.ArrayList;

public class CanJumpMoreStrategy extends AbstractStrategy {

    protected ArrayList<CheckerPiece> forcedJumpMoves;

    protected boolean isActive = false;

    public CanJumpMoreStrategy(CPURegularCheckersController controller) {
        super(controller);
    }

    public Move getMoveOrNull() {
        if(!this.isActive || this.forcedJumpMoves.size() == 0) {
            return null;
        }

        // Do first forced jump move
        CheckerPiece piece = this.forcedJumpMoves.get(0);
        ArrayList<Move> allMoves = this.controller.getLegalMovesForPiece(piece);

        for(Move m : allMoves) {
            if(m.getMoveType() != MoveType.JUMP) {
                continue;
            }

            return m;
        }

        // If not, return null
        return null;
    }

    public void setState(boolean isActive) {
        this.isActive = isActive;
    }

    public void setState(boolean isActive, ArrayList<CheckerPiece> forcedJumpMoves) {
        this.setState(isActive);

        this.forcedJumpMoves = forcedJumpMoves;
    }
}