package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Model.CheckerPiece;
import Model.Move;

import java.util.ArrayList;
import java.util.Random;

public class RandomStrategy extends AbstractStrategy {

    public RandomStrategy(CPURegularCheckersController controller) {
        super(controller);
    }

    // TODO: Implement non-random strategy
    public Move getMoveOrNull() {
        this.updateAllLegalMoves();

        Random generator = new Random();

        ArrayList<CheckerPiece> piecesWithMoves = new ArrayList<>(this.allLegalMoves.keySet());
        CheckerPiece selectedPiece = piecesWithMoves.get(generator.nextInt(piecesWithMoves.size()));

        ArrayList<Move> legalMoves = this.allLegalMoves.get(selectedPiece);
        return legalMoves.get(generator.nextInt(legalMoves.size()));
    }
}
