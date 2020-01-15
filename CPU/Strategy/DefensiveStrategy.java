package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Enum.MoveType;
import Enum.Team;
import Model.Move;
import Model.VulnerablePosition;
import Model.CheckerPiece;
import Model.Field;

import java.awt.*;
import java.util.*;

public class DefensiveStrategy extends AbstractStrategy {

    // Try finding a move to fix vulnerability - else return null
    protected Move findFixingMove(VulnerablePosition vulnerability) {
        Move coveringBehindMove = this.findFixingMoveByCoveringBehind(vulnerability);
        Move movingAwayMove = this.findFixingMoveByMovingAway(vulnerability);

        return movingAwayMove != null ? movingAwayMove : coveringBehindMove;
    }

    // Fix vulnerability by moving another piece behind vulnerable piece to block jump from opponent
    protected Move findFixingMoveByCoveringBehind(VulnerablePosition vulnerability) {
        Point positionToCover = vulnerability.getOpponentMove().getToField().getPosition();

        // Can we move a piece there?
        for(Move legalMove : this.allLegalMoves) {
            // We have to find a legal, regular move that moves another piece behind the vulnerable piece to block jump
            if(legalMove.getMoveType() == MoveType.JUMP || !legalMove.getToField().getPosition().equals(positionToCover)) {
                continue;
            }

            return legalMove;
        }

        return null;
    }

    // Fix vulnerability by moving vulnerable piece away to a new, safe position
    protected Move findFixingMoveByMovingAway(VulnerablePosition vulnerability) {
        // Get all possible moves (away) for piece in vulnerability
        for(Move possibleMove : this.controller.legalMovesForPiece(vulnerability.getPiece())) {
            boolean isSafe = true;

            // Test if (possibly) new diagonal position can be jumped by opponent in next move
            Field field = possibleMove.getToField();
            for(Field diagonalField : this.controller.surroundingFields(field)) {
                CheckerPiece newFieldSurroundingField = diagonalField.getAttachedPieceSecure();

                if (newFieldSurroundingField == null || newFieldSurroundingField.getTeam() == possibleMove.getPiece().getTeam()) {
                    // Diagonal piece does not exist or is on same team as player
                    continue;
                }

                // Piece exists and is on opponent's team
                Field opponentsFieldAfterPossibleJump = this.controller.oppositeDiagonalField(field, diagonalField);
                if (opponentsFieldAfterPossibleJump == null || opponentsFieldAfterPossibleJump.getAttachedPieceSecure() != null) {
                    isSafe = false;
                    break;
                }
            }

            if(isSafe) {
                return possibleMove;
            }
        }

        return null;
    }

    // Find all vulnerable positions (position where opponent can jump over piece) for a given piece
    protected ArrayList<VulnerablePosition> getVulnerabilitiesForPiece(CheckerPiece piece) {
        ArrayList<VulnerablePosition> vulnerablePositions = new ArrayList<>();

        Field pieceField = piece.getParent();

        for(Field opField : this.controller.surroundingFields(pieceField)) {
            CheckerPiece fieldPiece = opField.getAttachedPieceSecure();

            // Is there an opponent on a surrounding piece
            if(fieldPiece == null || fieldPiece.getTeam() == Team.BLACK) {
                continue;
            }

            Field oppositeDiagonalField = this.controller.oppositeDiagonalField(pieceField, opField);

            // Is the piece behind empty so opponent can jump?
            if(
                oppositeDiagonalField != null &&
                oppositeDiagonalField.getAttachedPieceSecure() == null &&
                !this.controller.fieldShouldNotBeConsidered(fieldPiece, oppositeDiagonalField.getPosition())
            ) {
                Move opponentMove = new Move(fieldPiece, oppositeDiagonalField, pieceField);
                vulnerablePositions.add(new VulnerablePosition(piece, opponentMove));
            }
        }

        return vulnerablePositions;
    }

    // Get vulnerabilities for all pieces
    protected ArrayList<VulnerablePosition> getVulnerabilities() {
        ArrayList<VulnerablePosition> vulnerabilities = new ArrayList<>();

        for(CheckerPiece piece : this.controller.getCheckerPieces()) {
            if(!piece.isActive() || piece.getTeam() == Team.WHITE) {
                continue;
            }

            ArrayList<VulnerablePosition> vulnerabilitiesForPiece = this.getVulnerabilitiesForPiece(piece);
            vulnerabilities.addAll(vulnerabilitiesForPiece);
        }

        return vulnerabilities;
    }

    // Risk assess a move (high is worse)
    // One possible jump from opponent equals 1 risk (so n possible jumps equals n risk and is therefore worse than 1 risk)
    protected int riskAssessment(Move opponentMove) {
        int risk = 1;

        ArrayList<Move> jumpMoves = this.jumpsFromPosition(opponentMove.getPiece(), opponentMove.getToField());

        for(Move jumpMove : jumpMoves) {
            risk += this.riskAssessment(jumpMove);
        }

        return risk;
    }

    public DefensiveStrategy(CPURegularCheckersController controller) {
        super(controller);
    }

    // Get a move
    // If multiple moves is available, the positions of the pieces are risk assessed and the solving move for the highest risk is chosen
    public Move getMoveOrNull() {
        this.updateAllLegalMoves();

        ArrayList<VulnerablePosition> vulnerabilities = this.getVulnerabilities();

        // Set risk assessments for all vulnerabilities (if more than 1)
        vulnerabilities.sort((v1, v2) -> {
            if(v1.getRiskLevel() == 0) {
                v1.setRiskLevel(this.riskAssessment(v1.getOpponentMove()));
            }

            if(v2.getRiskLevel() == 0) {
                v2.setRiskLevel(this.riskAssessment(v2.getOpponentMove()));
            }

            return v2.getRiskLevel() - v1.getRiskLevel();
        });

        // ... and try to solve the vulnerabilities ordered in descending order by risk
        for(VulnerablePosition vulnerability : vulnerabilities) {
            Move fixingMove = this.findFixingMove(vulnerability);
            if(fixingMove != null) {
                return fixingMove;
            }
        }

        return null;
    }

}
