package Controller;

import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import View.GameView;

import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: Needs cleanup and comments
public class FlexibleKingController extends RegularCheckersController {

    // Extend
    protected ArrayList<Point> surroundingPoints(Point point) {
        // Get regular surrounding points
        ArrayList<Point> eligiblePoints = super.surroundingPoints(point);

        CheckerPiece piece = this.fields.get(point.x).get(point.y).getAttachedPieceSecure();

        // Cannot move anywhere out of the ordinary if no eligible positions at this point or piece is not king
        if(eligiblePoints.size() == 0 || piece == null || !piece.getIsKing()) {
            return eligiblePoints;
        }

        // Get diagonal position from current position
        Point[] points = this.surroundingFieldsPosition(point);

        for (Point p : points) {
            // Get difference from given position
            Point diagonalDifference = new Point(p.x - point.x, p.y - point.y);

            // Gradually extend difference (and add to eligiblePoints) until an invalid or occupied field is found
            for (int j = 2; j < this.dimension; j++) {
                Point extendedDiagonal = new Point(p.x + j * diagonalDifference.x, p.y + j * diagonalDifference.y);

                // Is the field valid?
                if (!this.isPositionValid(extendedDiagonal)) {
                    break;
                }

                CheckerPiece fieldPiece = this.fields.get(extendedDiagonal.x).get(extendedDiagonal.y).getAttachedPieceSecure();

                // Is the field occupied?
                if(fieldPiece != null) {
                    break;
                }

                eligiblePoints.add(extendedDiagonal);
            }
        }
        return eligiblePoints;
    }

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
