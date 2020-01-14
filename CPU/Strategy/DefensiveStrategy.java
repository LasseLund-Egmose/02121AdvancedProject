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

    protected Move findFixingMove(VulnerablePosition vulnerability) {
        Move coveringBehindMove = this.findFixingMoveByCoveringBehind(vulnerability);
        Move movingAwayMove = this.findFixingMoveByMovingAway(vulnerability);

        return coveringBehindMove != null ? coveringBehindMove : movingAwayMove;
    }

    protected Move findFixingMoveByCoveringBehind(VulnerablePosition vulnerability) {
        Point positionToCover = vulnerability.getOpponentMove().getToField().getPosition();

        // Can we move a piece there?
        for(Move legalMove : this.allLegalMoves) {
            if(legalMove.getMoveType() == MoveType.JUMP || !legalMove.getToField().getPosition().equals(positionToCover)) {
                continue;
            }

            return legalMove;
        }

        return null;
    }

    // TODO
    protected Move findFixingMoveByMovingAway(VulnerablePosition vulnerability) {
        return null;
    }

    protected ArrayList<VulnerablePosition> getVulnerabilitiesForPiece(CheckerPiece piece) {
        ArrayList<VulnerablePosition> vulnerablePositions = new ArrayList<>();

        Field pieceField = piece.getParent();

        for(Field opField : this.controller.getSurroundingFields(pieceField)) {
            CheckerPiece fieldPiece = opField.getAttachedPiece();

            if(fieldPiece == null || fieldPiece.getTeam() == Team.BLACK) {
                continue;
            }

            Field oppositeDiagonalField = this.controller.getOppositeDiagonalField(pieceField, opField);

            if(
                oppositeDiagonalField != null &&
                oppositeDiagonalField.getAttachedPiece() == null &&
                !this.controller.fieldShouldNotBeConsidered(fieldPiece, oppositeDiagonalField.getPosition())
            ) {
                Move opponentMove = new Move(fieldPiece, oppositeDiagonalField, pieceField);
                vulnerablePositions.add(new VulnerablePosition(piece, opponentMove));
            }
        }

        return vulnerablePositions;
    }

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