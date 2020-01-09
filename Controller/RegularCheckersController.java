package Controller;

import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import View.View;

import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;

public class RegularCheckersController extends AbstractController {

    protected boolean canJumpMore(CheckerPiece piece) {
        ArrayList<Point> surrounding = this.surroundingFields(piece.getPosition());

        for(Point surroundingField : surrounding) {
            Field field = this.fields.get(surroundingField.x).get(surroundingField.y);

            if(field.getChildren().size() == 0) {
                continue;
            }

            Object eligibleJumpMove = this.eligibleJumpMoveOrNull(piece, surroundingField);

            // Check if jump move is eligible - per eligibleJumpMoveOrNull
            if (eligibleJumpMove instanceof Field) {
                Field eligibleJumpMoveField = (Field) eligibleJumpMove;

                if(eligibleJumpMoveField.getAttachedPiece() == null) {
                    this.possibleJumpMoves.put(eligibleJumpMoveField, field);
                    this.view.highlightPane(eligibleJumpMoveField);

                    return true;
                }
            }
        }

        return false;
    }

    protected boolean fieldShouldBeConsidered(CheckerPiece piece, Point position) {
        if(piece.getIsKing()) {
            return true;
        }

        return (piece.getTeam() != Team.WHITE || !(position.getY() < piece.getPosition().getY())) &&
            (piece.getTeam() != Team.BLACK || !(position.getY() > piece.getPosition().getY()));
    }

    protected boolean onPieceMove(CheckerPiece movedPiece, boolean didJump) {
        Team pieceTeam = movedPiece.getTeam();
        Point piecePosition = movedPiece.getPosition();

        if(pieceTeam == Team.BLACK && piecePosition.y == 1) {
            movedPiece.setKing();
        }

        if(pieceTeam == Team.WHITE && piecePosition.y == this.dimension) {
            movedPiece.setKing();
        }

        return !didJump || !this.canJumpMore(movedPiece);
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
        for (int i = 0; i < 3; i++) {
            this.setupPieceRow(i, Team.WHITE);
        }

        for (int i = this.dimension - 1; i > this.dimension - 4; i--) {
            this.setupPieceRow(i, Team.BLACK);
        }
    }
}
