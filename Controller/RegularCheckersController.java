package Controller;

import Enum.Team;
import Model.CheckerPiece;
import View.View;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.awt.*;

public class RegularCheckersController extends AbstractController {

    protected boolean fieldShouldBeConsidered(CheckerPiece piece, Point position) {
        if(piece.getIsKing()) {
            return true;
        }

        return (piece.getTeam() != Team.WHITE || !(position.getY() < piece.getPosition().getY())) &&
            (piece.getTeam() != Team.BLACK || !(position.getY() > piece.getPosition().getY()));
    }

    public RegularCheckersController(View view, int dimension, GridPane grid) {
        super(view, dimension, grid);
    }

    public void setupPieceRow(int row, Team t) {
        for (int j = (row + 1) % 2; j < this.dimension; j += 2) {
            this.setupPiece(new Point(j + 1, row + 1), t);
        }
    }

    public void setupPieces() {
        for (int i = 0; i < 2; i++) {
            this.setupPieceRow(i, Team.WHITE);
        }

        for (int i = this.dimension - 1; i > this.dimension - 3; i--) {
            this.setupPieceRow(i, Team.BLACK);
        }
    }
}
