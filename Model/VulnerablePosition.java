package Model;

public class VulnerablePosition {

    protected Move opponentMove;
    protected CheckerPiece piece;
    protected int riskLevel = 0;

    public VulnerablePosition(CheckerPiece piece, Move opponentPiece) {
        this.piece = piece;
        this.opponentMove = opponentPiece;
    }

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
