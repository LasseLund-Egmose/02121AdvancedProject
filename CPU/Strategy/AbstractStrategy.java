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

    protected void updateAllLegalMoves() {
        this.allLegalMoves = new HashMap<>();

        for(CheckerPiece piece : this.controller.getCheckerPieces()) {
            if(piece.getTeam() == Team.WHITE) {
                continue;
            }

            ArrayList<Move> legalMovesForPiece = this.controller.getLegalMovesForPiece(piece);

            if(legalMovesForPiece.size() > 0) {
                this.allLegalMoves.put(piece, legalMovesForPiece);
            }
        }
    }

    public AbstractStrategy(CPURegularCheckersController controller) {
        this.controller = controller;
    }

}
