package Controller;

import CPU.CPU;
import Model.CheckerPiece;
import View.GameView;
import javafx.scene.layout.GridPane;

public class CPURegularCheckersController extends RegularCheckersController {

    // CPU plays as Team.BLACK

    protected CPU cpu;

    public CPURegularCheckersController(GameView view, int dimension, GridPane grid) {
        super(view, dimension, grid);

        this.cpu = new CPU(this);
    }

    protected boolean onTurnStart() {
        super.onTurnStart();

        if(this.isWhiteTurn) {
            return false;
        }

        this.cpu.takeTurn();

        return false;
    }

    public void setSelectedPiece(CheckerPiece piece) {
        // Do not allow moving black pieces
        if(!this.isWhiteTurn) {
            return;
        }

        super.setSelectedPiece(piece);
    }

    public void setSelectedPieceCPU(CheckerPiece piece) {
        if(this.isWhiteTurn) {
            return;
        }

        this.selectedPiece = piece;
    }

    public void setupPieces() {
        super.setupPieces();

        this.cpu.initStrategies();
    }
}
