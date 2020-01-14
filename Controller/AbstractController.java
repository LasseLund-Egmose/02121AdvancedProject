package Controller;

import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import View.GameView;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static javafx.animation.Animation.Status.STOPPED;

abstract public class AbstractController {

    protected ArrayList<CheckerPiece> checkerPieces = new ArrayList<>(); // A list of all pieces
    protected HashMap<Integer, HashMap<Integer, Field>> fields = new HashMap<>(); // A map (x -> y -> pane) of all fields

    protected HashMap<Team, Integer> activeCount = new HashMap<>(); // A map (Team -> int) of number of active pieces on each team

    protected ArrayList<CheckerPiece> forcedJumpMoves = new ArrayList<>(); // A list of all pieces with jump moves that has to be done (one has to be selected)
    protected HashMap<Field, Field> possibleJumpMoves = new HashMap<>(); // A map (pane -> jumped position) of all possible jump moves
    protected ArrayList<Field> possibleRegularMoves = new ArrayList<>(); // A list of all possible regular moves

    protected int dimension; // Dimension of board
    protected GridPane grid;
    protected boolean isWhiteTurn = true; // Keep track of turn
    protected EventHandler<MouseEvent> moveClickEventHandler; // EventHandler for click events on black fields
    protected boolean pieceHighlightLocked = false; // Should highlight be locked to one piece? Happens when jumping multiple pieces in one turn
    protected CheckerPiece selectedPiece = null; // Keep track of selected piece
    protected GameView view; // Reference to view instance
    protected Timeline timeline = new Timeline();
    public static int timeWhite = 300;
    public static int timeBlack = 300;
    public static int totalTime = 0;
    protected static final String path = "./src/assets/chipsCollide4.wav";
    protected AudioClip audioclip = new AudioClip(new File(path).toURI().toString());
    protected int minFieldSize=8; //minimum field size
    protected int maxFieldSize=100; //maximum field size

    public GameView getView(GameView view){
        return view;
    }

    public

    public void countDownTimer() {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            if (isWhiteTurn) {
                GameView.displayWhiteTimeLeft.setText("White time left: " + formatTime(timeWhite--));
                totalTime++;
                if (timeWhite <= -2) {
                    timeline.stop();
                    this.view.displayWin("Black won");
                }
            } else {
                GameView.displayBlackTimeLeft.setText("Black time left: " + formatTime(timeBlack--));
                totalTime++;
                if (timeBlack <= -2) {
                    timeline.stop();
                    this.view.displayWin("White won");
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public static String formatTime(int timeSeconds) {
            int minutes = timeSeconds / 60;
            int seconds = timeSeconds % 60;
            String formattedTime = "";
            if (seconds < 10) {
                formattedTime = minutes + ":" +  "0" + seconds;
            } else {
                formattedTime = minutes + ":" + seconds;
            }
            return formattedTime;
    }

    // Setup a piece in each corner
    abstract public void setupPieces();

    // Check if a team has won
    protected void checkForWin() {
        if (this.activeCount.get(Team.BLACK) == 0) {
            this.view.displayWin(Team.WHITE);
        }

        if (this.activeCount.get(Team.WHITE) == 0) {
            this.view.displayWin(Team.BLACK);
        }
    }

    // Handle a jump move
    protected void doJumpMove(Field toField, Field jumpedField) {
        // Detach (remove) jumped Model.CheckerPiece
        jumpedField.getAttachedPiece().detach(this.activeCount);

        // Handle rest of move as a regular move
        this.doRegularMove(toField, true);
    }

    // Handle a regular move
    protected void doRegularMove(Field toField, boolean didJump) {
        //play on move sound
        playOnMoveSound();

        // Attach selected piece to chosen field
        this.getSelectedPiece().attachToField(toField, this.activeCount);

        // Remove highlight fields
        this.normalizeFields();

        // Reset highlight-related properties
        this.possibleJumpMoves.clear();
        this.possibleRegularMoves.clear();

        // Finish turn if onPieceMove returns true
        if(this.onPieceMove(this.selectedPiece, didJump)) {
            // Reset forced jump moves
            this.forcedJumpMoves.clear();

            // Reset selected field
            this.selectedPiece.assertHighlight(false);
            this.selectedPiece = null;

            // Finish turn
            this.finishTurn();
        }
    }

    protected void playOnMoveSound() {
        this.audioclip.play();
    }

    // Check if a jump move is eligible (e.g. no piece behind jumped piece)
    // Return field from new position if yes and null if no
    protected Object eligibleJumpMoveOrNull(CheckerPiece thisPiece, Point opponentPosition) {
        Point thisPos = thisPiece.getPosition();
        Point diff = new Point(opponentPosition.x - thisPos.x, opponentPosition.y - thisPos.y);

        Point newPos = (Point) opponentPosition.clone();
        newPos.translate(diff.x, diff.y);

        if(!this.isPositionValid(newPos)) {
            return null;
        }

        Field jumpOver = fields.get(opponentPosition.x).get(opponentPosition.y);
        Field jumpTo = fields.get(newPos.x).get(newPos.y);

        if(jumpOver.getAttachedPiece() == null || jumpOver.getAttachedPiece().getTeam() == thisPiece.getTeam()) {
            return null;
        }

        return jumpTo.getAttachedPiece() == null ? jumpTo : null;
    }

    // Check if game is over, toggle isWhiteTurn and setup turn for other team
    protected void finishTurn() {
        this.isWhiteTurn = !this.isWhiteTurn;
        this.pieceHighlightLocked = false;

        checkForWin();

        this.view.setupDisplayTurn(this.isWhiteTurn);
        this.onTurnStart();

        this.view.rotate();
    }

    // Should a piece be allowed to move to the given position? - Default yes
    protected boolean fieldShouldNotBeConsidered(CheckerPiece piece, Point position) {
        return false;
    }

    // Highlight fields a selected piece can move to
    protected void highlightEligibleFields(CheckerPiece piece) {
        // Iterate surrounding diagonal fields of given piece
        for (Point p : this.surroundingFields(piece.getPosition())) {
            if(this.fieldShouldNotBeConsidered(piece, p)) {
                continue;
            }

            // Get pane of current field
            Field field = this.fields.get(p.x).get(p.y);

            // Is this position occupied - and is it possible to jump it?
            if (field.getChildren().size() > 0) {
                Object eligibleJumpMove = this.eligibleJumpMoveOrNull(piece, p);

                // Check if jump move is eligible - per eligibleJumpMoveOrNull
                if (eligibleJumpMove instanceof Field) {
                    // Handle jump move if not null (e.g. instance of Field)
                    Field eligibleJumpMovePane = (Field) eligibleJumpMove;

                    this.possibleJumpMoves.put(eligibleJumpMovePane, field);
                    this.view.highlightPane(eligibleJumpMovePane);
                }
            } else if (this.forcedJumpMoves.size() == 0) { // Else allow a regular move if a player isn't forced to do a jump
                this.possibleRegularMoves.add(field);
                this.view.highlightPane(field);
            }
        }
    }

    // Check if position is within boundaries of board
    protected boolean isPositionValid(Point p) {
        return p.x >= 1 && p.y >= 1 && p.x <= this.dimension && p.y <= this.dimension;
    }

    // Remove highlights from highlighted fields
    protected void normalizeFields() {
        ArrayList<Field> allHighlightedPanes = new ArrayList<>();
        allHighlightedPanes.addAll(this.possibleJumpMoves.keySet());
        allHighlightedPanes.addAll(this.possibleRegularMoves);

        for (Field field : allHighlightedPanes) {
            this.view.normalizePane(field);
        }
    }

    // Handle click on black field
    protected void onFieldClick(Object clickedElement) {
        // Check if Field is clicked and a selectedPiece is chosen
        if (!(clickedElement instanceof Field) || this.getSelectedPiece() == null) {
            return;
        }

        Field clickedElementField = (Field) clickedElement;

        // Is a jump move chosen?
        if (this.possibleJumpMoves.containsKey(clickedElement)) {
            this.doJumpMove(clickedElementField, this.possibleJumpMoves.get(clickedElement));
            return;
        }

        // Is a regular move chosen?
        if (this.possibleRegularMoves.contains(clickedElement)) {
            this.doRegularMove(clickedElementField, false);
        }
    }

    protected void onSelectedPieceClick() {}

    public void onTurnStart() {}

    // Called every time a piece is moved
    protected boolean onPieceMove(CheckerPiece movedPiece, boolean didJump) {
        return true;
    }

    // Setup one black field by position
    protected void setupField(Point p) {
        Field field = new Field(p);

        field.addEventFilter(MouseEvent.MOUSE_PRESSED, this.moveClickEventHandler);

        if (!this.fields.containsKey(p.x)) {
            this.fields.put(p.x, new HashMap<>());
        }

        this.fields.get(p.x).put(p.y, field);

        this.view.setupField(field, p);
    }

    public void setupField(Field field) {
        field.addEventFilter(MouseEvent.MOUSE_PRESSED, this.moveClickEventHandler);
        this.view.setupField(field, field.getPosition());
    }

    // Create a piece by team and attach it to given position
    protected void setupPiece(Point position, Team team) {
        CheckerPiece piece = new CheckerPiece(this.view.getFieldSize(), team);

        Field field = this.fields.get(position.x).get(position.y);

        // Attach to field by position
        piece.attachToField(field, this.activeCount);

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
    public AbstractController(GameView view, int dimension, GridPane grid) {
        this.dimension = dimension;
        this.grid = grid;
        this.moveClickEventHandler = mouseEvent -> this.onFieldClick(mouseEvent.getSource());
        this.view = view;

        this.activeCount.put(Team.BLACK, 0);
        this.activeCount.put(Team.WHITE, 0);
        countDownTimer();
    }


    public AbstractController(
            GameView view,
            int dimension,
            GridPane grid,
            ArrayList<CheckerPiece> checkerPieces,
            HashMap<Integer, HashMap<Integer, Field>> fields,
            boolean isWhiteTurn,
            HashMap<Team, Integer> activeCount
    ) {
        this.view = view;
        this.dimension = dimension;
        this.grid = grid;
        this.checkerPieces = checkerPieces;
        this.fields = fields;
        this.isWhiteTurn = isWhiteTurn;
        this.activeCount = activeCount;
        this.moveClickEventHandler = mouseEvent -> this.onFieldClick(mouseEvent.getSource());
    }

    public ArrayList<CheckerPiece> getCheckerPieces() {
        return checkerPieces;
    }

    public HashMap<Integer, HashMap<Integer, Field>> getFields() {
        return fields;
    }

    public HashMap<Team, Integer> getActiveCount() {
        return activeCount;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    // Get selected piece
    public CheckerPiece getSelectedPiece() {
        return this.selectedPiece;
    }

    // Set selected piece
    public void setSelectedPiece(CheckerPiece piece) {
        if(piece != null && this.selectedPiece == piece) {
            this.onSelectedPieceClick();
        }

        if(this.pieceHighlightLocked) {
            return;
        }

        // Remove highlight from currently selected piece
        if (this.selectedPiece != null) {
            this.normalizeFields();
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

        // Reset selectedPiece
        this.selectedPiece = null;
    }

    // Setup black fields
    public void setupFields() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = (i + 1) % 2; j < this.dimension; j += 2) {
                this.setupField(new Point(j + 1, i + 1));
            }
        }
    }
}
