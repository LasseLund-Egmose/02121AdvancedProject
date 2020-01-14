package CPU;

import CPU.Strategy.AbstractStrategy;
import CPU.Strategy.CanJumpMoreStrategy;
import CPU.Strategy.DefensiveStrategy;
import CPU.Strategy.OffensiveStrategy;
import Controller.CPURegularCheckersController;
import Enum.MoveType;
import Model.CheckerPiece;
import Model.Move;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.ArrayList;

// TODO: Needs comments
public class CPU {

    protected CPURegularCheckersController controller;
    protected CanJumpMoreStrategy jumpMoreStrategy;
    protected ArrayList<AbstractStrategy> strategies = new ArrayList<>();

    public void takeRestOfTurn(Move nextMove) {

        if(nextMove.getMoveType() == MoveType.JUMP) {
            this.controller.doJumpMove(nextMove.getToField(), nextMove.getJumpedField());

            if(this.controller.canJumpMore(nextMove.getPiece(), false)) {
                this.jumpMoreStrategy.setState(true, this.controller.getForcedJumpMoves());

                // Restart turn and do jump with currently selected piece
                this.takeTurn();
            }
        } else {
            this.controller.doRegularMove(nextMove.getToField(), false);
        }

        this.jumpMoreStrategy.setState(false);

        nextMove.getPiece().setHighlight(false);
        this.controller.getView().normalizePane(nextMove.getToField());
    }

    public CPU(CPURegularCheckersController controller) {
        this.controller = controller;
    }

    public void initStrategies() {
        this.jumpMoreStrategy = new CanJumpMoreStrategy(this.controller);

        this.strategies.add(this.jumpMoreStrategy); // Handle multi-jumps from last turn first (and continue with other strategies if null)
        this.strategies.add(new DefensiveStrategy(this.controller)); // Make a defensive move first if possible
        this.strategies.add(new OffensiveStrategy(this.controller)); // Else make an offensive move
    }

    public void takeTurn() {
        for(AbstractStrategy strategy : this.strategies) {
            Move nextMove = strategy.getMoveOrNull();

            if(nextMove == null) {
                continue;
            }

            System.out.println("Caught by: " + strategy.getClass().getSimpleName());

            CheckerPiece piece = nextMove.getPiece();

            this.controller.setSelectedPieceCPU(piece);

            piece.setHighlightCPU(true);
            this.controller.getView().highlightPaneCPU(nextMove.getToField());

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> this.takeRestOfTurn(nextMove));
            pause.play();

            break;
        }
    }
}
