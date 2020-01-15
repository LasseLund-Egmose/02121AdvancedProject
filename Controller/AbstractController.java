package Controller;

import Enum.MoveType;
import Enum.Setting;
import Enum.Team;
import Model.CheckerPiece;
import Model.Field;
import Model.Move;
import Model.Settings;
import View.GameView;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

abstract public class AbstractController {

    // List of used audio clips (when moving a piece in-game)
    protected static String[] audioClips = new String[] {
            "chipsCollide1.wav", "chipsCollide2.wav", "chipsCollide3.wav", "chipsCollide4.wav"
    };

    protected ArrayList<CheckerPiece> checkerPieces = new ArrayList<>(); // A list of all pieces
    protected HashMap<Integer, HashMap<Integer, Field>> fields = new HashMap<>(); // A map (x -> y -> pane) of all fields

    protected HashMap<Team, Integer> activeCount = new HashMap<>(); // A map (Team -> int) of number of active pieces on each team

    protected ArrayList<CheckerPiece> forcedJumpMoves = new ArrayList<>(); // A list of all pieces with jump moves that has to be done (one has to be selected)
    protected HashMap<Field, Field> possibleJumpMoves = new HashMap<>(); // A map (pane -> jumped position) of all possible jump moves
    protected ArrayList<Field> possibleRegularMoves = new ArrayList<>(); // A list of all possible regular moves

    protected int dimension; // Dimension of board
    protected GridPane grid; // Main grid of board
    protected boolean isWhiteTurn = true; // Keep track of turn
    protected EventHandler<MouseEvent> moveClickEventHandler; // EventHandler for click events on black fields
    protected boolean pieceHighlightLocked = false; // Should highlight be locked to one piece? Happens when jumping multiple pieces in one turn
    protected CheckerPiece selectedPiece = null; // Keep track of selected piece
    protected GameView view; // Reference to view instance
    protected Timeline timeline = new Timeline();
    protected ArrayList<Clip> soundArrayList = new ArrayList<>(); //used to store the paths for each audio file
    public int timeWhite; // White players time variable
    public int timeBlack; // Black players time variable
    public int totalTime = 0; // Total elapsed time of game

    // Setup a piece in each corner
    abstract public void setupPieces();

    // Format int to time string
    public static String formatTime(int timeSeconds) {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        return seconds < 10 ? minutes + ":" + "0" + seconds : minutes + ":" + seconds;
    }

    // Check if a team has won
    protected boolean checkForWin() {
        if (this.activeCount.get(Team.BLACK) == 0) {
            this.view.displayWin(Team.WHITE);
            return true;
        }

        if (this.activeCount.get(Team.WHITE) == 0) {
            this.view.displayWin(Team.BLACK);
            return true;
        }

        return false;
    }

    // Setup game timer
    // If whiteTime or blackTime reaches 0, the opposite team wins.
    public void countDownTimer() {
        this.countDownTimer(true);
    }

    // Set up timer (this specific method call is used with CPU)
    public void countDownTimer(boolean setupBlack) {
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            if (isWhiteTurn) {
                GameView.displayWhiteTimeLeft.setText("White time left: " + formatTime(timeWhite--));
                if (timeWhite <= -2) {
                    timeline.stop();
                    this.view.displayWin(Team.BLACK);
                }
            } else if(setupBlack) {
                GameView.displayBlackTimeLeft.setText("Black time left: " + formatTime(timeBlack--));
                if (timeBlack <= -2) {
                    timeline.stop();
                    this.view.displayWin(Team.WHITE);
                }
            }
            totalTime++;
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // Check if a jump move is eligible (e.g. no piece behind jumped piece)
    // Return field from new position if yes and null if no
    protected Object eligibleJumpMoveOrNull(CheckerPiece thisPiece, Point opponentPosition) {
        Point thisPos = thisPiece.getPosition();
        Point diff = new Point(opponentPosition.x - thisPos.x, opponentPosition.y - thisPos.y);

        Point newPos = (Point) opponentPosition.clone();
        newPos.translate(diff.x, diff.y);

        if (!this.isPositionValid(newPos)) {
            return null;
        }

        Field jumpOver = fields.get(opponentPosition.x).get(opponentPosition.y);
        Field jumpTo = fields.get(newPos.x).get(newPos.y);

        CheckerPiece attachedPiece = jumpOver.getAttachedPieceSecure();

        if (attachedPiece == null || attachedPiece.getTeam() == thisPiece.getTeam()) {
            return null;
        }

        return jumpTo.getAttachedPieceSecure() == null ? jumpTo : null;
    }

    // Highlight fields a selected piece can move to
    protected void highlightEligibleFields(CheckerPiece piece) {
        ArrayList<Move> legalMoves = this.legalMovesForPiece(piece);

        for (Move move : legalMoves) {
            Field toField = move.getToField();

            if (move.getMoveType() == MoveType.JUMP) {
                this.possibleJumpMoves.put(toField, move.getJumpedField());
                this.view.highlightPane(toField);
                continue;
            }

            // Regular move
            this.possibleRegularMoves.add(toField);
            this.view.highlightPane(toField);
        }
    }

    // Check if position is within boundaries of board
    protected boolean isPositionValid(Point p) {
        return p.x >= 1 && p.y >= 1 && p.x <= this.dimension && p.y <= this.dimension;
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

    // Returns boolean whether or not board should rotate
    public boolean onTurnStart() {
        return true;
    }

    // Called every time a piece is moved
    protected boolean onPieceMove(CheckerPiece movedPiece, boolean didJump) {
        return true;
    }

    // Pause timer
    public void pauseTime() {
        this.timeline.pause();
    }

    //plays one of the four move sounds randomly
    protected void playOnMoveSound() {
        Random randomSound = new Random();

        // Get a random sound clip
        Clip clip = this.soundArrayList.get(randomSound.nextInt(soundArrayList.size()));
        clip.setFramePosition(0); // Reset the clip to start
        clip.start(); // Play the clip
    }

    // Reset time for both teams
    public void setTime() {
        this.timeWhite = this.timeBlack = Settings.getInt(Setting.Time);
    }

    // Set black time from state
    public void setTimeBlack(int timeBlack) {
        this.timeBlack = timeBlack;
    }

    // Set white time from state
    public void setTimeWhite(int timeWhite) {
        this.timeWhite = timeWhite;
    }

    // Reset total time
    public void setTotalTime() {
        this.totalTime = 0;
    }

    // Set total time from state
    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
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

    // Setup black field from exsisting field
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

    // Setup audio clips from this.soundNames
    protected void setupSounds() {
        try {
            for (String name : AbstractController.audioClips) {

                // Get audio clip from assets directory
                Clip clip;
                clip = AudioSystem.getClip();
                BufferedInputStream bis = new BufferedInputStream(this.getClass().getResourceAsStream("/assets/" + name));

                try {
                    // Add clip to soundArrayList
                    AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
                    clip.open(ais);
                    this.soundArrayList.add(clip);
                } catch (UnsupportedAudioFileException e) {
                    System.out.println("Error - Unsupported audio file:" + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IO error: " + e.getMessage());
                }
            }

        } catch (LineUnavailableException e) {
            System.out.println("Error occurred in audio line: " + e.getMessage());
        }
    }

    // Start timer
    public void startTime() {
        timeline.play();
    }

    // Get diagonally surrounding fields (within board boundaries) from a given position
    protected ArrayList<Point> surroundingPoints(Point p) {
        ArrayList<Point> eligiblePoints = new ArrayList<>();
        Point[] points = surroundingFieldsPosition(p);

        for (int i = 0; i < 4; i++) {
            Point ip = points[i];
            if (this.isPositionValid(ip)) {
                eligiblePoints.add(ip);
            }
        }

        return eligiblePoints;
    }

    // Get position of surrounding fields
    protected Point[] surroundingFieldsPosition(Point p) {
        return new Point[]{
                new Point(p.x - 1, p.y + 1),
                new Point(p.x + 1, p.y + 1),
                new Point(p.x - 1, p.y - 1),
                new Point(p.x + 1, p.y - 1)
        };
    }

    // Construct controller
    public AbstractController(GameView view, GridPane grid) {
        this.grid = grid;
        this.moveClickEventHandler = mouseEvent -> this.onFieldClick(mouseEvent.getSource());
        this.view = view;

        this.activeCount.put(Team.BLACK, 0);
        this.activeCount.put(Team.WHITE, 0);

        this.dimension = Settings.getInt(Setting.Dimension);

        this.setupSounds();
        this.setTime();
    }


    public AbstractController(
            GameView view,
            GridPane grid,
            ArrayList<CheckerPiece> checkerPieces,
            HashMap<Integer, HashMap<Integer, Field>> fields,
            boolean isWhiteTurn,
            HashMap<Team, Integer> activeCount
    ) {
        this.view = view;
        this.grid = grid;
        this.checkerPieces = checkerPieces;
        this.fields = fields;
        this.isWhiteTurn = isWhiteTurn;
        this.activeCount = activeCount;

        this.moveClickEventHandler = mouseEvent -> this.onFieldClick(mouseEvent.getSource());

        this.setupSounds();
    }

    /*
     * Public methods
     */

    // Handle a jump move
    public void doJumpMove(Field toField, Field jumpedField) {
        // Detach (remove) jumped Model.CheckerPiece
        jumpedField.getAttachedPieceSecure().detach(this.activeCount);

        // Handle rest of move as a regular move
        this.doRegularMove(toField, true);
    }

    // Handle a regular move
    public void doRegularMove(Field toField, boolean didJump) {

        //play on move sound
        playOnMoveSound();

        // Attach selected piece to chosen field
        this.getSelectedPiece().attachToField(toField, this.activeCount);

        // Remove highlight fields
        this.normalizeFields();

        // Reset highlight-related properties
        this.possibleJumpMoves.clear();
        this.possibleRegularMoves.clear();

        // Disable pause button
        this.view.setPauseButtonActive(false);

        // Finish turn if onPieceMove returns true
        if (this.onPieceMove(this.selectedPiece, didJump)) {
            // Reset forced jump moves
            this.forcedJumpMoves.clear();

            // Reset selected field
            this.selectedPiece.setHighlight(false);
            this.selectedPiece = null;

            // Finish turn
            this.finishTurn();
        }
    }

    // Should a piece be allowed to move to the given position? - Default yes
    public boolean fieldShouldNotBeConsidered(CheckerPiece piece, Point position) {
        return false;
    }

    // Check if game is over, toggle isWhiteTurn and setup turn for other team
    public void finishTurn() {
        this.isWhiteTurn = !this.isWhiteTurn;
        this.pieceHighlightLocked = false;

        // Enable pause button
        this.view.setPauseButtonActive(true);

        // Is game won or can we play on?
        if (!checkForWin()) {
            this.view.setupDisplayTurn(this.isWhiteTurn);
            if (this.onTurnStart()) {
                this.view.rotate();
            }
        }
    }

    // Get all "legal" moves for a given piece
    public ArrayList<Move> legalMovesForPiece(CheckerPiece piece) {
        ArrayList<Move> legalMoves = new ArrayList<>();

        if (!piece.isActive()) {
            return legalMoves;
        }

        // Iterate surrounding diagonal fields of given piece
        for (Point p : this.surroundingPoints(piece.getPosition())) {
            if (this.fieldShouldNotBeConsidered(piece, p)) {
                continue;
            }

            // Get pane of current field
            Field field = this.fields.get(p.x).get(p.y);
            CheckerPiece attachedCheckerPiece = field.getAttachedPieceSecure();

            // Is this position occupied - and is it possible to jump it?
            if (attachedCheckerPiece != null && attachedCheckerPiece.getTeam() != piece.getTeam()) {
                Object eligibleJumpMove = this.eligibleJumpMoveOrNull(piece, p);

                // Check if jump move is eligible - per eligibleJumpMoveOrNull
                if (eligibleJumpMove instanceof Field) {
                    // Handle jump move if not null (e.g. instance of Field)
                    Field eligibleJumpMovePane = (Field) eligibleJumpMove;

                    // Add new move to legalMoves
                    legalMoves.add(new Move(piece, eligibleJumpMovePane, field));
                }

                continue;
            }

            // Else allow a regular move if a player isn't forced to do a jump
            if (this.forcedJumpMoves.size() == 0 && attachedCheckerPiece == null) {
                legalMoves.add(new Move(piece, field));
            }
        }

        // Clean any regular moves mistakenly added to available moves (when forced jump moves are present)
        if (this.forcedJumpMoves.size() > 0) {
            for (Move m : legalMoves) {
                if (m.getMoveType() != MoveType.REGULAR) {
                    continue;
                }

                legalMoves.remove(m);
            }
        }

        return legalMoves;
    }

    // Remove highlights from highlighted fields
    public void normalizeFields() {
        ArrayList<Field> allHighlightedPanes = new ArrayList<>();
        allHighlightedPanes.addAll(this.possibleJumpMoves.keySet());
        allHighlightedPanes.addAll(this.possibleRegularMoves);

        this.possibleJumpMoves.clear();
        this.possibleRegularMoves.clear();

        for (Field field : allHighlightedPanes) {
            this.view.normalizePane(field);
        }
    }

    // Get field diagonally on the other side of mainField compared to diagonalField
    public Field oppositeDiagonalField(Field mainField, Field diagonalField) {
        Point mainFieldPosition = mainField.getPosition();
        Point diagonalFieldPosition = diagonalField.getPosition();

        Point diff = new Point(
                mainFieldPosition.x - diagonalFieldPosition.x,
                mainFieldPosition.y - diagonalFieldPosition.y
        );

        Point otherDiagonalPosition = new Point(mainFieldPosition.x + diff.x, mainFieldPosition.y + diff.y);

        return this.isPositionValid(otherDiagonalPosition) ?
                this.fields.get(otherDiagonalPosition.x).get(otherDiagonalPosition.y) : null;
    }

    // Setup black fields
    public void setupFields() {
        for (int i = 0; i < this.dimension; i++) {
            for (int j = (i + 1) % 2; j < this.dimension; j += 2) {
                this.setupField(new Point(j + 1, i + 1));
            }
        }
    }

    // Get fields (diagonally) surrounding a given field
    public ArrayList<Field> surroundingFields(Field f) {
        ArrayList<Field> fields = new ArrayList<>();
        ArrayList<Point> points = this.surroundingPoints(f.getPosition());

        for (Point p : points) {
            fields.add(this.fields.get(p.x).get(p.y));
        }

        return fields;
    }

    /*
     * Getters and setters
     */

    public boolean isWhiteTurn() {
        return this.isWhiteTurn;
    }

    public HashMap<Team, Integer> getActiveCount() {
        return this.activeCount;
    }

    public ArrayList<CheckerPiece> getCheckerPieces() {
        return this.checkerPieces;
    }

    public HashMap<Integer, HashMap<Integer, Field>> getFields() {
        return this.fields;
    }

    public ArrayList<CheckerPiece> getForcedJumpMoves() {
        return this.forcedJumpMoves;
    }

    public CheckerPiece getSelectedPiece() {
        return this.selectedPiece;
    }

    // Get view instance
    public GameView getView() {
        return this.view;
    }

    // Set selected piece
    public void setSelectedPiece(CheckerPiece piece) {
        if (this.selectedPiece == piece) {
            this.normalizeFields();
            this.selectedPiece.setHighlight(false);
            this.selectedPiece = null;
            return;
        }

        if (piece == null || this.pieceHighlightLocked) {
            return;
        }

        // Remove highlight from currently selected piece
        if (this.selectedPiece != null) {
            this.normalizeFields();
            this.selectedPiece.setHighlight(false);
        }

        // Select piece if turn matches the piece's team
        if (this.isWhiteTurn == (piece.getTeam() == Team.WHITE)) {
            this.selectedPiece = piece;
            this.selectedPiece.setHighlight(true);

            // Highlight fields around selected piece
            this.highlightEligibleFields(this.selectedPiece);
            return;
        }

        // Reset selectedPiece
        this.selectedPiece = null;
    }
}
