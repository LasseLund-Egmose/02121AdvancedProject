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

    protected ArrayList<Point> surroundingPoints(Point p) {
        Field field = this.fields.get(p.x).get(p.y);
        CheckerPiece fieldAttachedPiece = field.getAttachedPieceSecure();

        if(fieldAttachedPiece == null || !fieldAttachedPiece.getIsKing()) {
            return super.surroundingPoints(p);
        }

        ArrayList<Point> eligiblePoints = new ArrayList<>();
        Point[] points = this.surroundingFieldsPosition(p);

        for (int i = 0; i < 4; i++) {
            Point ip = points[i];
            int differencX = ip.x - p.x;
            int differencY = ip.y - p.y;

            if (this.isPositionValid(ip)) {
                eligiblePoints.add(ip);
            }

            for (int j = 2; j < this.dimension; j++) {
                Point jp = new Point(p.x + (j * differencX), p.y + (j * differencY));
                if (this.isPositionValid(jp) && this.fields.get(jp.x).get(jp.y).getAttachedPieceSecure() == null) {
                    eligiblePoints.add(jp);
                } else {
                    break;
                }

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
