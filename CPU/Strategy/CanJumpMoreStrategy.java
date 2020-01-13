package CPU.Strategy;

import Controller.CPURegularCheckersController;
import Model.Move;

public class CanJumpMoreStrategy extends AbstractStrategy {

    protected Move lastJump = null;

    public CanJumpMoreStrategy(CPURegularCheckersController controller) {
        super(controller);
    }

    public Move getMoveOrNull() {
        if(this.lastJump == null) {
            return null;
        }

        // TODO: Handle multi jumps

        return null;
    }

    public void setLastJump(Move lastJump) {
        this.lastJump = lastJump;
    }
}
