package CPU.Strategy;

import Model.Move;
import Model.VulnerablePosition;
import Controller.CPURegularCheckersController;
import Enum.Team;
import Model.CheckerPiece;
import Model.Field;

import java.util.ArrayList;
import java.util.HashMap;

public class DefensiveStrategy extends AbstractStrategy {

    protected ArrayList<VulnerablePosition> getDangerousOpponents(CheckerPiece piece) {
        ArrayList<VulnerablePosition> vulnerablePosition = new ArrayList<>();

        Field pieceField = piece.getParent();

        for(Field f : this.controller.getSurroundingFields(pieceField)) {
            CheckerPiece fieldPiece = f.getAttachedPiece();

            if(fieldPiece == null || fieldPiece.getTeam() == Team.BLACK) {
                continue;
            }

            Field oppositeDiagonalField = this.controller.getOppositeDiagonalField(pieceField, f);

            if(oppositeDiagonalField != null && oppositeDiagonalField.getAttachedPiece() == null) {
                vulnerablePosition.add(new VulnerablePosition(piece, fieldPiece));
            }
        }

        return vulnerablePosition;
    }

    protected HashMap<CheckerPiece, ArrayList<VulnerablePosition>> getVulnerabilities() {
        HashMap<CheckerPiece, ArrayList<VulnerablePosition>> vulnerabilities = new HashMap<>();

        for(CheckerPiece piece : this.controller.getCheckerPieces()) {
            if(!piece.isActive() || piece.getTeam() == Team.WHITE) {
                continue;
            }

            ArrayList<VulnerablePosition> dangerousOpponents = this.getDangerousOpponents(piece);

            if(dangerousOpponents.size() > 0) {
                vulnerabilities.put(piece, this.getDangerousOpponents(piece));
            }
        }

        return vulnerabilities;
    }

    public DefensiveStrategy(CPURegularCheckersController controller) {
        super(controller);
    }

    public Move getMoveOrNull() {
        this.updateAllLegalMoves();

        System.out.println("Defensive: " + this.getVulnerabilities());

        return null;
    }

}
