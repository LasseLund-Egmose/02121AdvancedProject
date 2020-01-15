package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Enum.MoveType;
import Model.CheckerPiece;
import Model.Move;

import java.util.ArrayList;

public class CanJumpMoreStrategy extends AbstractStrategy {

    protected ArrayList<CheckerPiece> forcedJumpMoves; // A list of all pieces that has to jump (taken from forcedJumpMoves in controller)
    protected boolean isActive = false; // Should we check for possible jump moves? Set from CPU class instance

    public CanJumpMoreStrategy(CPURegularCheckersController controller) {
        super(controller);
    }

    public Move getMoveOrNull() {
        if(!this.isActive || this.forcedJumpMoves.size() == 0) {
            return null;
        }

        // Do first forced jump move
        CheckerPiece piece = this.forcedJumpMoves.get(0);
        ArrayList<Move> allMoves = this.controller.legalMovesForPiece(piece);

        for(Move m : allMoves) {
            if(m.getMoveType() != MoveType.JUMP) {
                continue;
            }

            return m;
        }

        // If not, return null
        return null;
    }

    /*
     * State setters
     */

    public void setState(boolean isActive) {
        this.isActive = isActive;
    }

    public void setState(boolean isActive, ArrayList<CheckerPiece> forcedJumpMoves) {
        this.setState(isActive);

        this.forcedJumpMoves = forcedJumpMoves;
    }
}
