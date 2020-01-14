package Model;

public class VulnerablePosition {

    /*
     * This class is used to describe a situation where a certain CheckerPiece is vulnerable (can be jumped)
     * by a specific move from the opponent
     */

    protected Move opponentMove;
    protected CheckerPiece piece;
    protected int riskLevel = 0; // Risk level (high is worse) - not always needed/set

    public VulnerablePosition(CheckerPiece piece, Move opponentPiece) {
        this.piece = piece;
        this.opponentMove = opponentPiece;
    }

    /*
     * Getters and setters
     */

    public Move getOpponentMove() {
        return this.opponentMove;
    }

    public CheckerPiece getPiece() {
        return this.piece;
    }

    public int getRiskLevel() {
        return this.riskLevel;
    }

    public void setRiskLevel(int riskLevel) {
        this.riskLevel = riskLevel;
    }

}
