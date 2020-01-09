package Controller;

import Enum.Team;
import Model.CheckerPiece;
import View.View;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

abstract public class AbstractController {

    protected ArrayList<CheckerPiece> checkerPieces = new ArrayList<>(); // A list of all pieces
    protected HashMap<Integer, HashMap<Integer, StackPane>> fields = new HashMap<>(); // A map (x -> y -> pane) of all dark fields (StackPanes)

    protected HashMap<Team, Integer> activeCount = new HashMap<>(); // A map (Team -> int) of number of active pieces on each team

    protected HashMap<StackPane, Point> possibleJumpMoves = new HashMap<>(); // A map (pane -> jumped position) of all possible jump moves
    protected ArrayList<StackPane> possibleRegularMoves = new ArrayList<>(); // A list of all possible regular moves

    protected int dimension; // Dimension of board
    protected GridPane grid;
    protected boolean isWhiteTurn = true; // Keep track of turn
    protected EventHandler<MouseEvent> moveClickEventHandler; // EventHandler for click events on black fields
    protected CheckerPiece selectedPiece = null; // Keep track of selected piece
    protected View view; // Reference to view instance

    // Highlight fields a selected piece can move to
    abstract protected void highlightEligibleFields(CheckerPiece piece);

    // Setup a piece in each corner
    abstract public void setupPieces();

    // Check if a team has won
    protected void checkForWin() {
        if (this.activeCount.get(Team.BLACK) == 0) {
            this.view.displayWin("White won");
        }

        if (this.activeCount.get(Team.WHITE) == 0) {
            this.view.displayWin("Black won");
        }
    }

    // Handle a jump move
    protected void doJumpMove(StackPane toPane, Point jumpedPosition) {
        // Detach (remove) jumped Model.CheckerPiece
        for (CheckerPiece piece : this.checkerPieces) {
            if (!piece.getPosition().equals(jumpedPosition)) {
                continue;
            }

            piece.detach(this.activeCount);

            break;
        }

        // Handle rest of move as a regular move
        this.doRegularMove(toPane);
    }

    // Handle a regular move
    protected void doRegularMove(StackPane toPane) {
        // Attach selected piece to chosen field
        this.getSelectedPiece().attachToFieldByPane(this.fields, toPane, this.activeCount);

        // Remove highlight of piece and fields
        this.selectedPiece.assertHighlight(false);
        this.normalizeFields();

        // Reset highlight-related properties
        this.selectedPiece = null;
        this.possibleJumpMoves.clear();
        this.possibleRegularMoves.clear();

        // Finish turn
        this.finishTurn();
    }

    // Check if a jump move is eligible (e.g. no piece behind jumped piece)
    // Return pane from new position if yes and null if no
    protected Object eligibleJumpMoveOrNull(CheckerPiece thisPiece, Point opponentPosition) {
        Point thisPos = thisPiece.getPosition();
        Point diff = new Point(opponentPosition.x - thisPos.x, opponentPosition.y - thisPos.y);

        Point newPos = (Point) opponentPosition.clone();
        newPos.translate(diff.x, diff.y);

        return this.isPositionValid(newPos) ? fields.get(newPos.x).get(newPos.y) : null;
    }

    // Check if game is over, toggle isWhiteTurn and setup turn for other team
    protected void finishTurn() {
        this.isWhiteTurn = !this.isWhiteTurn;

        checkForWin();

        this.view.setupDisplayTurn(this.isWhiteTurn);
        this.view.rotate();
    }

    // Check if position is within boundaries of board
    protected boolean isPositionValid(Point p) {
        return p.x >= 1 && p.y >= 1 && p.x <= this.dimension && p.y <= this.dimension;
    }

    // Remove highlights from highlighted fields
    protected void normalizeFields() {
        ArrayList<StackPane> allHighlightedPanes = new ArrayList<>();
        allHighlightedPanes.addAll(this.possibleJumpMoves.keySet());
        allHighlightedPanes.addAll(this.possibleRegularMoves);

        for (StackPane p : allHighlightedPanes) {
            this.view.normalizePane(p);
        }
    }

    // Handle click on black field
    protected void onFieldClick(Object clickedElement) {
        // Check if StackPane is clicked and a selectedPiece is chosen
        if (!(clickedElement instanceof StackPane) || this.getSelectedPiece() == null) {
            return;
        }

        StackPane clickedElementPane = (StackPane) clickedElement;

        // Is a jump move chosen?
        if (this.possibleJumpMoves.containsKey(clickedElement)) {
            this.doJumpMove(clickedElementPane, this.possibleJumpMoves.get(clickedElement));
            return;
        }

        // Is a regular move chosen?
        if (this.possibleRegularMoves.contains(clickedElement)) {
            this.doRegularMove(clickedElementPane);
        }
    }

    // Setup one black field by position
    public void setupField(Point p) {
        StackPane field = new StackPane();

        field.addEventFilter(MouseEvent.MOUSE_PRESSED, this.moveClickEventHandler);

        if (!this.fields.containsKey(p.x)) {
            this.fields.put(p.x, new HashMap<>());
        }

        this.fields.get(p.x).put(p.y, field);

        this.view.setupField(field, p);
    }

    // Create a piece by team and attach it to given position
    protected void setupPiece(Point position, Team team) {
        CheckerPiece piece = new CheckerPiece(this.view.getSize(), team);

        // Attach to field by position
        piece.attachToFieldByPosition(
            this.fields,
            position,
            this.activeCount
        );

        // Setup click event for field
        piece.setupEvent(this);

        // Add to list of pieces
        this.checkerPieces.add(piece);
    }

    // Get diagonally surrounding fields (within board boundaries) from a given position
    protected ArrayList<Point> surroundingFields(Point p) {
        ArrayList<Point> eligiblePoints = new ArrayList<>();
        Point[] points = new Point[]{
            new Point(p.x - 1, p.y + 1),
            new Point(p.x + 1, p.y + 1),
            new Point(p.x - 1, p.y - 1),
            new Point(p.x + 1, p.y - 1)
        };

        for (int i = 0; i < 4; i++) {
            Point ip = points[i];
            if (this.isPositionValid(ip)) {
                eligiblePoints.add(ip);
            }
        }

        return eligiblePoints;
    }

    // Construct controller
    public AbstractController(View view, int dimension, GridPane grid) {
        this.dimension = dimension;
        this.grid = grid;
        this.moveClickEventHandler = mouseEvent -> this.onFieldClick(mouseEvent.getSource());
        this.view = view;

        this.activeCount.put(Team.BLACK, 0);
        this.activeCount.put(Team.WHITE, 0);
    }

    // Get selected piece
    public CheckerPiece getSelectedPiece() {
        return this.selectedPiece;
    }

    // Set selected piece
    public void setSelectedPiece(CheckerPiece piece) {
        // Remove highlight from currently selected piece
        if (this.selectedPiece != null) {
            this.selectedPiece.assertHighlight(false);
        }

        // Select piece if turn matches the piece's team
        if (this.selectedPiece != piece && isWhiteTurn == (piece.getTeam() == Team.WHITE)) {
            this.selectedPiece = piece;
            this.selectedPiece.assertHighlight(true);

            // Highlight fields around selected piece
            this.highlightEligibleFields(this.selectedPiece);
            return;
        }

        // Remove highlight and reset selectedPiece
        this.normalizeFields();
        this.selectedPiece = null;
    }

    // Setup black fields
    public void setupFields() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = i % 2; j < this.dimension; j += 2) {
                this.setupField(new Point(i + 1, j + 1));
            }
        }
    }
}
