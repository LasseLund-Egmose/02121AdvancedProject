package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Enum.Team;
import Model.CheckerPiece;
import Model.Move;

import java.util.ArrayList;
import java.util.HashMap;

abstract public class AbstractStrategy {

    abstract public Move getMoveOrNull();

    protected HashMap<CheckerPiece, ArrayList<Move>> allLegalMoves;
    protected CPURegularCheckersController controller;

    public AbstractStrategy(CPURegularCheckersController controller) {
        this.controller = controller;

        this.updateAllLegalMoves();
    }

    protected void updateAllLegalMoves() {
        this.allLegalMoves = new HashMap<>();

        for(CheckerPiece piece : this.controller.getCheckerPieces()) {
            if(piece.getTeam() == Team.WHITE) {
                continue;
            }

            this.allLegalMoves.put(piece, this.controller.getLegalMovesForPiece(piece));
        }
    }

}
