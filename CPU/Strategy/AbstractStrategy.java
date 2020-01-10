package CPU.Strategy;

import CPU.Model.Move;
import Controller.CPURegularCheckersController;

abstract public class AbstractStrategy {

    abstract public Move getMoveOrNull();

    protected CPURegularCheckersController controller;

    public AbstractStrategy(CPURegularCheckersController controller) {
        this.controller = controller;
    }

}
