package Controller;

import Enum.Team;

import CPU.CPU;
import Model.CheckerPiece;
import Model.Field;
import View.GameView;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: Needs comments
public class CPURegularCheckersController extends RegularCheckersController {

    // CPU plays as Team.BLACK

    protected CPU cpu;

    protected boolean onPieceMove(CheckerPiece movedPiece, boolean didJump) {
        return super.onPieceMove(movedPiece, didJump, !this.isWhiteTurn);
    }

    public CPURegularCheckersController(GameView view, int dimension, GridPane grid) {
        super(view, dimension, grid);

        this.cpu = new CPU(this);
    }

    public CPURegularCheckersController(
            GameView view,
            int dimension,
            GridPane grid,
            ArrayList<CheckerPiece> checkerPieces,
            HashMap<Integer, HashMap<Integer, Field>> fields,
            boolean isWhiteTurn,
            HashMap<Team, Integer> activeCount
    ) {
        super(view, dimension, grid, checkerPieces, fields, isWhiteTurn, activeCount);

        this.cpu = new CPU(this);
        this.cpu.initStrategies();
    }

    public boolean onTurnStart() {
        super.onTurnStart();

        if(this.isWhiteTurn) {
            return false;
        }

        this.cpu.takeTurn();

        return false;
    }

    public void setSelectedPiece(CheckerPiece piece) {
        // Do not allow moving black pieces
        if(!this.isWhiteTurn) {
            return;
        }

        super.setSelectedPiece(piece);
    }

    public void setSelectedPieceCPU(CheckerPiece piece) {
        if(this.isWhiteTurn) {
            return;
        }

        this.selectedPiece = piece;
    }

    public void setupPieces() {
        super.setupPieces();

        this.cpu.initStrategies();
    }

    public void countDownTimer() {
        GameView.displayBlackTimeLeft.setText("CPU");
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            if (isWhiteTurn) {
                GameView.displayWhiteTimeLeft.setText("White time left: " + formatTime(timeWhite--));
                totalTime++;
                if (timeWhite <= -2) {
                    timeline.stop();
                    this.view.displayWin(Team.BLACK);
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
