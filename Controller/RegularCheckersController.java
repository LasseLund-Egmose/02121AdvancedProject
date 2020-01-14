package Controller;

import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import View.GameView;

import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class RegularCheckersController extends AbstractController {

    protected boolean onPieceMove(CheckerPiece movedPiece, boolean didJump) {
        Team pieceTeam = movedPiece.getTeam();
        Point piecePosition = movedPiece.getPosition();

        if(pieceTeam == Team.BLACK && piecePosition.y == 1) {
            movedPiece.setKing();
        }

        if(pieceTeam == Team.WHITE && piecePosition.y == this.dimension) {
            movedPiece.setKing();
        }

        for(CheckerPiece piece : this.forcedJumpMoves) {
            piece.setCanHighlight(false);
        }

        return !didJump || !this.canJumpMore(movedPiece, true);
    }

    // Check if any jump moves can be made and if yes, force the player to select one
    public boolean onTurnStart() {
        boolean teamHasMoves = false;

        for(CheckerPiece piece : this.checkerPieces) {
            if(!piece.isActive()) {
                continue;
            }

            if(this.isWhiteTurn && piece.getTeam() == Team.BLACK || !this.isWhiteTurn && piece.getTeam() == Team.WHITE) {
                piece.setCanHighlight(true);
                continue;
            }

            boolean pieceHasJumps = false;

            for (Point p : this.surroundingFields(piece.getPosition())) {
                if(this.fieldShouldNotBeConsidered(piece, p)) {
                    continue;
                }

                teamHasMoves = true;

                // Get pane of current field
                Field field = this.fields.get(p.x).get(p.y);

                // Is this position occupied - and is it possible to jump it?
                if (field.getChildren().size() > 0) {
                    Object eligibleJumpMove = this.eligibleJumpMoveOrNull(piece, p);

                    // Check if jump move is eligible - per eligibleJumpMoveOrNull
                    if (eligibleJumpMove != null) {
                        // Force player to do this (or another) jump
                        this.forcedJumpMoves.add(piece);
                        piece.setCanHighlight(true);
                        pieceHasJumps = true;
                        break;
                    }
                }
            }

            piece.setCanHighlight(pieceHasJumps);
        }

        if(!teamHasMoves) {
            this.view.displayWin(this.isWhiteTurn ? Team.BLACK : Team.WHITE);
        }

        if(this.forcedJumpMoves.size() == 0) {
            for(CheckerPiece piece : this.checkerPieces) {
                piece.setCanHighlight(true);
            }
        }

        return super.onTurnStart();
    }

    public RegularCheckersController(GameView view, int dimension, GridPane grid) {
        super(view, dimension, grid);
    }

    public RegularCheckersController(
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

    public boolean canJumpMore(CheckerPiece piece, boolean shouldHighlight) {
        this.forcedJumpMoves.clear();

        ArrayList<Point> surrounding = this.surroundingFields(piece.getPosition());
        for(Point surroundingField : surrounding) {
            if(this.fieldShouldNotBeConsidered(piece, surroundingField)) {
                continue;
            }

            Field field = this.fields.get(surroundingField.x).get(surroundingField.y);

            if(field.getChildren().size() == 0) {
                continue;
            }

            Object eligibleJumpMove = this.eligibleJumpMoveOrNull(piece, surroundingField);

            // Check if jump move is eligible - per eligibleJumpMoveOrNull
            if (eligibleJumpMove instanceof Field) {
                Field eligibleJumpMoveField = (Field) eligibleJumpMove;

                if(eligibleJumpMoveField.getAttachedPiece() == null) {
                    this.pieceHighlightLocked = true;

                    piece.setCanHighlight(true);

                    this.forcedJumpMoves.add(piece);
                    this.possibleJumpMoves.put(eligibleJumpMoveField, field);

                    if(shouldHighlight) {
                        this.view.highlightPane(eligibleJumpMoveField);
                    }
                }
            }
        }

        return this.forcedJumpMoves.size() > 0;
    }

    public boolean fieldShouldNotBeConsidered(CheckerPiece piece, Point position) {
        return this.fieldShouldNotBeConsidered(piece, piece.getPosition(), position);
    }

    public boolean fieldShouldNotBeConsidered(CheckerPiece piece, Point alternativeFromPosition, Point position) {
        if(piece.getIsKing()) {
            return false;
        }

        return (piece.getTeam() == Team.WHITE && position.getY() < alternativeFromPosition.getY()) ||
            (piece.getTeam() == Team.BLACK && position.getY() > alternativeFromPosition.getY());
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
