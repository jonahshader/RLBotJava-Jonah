package rlbotexample.strategy.actions;

import rlbot.flat.BallPrediction;
import rlbotexample.boost.BoostManager;
import rlbotexample.boost.BoostPad;
import rlbotexample.input.CarData;
import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;
import rlbotexample.strategy.ActionPlanner;
import rlbotexample.vector.Vector2;

/**
 * Boosts towards a large boost.
 */
public class GettingBoost implements Action {
    private static double MAX_IMPORTANCE = 2;
    private static double MIN_IMPORTANCE = 0;

    private double importance = 0;

    private BallPrediction ballPrediction;

    public GettingBoost(ActionPlanner actionPlanner) {
        ballPrediction = actionPlanner.getBallPrediction();
    }

    @Override
    public double getImportance() {
//        System.out.println(importance);
        return importance;
    }

    @Override
    public void updateImportance(DataPacket input) {
//        importance = MIN_IMPORTANCE + (1 - (input.car.boost / 100.0)) * (MAX_IMPORTANCE - MIN_IMPORTANCE);
        importance = (1 - (input.car.boost / 100.0));
        importance = Math.pow(importance, 2);
        importance *= (MAX_IMPORTANCE - MIN_IMPORTANCE);
        importance += MIN_IMPORTANCE;
        boolean boostAvailable = false;
        for (BoostPad boostPad : BoostManager.getFullBoosts()) {
            if (boostPad.isActive())
                boostAvailable = true;
        }
        if (!boostAvailable)
            importance = 0;
    }

    @Override
    public ControlsOutput run(DataPacket input) {
        CarData myCar = input.car;
        Vector2 carPosition = myCar.position.flatten();
        Vector2 ballPosition = input.ball.position.flatten();
//        Vector2 closeTarget = ballPosition.plus(carPosition).scaled(0.5);
        Vector2 closeTarget = ballPosition;
        Vector2 carDirection = myCar.orientation.noseVector.flatten();

        BoostPad closestBoost = BoostManager.getFullBoosts().get(0);
        double closestDistance = closeTarget.distance(closestBoost.getLocation().flatten());

        for (BoostPad boostPad : BoostManager.getFullBoosts()) {
            if ((boostPad.isActive() && boostPad.getLocation().flatten().distance(closeTarget) < closestDistance) || !closestBoost.isActive()) {
                closestDistance = boostPad.getLocation().flatten().distance(closeTarget);
                closestBoost = boostPad;
            }
        }

        Vector2 boostPosition = closestBoost.getLocation().flatten();
        Vector2 carToBoost = boostPosition.minus(carPosition);

        double steerCorrectionRadians = carDirection.correctionAngle(carToBoost);
        boolean steerTooStrong = Math.abs(steerCorrectionRadians) > 2 * Math.PI / 4;
        double turnSpeed = steerCorrectionRadians * 5;

        boolean boost = Math.abs(steerCorrectionRadians) < Math.PI / 8.0;

        return new ControlsOutput()
                .withBoost(boost)
                .withSteer((float) -turnSpeed)
                .withSlide(steerTooStrong)
                .withThrottle(1);
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
        return "Getting Boost";
    }
}
