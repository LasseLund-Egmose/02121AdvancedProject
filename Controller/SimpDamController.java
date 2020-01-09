package Controller;

import Enum.Team;
import View.View;

import javafx.scene.layout.GridPane;

import java.awt.*;

public class SimpDamController extends AbstractController {

    public SimpDamController(View view, int dimension, GridPane grid) {
        super(view, dimension, grid);
    }

    // Setup black fields - override to invert board so a black field is located bottom left and top right
    public void setupFields() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = i % 2; j < this.dimension; j += 2) {
                this.setupField(new Point(j + 1, i + 1));
            }
        }
    }

    // Setup a piece in each corner
    public void setupPieces() {
        this.setupPiece(new Point(1, 1), Team.WHITE);
        this.setupPiece(new Point(this.dimension, this.dimension), Team.BLACK);
    }


}
