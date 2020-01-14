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

    protected ArrayList<Point> surroundingFields(Point p) {
        Field field = this.fields.get(p.x).get(p.y);
        CheckerPiece fieldAttachedPiece = field.getAttachedPiece();

        if(fieldAttachedPiece == null || !fieldAttachedPiece.getIsKing()) {
            return super.surroundingFields(p);
        }

        ArrayList<Point> eligiblePoints = new ArrayList<>();
        Point[] points = new Point[]{
                new Point(p.x - 1, p.y + 1),
                new Point(p.x + 1, p.y + 1),
                new Point(p.x - 1, p.y - 1),
                new Point(p.x + 1, p.y - 1)
        };


        for (int i = 0; i < 4; i++) {
            Point ip = points[i];
            int differencX = ip.x-p.x;
            int differencY = ip.y-p.y;

            if (this.isPositionValid(ip)) {
                eligiblePoints.add(ip);
            }

            for (int j = 2; j < this.dimension; j++) {
                Point ip1 = new Point(p.x + (j * differencX), p.y + (j * differencY));
                if (this.isPositionValid(ip1) && this.fields.get(ip1.x).get(ip1.y).getAttachedPiece() == null) {
                    eligiblePoints.add(ip1);
                } else {
                    break;
                }

            }
        }
        return eligiblePoints;
    }

    public FlexibleKingController(GameView view, int dimension, GridPane grid) {
        super(view, dimension, grid);
    }

}
