package rlbotexample.strategy.actions;

import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;

/**
 * Just idle when there is nothing else to do.
 */
public class Idle implements Action {
    private static double MAX_IMPORTANCE = 0.01;

    @Override
    public double getImportance() {
        return MAX_IMPORTANCE;
    }

    @Override
    public void updateImportance(DataPacket input) {

    }

    @Override
    public ControlsOutput run(DataPacket input) {
        return new ControlsOutput(); // don't move
    }

    @Override
    public void init(DataPacket input) {

    }

    @Override
    public double getMaxImportance() {
        return MAX_IMPORTANCE;
    }

    @Override
    public String getActionName() {
        return "Idle";
    }
}
