package CPU;

import CPU.Strategy.AbstractStrategy;
import CPU.Strategy.CanJumpMoreStrategy;
import CPU.Strategy.DefensiveStrategy;
import CPU.Strategy.RandomStrategy;
import Controller.CPURegularCheckersController;
import Enum.MoveType;
import Model.CheckerPiece;
import Model.Move;

import java.util.ArrayList;

public class CPU {

    final protected static int SLEEP_TIME = 1000;

    protected CPURegularCheckersController controller;
    protected CanJumpMoreStrategy jumpMoreStrategy;
    protected ArrayList<AbstractStrategy> strategies = new ArrayList<>();

    public CPU(CPURegularCheckersController controller) {
        this.controller = controller;
    }

    public void initStrategies() {
        this.jumpMoreStrategy = new CanJumpMoreStrategy(this.controller);

        this.strategies.add(this.jumpMoreStrategy); // Handle multi-jumps from last turn first (and continue with other strategies if null)
        this.strategies.add(new DefensiveStrategy(this.controller)); // Make a defensive move first if possible
        this.strategies.add(new RandomStrategy(this.controller)); // Else make an offensive move
    }

    public void takeTurn() {
        for(AbstractStrategy strategy : this.strategies) {
            Move nextMove = strategy.getMoveOrNull();

            if(nextMove == null) {
                continue;
            }

            System.out.println("Caught by: " + strategy.getClass().getSimpleName());

            CheckerPiece movedPiece = nextMove.getPiece();
            this.controller.setSelectedPieceCPU(movedPiece);
            this.controller.getView().highlightPane(nextMove.getToField());

            // TODO: Do some kind of sleep here

            if(nextMove.getMoveType() == MoveType.JUMP) {
                this.controller.doJumpMove(nextMove.getToField(), nextMove.getJumpedField());

                if(this.controller.canJumpMore(movedPiece, false)) {
                    this.jumpMoreStrategy.setState(true, this.controller.getForcedJumpMoves());

                    // Restart turn and do jump with currently selected piece
                    this.takeTurn();
                }
            } else {
                this.controller.doRegularMove(nextMove.getToField(), false);
            }

            this.jumpMoreStrategy.setState(false);
            this.controller.getView().normalizePane(nextMove.getToField());

            break;
        }
    }

}
