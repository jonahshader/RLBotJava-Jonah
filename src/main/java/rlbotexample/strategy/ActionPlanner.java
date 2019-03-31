package rlbotexample.strategy;

import rlbot.Bot;
import rlbot.cppinterop.RLBotDll;
import rlbot.flat.BallPrediction;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;
import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;
import rlbotexample.strategy.actions.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ActionPlanner evaluates every possible action that can be taken at a moment
 * and executes the one that is the most important.
 */
public class ActionPlanner {
    private ArrayList<Action> actionSet;
    private Action currentAction;
    private BallPrediction ballPrediction;
    private Renderer renderer;
    private Bot bot;

    public ActionPlanner(Bot bot) {
        this.bot = bot;

        try {
            ballPrediction = RLBotDll.getBallPrediction();
        } catch (IOException ignored) {}

        renderer = BotLoopRenderer.forBotLoop(bot); // updated renderer
        actionSet = new ArrayList<>();

        // populate action set
        actionSet.add(new Idle());
        actionSet.add(new Defense(this));
        actionSet.add(new Offense(this));
        actionSet.add(new GettingBoost(this));

        currentAction = actionSet.get(0); // idle
    }

    public ControlsOutput run(DataPacket input) {
        renderer = BotLoopRenderer.forBotLoop(bot); // updated renderer
        for (Action action : actionSet) {
            action.updateImportance(input);
        }

        Action mostImportantAction = getMostImportantAction();
        if (mostImportantAction != currentAction) {
            currentAction = mostImportantAction;
            currentAction.init(input);
        }
        System.out.println(currentAction.getActionName());
        return currentAction.run(input);
    }

    private Action getMostImportantAction() {
        Action mostImportantAction = actionSet.get(0);
        for (Action action : actionSet)
            if (action.getImportance() > mostImportantAction.getImportance())
                mostImportantAction = action;

        return mostImportantAction;
    }

    public final BallPrediction getBallPrediction() {
        return ballPrediction;
    }

    public Renderer getRenderer() {
        return renderer;
    }
}
