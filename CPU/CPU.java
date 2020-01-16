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

public class CPU {

    /*
     * This class handles the core of the CPU player.
     * When the CPU controller instance calls takeTurn() this class figures out
     * a move by going through all attached "strategies" which then results in a
     * qualified move at some point
     */

    protected CPURegularCheckersController controller; // Reference to game controller
    protected CanJumpMoreStrategy jumpMoreStrategy; // Reference to jump more strategy (special strategy used when jumping multiple times in one turn)
    protected ArrayList<AbstractStrategy> strategies = new ArrayList<>(); // All strategy instances

    // Take rest of turn after pausing 1 second
    protected void takeRestOfTurn(Move nextMove) {
        boolean shouldFinishTurn = true;

        // Do move
        if(nextMove.getMoveType() == MoveType.JUMP) {
            this.controller.doJumpMove(nextMove.getToField(), nextMove.getJumpedField());

            // Check if multi-jump is eligible
            if(this.controller.canJumpMore(nextMove.getPiece(), false)) {
                // CanJumpMoreStrategy will catch next move and make sure only the currently active piece can move

                // Setup CanJumpMoreStrategy
                this.jumpMoreStrategy.setState(true, this.controller.getForcedJumpMoves());

                // Restart turn and do jump with currently selected piece
                shouldFinishTurn = false;
                this.takeTurn();
            }
        } else {
            this.controller.doRegularMove(nextMove.getToField(), false);
        }

        // Disable CanJumpMoreStrategy again
        this.jumpMoreStrategy.setState(false);

        // Remove highlights
        nextMove.getPiece().setHighlight(false);
        this.controller.getView().normalizePane(nextMove.getToField());

        // Finish turn if not multi-jumping
        if(shouldFinishTurn) {
            this.controller.finishTurn();
        }
    }

    public CPU(CPURegularCheckersController controller) {
        this.controller = controller;
    }

    // Setup all strategies
    public void initStrategies() {
        this.jumpMoreStrategy = new CanJumpMoreStrategy(this.controller);

        this.strategies.add(this.jumpMoreStrategy); // Handle multi-jumps from last turn first (and continue with other strategies if null)
        this.strategies.add(new DefensiveStrategy(this.controller)); // Make a defensive move first if possible
        this.strategies.add(new OffensiveStrategy(this.controller)); // Else make an offensive move
    }

    // Take first part of turn
    public void takeTurn() {
        // Iterate strategies until move is found
        for(AbstractStrategy strategy : this.strategies) {
            Move nextMove = strategy.getMoveOrNull();

            if(nextMove == null) {
                continue;
            }

            // Move is found

            CheckerPiece piece = nextMove.getPiece();

            this.controller.setSelectedPieceCPU(piece);

            // Setup relevant highlight
            piece.setHighlightCPU(true);
            this.controller.getView().highlightPaneCPU(nextMove.getToField());

            // Wait 1 second to notify opponent about move
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> this.takeRestOfTurn(nextMove));
            pause.play();

            break;
        }
    }
}
