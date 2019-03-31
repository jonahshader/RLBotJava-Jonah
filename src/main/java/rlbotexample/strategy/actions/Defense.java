package rlbotexample.strategy.actions;

import rlbot.flat.BallPrediction;
import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;
import rlbotexample.strategy.ActionPlanner;

public class Defense implements Action {
    private static double MAX_IMPORTANCE = 3.0;
    private BallPrediction ballPrediction;

    public Defense(ActionPlanner actionPlanner) {
        ballPrediction = actionPlanner.getBallPrediction();
    }

    @Override
    public double getImportance() {
        return 0;
    }

    @Override
    public void updateImportance(DataPacket input) {

    }

    @Override
    public ControlsOutput run(DataPacket input) {
        return null; //TODO:
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
        return "Defense";
    }
}

