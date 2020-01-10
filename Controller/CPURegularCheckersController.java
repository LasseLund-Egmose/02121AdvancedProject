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



        return false;
    }

    public void setSelectedPiece(CheckerPiece piece) {
        // Do not allow moving black pieces
        if(!this.isWhiteTurn) {
            return;
        }

        super.setSelectedPiece(piece);
    }
}
