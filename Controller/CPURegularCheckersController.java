package Controller;

import CPU.CPU;
import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import View.GameView;

import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;

public class CPURegularCheckersController extends RegularCheckersController {

    // CPU plays as Team.BLACK

    protected CPU cpu;

    // Make sure turn isn't finished automatically after moving CPU piece
    // controller.finishTurn is called manually in CPU class when finished
    protected boolean onPieceMove(CheckerPiece movedPiece, boolean didJump) {
        return super.onPieceMove(movedPiece, didJump, !this.isWhiteTurn);
    }

    public CPURegularCheckersController(GameView view, GridPane grid) {
        super(view, grid);

        this.cpu = new CPU(this);
    }

    public CPURegularCheckersController(
            GameView view,
            GridPane grid,
            ArrayList<CheckerPiece> checkerPieces,
            HashMap<Integer, HashMap<Integer, Field>> fields,
            boolean isWhiteTurn,
            HashMap<Team, Integer> activeCount
    ) {
        super(view, grid, checkerPieces, fields, isWhiteTurn, activeCount);

        this.cpu = new CPU(this);
        this.cpu.initStrategies();
    }

    // Setup CPU-specific countdown timer
    public void countDownTimer() {
        // Do not display CPU time left as it is not relevant
        GameView.displayBlackTimeLeft.setText("CPU");

        // Setup timer for white only
        super.countDownTimer(false);
    }

    // Hook into onTurnStart and take turn with CPU if black turn
    public boolean onTurnStart() {
        super.onTurnStart();

        if(this.isWhiteTurn) {
            return false;
        }

        this.cpu.takeTurn();

        return false;
    }

    // Do not allow player to move black pieces
    public void setSelectedPiece(CheckerPiece piece) {
        if(!this.isWhiteTurn) {
            return;
        }

        super.setSelectedPiece(piece);
    }

    // Set selected piece without restrictions
    public void setSelectedPieceCPU(CheckerPiece piece) {
        if(this.isWhiteTurn) {
            return;
        }

        this.selectedPiece = piece;
    }

    // Hook into setupPieces and init CPU strategies afterwards
    public void setupPieces() {
        super.setupPieces();

        this.cpu.initStrategies();
    }
}
