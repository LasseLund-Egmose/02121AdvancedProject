package Controller;

import Enum.Team;
import Model.CheckerPiece;
import View.View;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.awt.*;

public class SimpDamController extends AbstractController {

    protected void highlightEligibleFields(CheckerPiece piece) {
        // Iterate surrounding diagonal fields of given piece
        for (Point p : this.surroundingFields(piece.getPosition())) {
            // Get pane of current field
            StackPane pane = this.fields.get(p.x).get(p.y);

            // Is this position occupied - and is it possible to jump it?
            if (pane.getChildren().size() > 0) {
                Object eligibleJumpMove = this.eligibleJumpMoveOrNull(piece, p);

                // Check if jump move is eligible - per eligibleJumpMoveOrNull
                if (eligibleJumpMove instanceof StackPane) {
                    // Handle jump move if not null (e.g. instance of StackPane)
                    StackPane eligibleJumpMovePane = (StackPane) eligibleJumpMove;

                    this.possibleJumpMoves.put(eligibleJumpMovePane, p);
                    this.view.highlightPane(eligibleJumpMovePane);
                }
            } else { // Else allow a regular move
                this.possibleRegularMoves.add(pane);
                this.view.highlightPane(pane);
            }
        }
    }

    public SimpDamController(View view, int dimension, GridPane grid) {
        super(view, dimension, grid);
    }

    // Setup a piece in each corner
    public void setupPieces() {
        this.setupPiece(new Point(1, 1), Team.WHITE);
        this.setupPiece(new Point(this.dimension, this.dimension), Team.BLACK);
    }


}
