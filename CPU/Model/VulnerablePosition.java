package CPU.Model;

import Model.CheckerPiece;

public class VulnerablePosition {

    protected CheckerPiece opponentPiece;
    protected CheckerPiece piece;

    public VulnerablePosition(CheckerPiece piece, CheckerPiece opponentPiece) {
        this.piece = piece;
        this.opponentPiece = opponentPiece;
    }

    public CheckerPiece getOpponentPiece() {
        return this.opponentPiece;
    }

    public CheckerPiece getPiece() {
        return this.piece;
    }

}
