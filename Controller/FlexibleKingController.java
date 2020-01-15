package Controller;

import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import View.GameView;

import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FlexibleKingController extends RegularCheckersController {

    // Extend
    protected ArrayList<Point> surroundingPoints(Point point) {
        // Get regular surrounding points
        ArrayList<Point> eligiblePoints = super.surroundingPoints(point);
        ArrayList<Point> allEligiblePoints = new ArrayList<>(eligiblePoints);

        CheckerPiece piece = this.fields.get(point.x).get(point.y).getAttachedPieceSecure();

        // Cannot move anywhere out of the ordinary if no eligible positions at this point or piece is not king
        if(eligiblePoints.size() == 0 || piece == null || !piece.getIsKing()) {
            return eligiblePoints;
        }

        for (Point p : eligiblePoints) {
            // Get difference from given position
            Point diagonalDifference = new Point(p.x - point.x, p.y - point.y);

            // Gradually extend difference (and add to eligiblePoints) until an invalid or occupied field is found
            for (int j = 2; j < this.dimension; j++) {
                Point extendedDiagonal = new Point(point.x + j * diagonalDifference.x, point.y + j * diagonalDifference.y);

                // Is the field valid?
                if (!this.isPositionValid(extendedDiagonal)) {
                    break;
                }

                CheckerPiece fieldPiece = this.fields.get(extendedDiagonal.x).get(extendedDiagonal.y).getAttachedPieceSecure();

                // Is the field occupied?
                if(fieldPiece != null) {
                    break;
                }

                allEligiblePoints.add(extendedDiagonal);
            }
        }
        return allEligiblePoints;
    }

    /*
     * Constructors
     */

    public FlexibleKingController(GameView view, GridPane grid) {
        super(view, grid);
    }

    public FlexibleKingController(
            GameView view,
            GridPane grid,
            ArrayList<CheckerPiece> checkerPieces,
            HashMap<Integer, HashMap<Integer, Field>> fields,
            boolean isWhiteTurn,
            HashMap<Team, Integer> activeCount
    ) {
        super(view, grid, checkerPieces, fields, isWhiteTurn, activeCount);
    }

}
