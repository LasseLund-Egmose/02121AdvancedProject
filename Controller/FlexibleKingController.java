package Controller;

import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import View.GameView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;

public class FlexibleKingController extends RegularCheckersController {

    public FlexibleKingController(GameView view, int dimension, GridPane grid) {
        super(view, dimension, grid);
    }

    public FlexibleKingController(
            GameView view,
            int dimension,
            GridPane grid,
            ArrayList<CheckerPiece> checkerPieces,
            HashMap<Integer, HashMap<Integer, Field>> fields,
            boolean isWhiteTurn,
            HashMap<Team, Integer> activeCount
    ) {
        super(view, dimension, grid, checkerPieces, fields, isWhiteTurn, activeCount);
    }



}
