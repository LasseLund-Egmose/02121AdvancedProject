package Controller;

import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import View.GameView;

import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SimpDamController extends AbstractController {


    // Short constructor
    public SimpDamController(GameView view, GridPane grid) {
        super(view, grid);
    }

    // Long constructor
    public SimpDamController(
            GameView view,
            GridPane grid,
            ArrayList<CheckerPiece> checkerPieces,
            HashMap<Integer, HashMap<Integer, Field>> fields,
            boolean isWhiteTurn,
            HashMap<Team, Integer> activeCount
    ) {
        super(view, grid, checkerPieces, fields, isWhiteTurn, activeCount);
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
