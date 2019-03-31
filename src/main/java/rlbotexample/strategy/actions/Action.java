package rlbotexample.strategy.actions;

import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;

public interface Action {
    double getImportance();     // returns a double from 0 to MAX_IMPORTANCE indicating how important this action is
    void updateImportance(DataPacket input);    // updates the importance value
    ControlsOutput run(DataPacket input);       // runs this action
    void init(DataPacket input);                // inits this action
    double getMaxImportance();
    String getActionName();
}
