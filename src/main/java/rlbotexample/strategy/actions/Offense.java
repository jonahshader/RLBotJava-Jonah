package rlbotexample.strategy.actions;

import rlbot.flat.BallPrediction;
import rlbot.render.Renderer;
import rlbotexample.input.CarData;
import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;
import rlbotexample.strategy.ActionPlanner;
import rlbotexample.strategy.BallRace;
import rlbotexample.strategy.movement.DriveToTarget;
import rlbotexample.vector.Vector2;


/**
 * When executed, the car will curve and make a shot towards the goal.
 */

public class Offense implements Action {
    private static double MAX_IMPORTANCE = 2.1;

    private double importance = 0;

    private BallPrediction ballPrediction;
    private Renderer renderer;

    public Offense(ActionPlanner actionPlanner) {
        ballPrediction = actionPlanner.getBallPrediction();
        this.renderer = actionPlanner.getRenderer();
    }

    @Override
    public double getImportance() {
        return importance > MAX_IMPORTANCE ? MAX_IMPORTANCE : importance;
    }

    @Override
    public void updateImportance(DataPacket input) {
        // calculate importance based on amount of boost and who will reach ball first
//        importance = (BallRace.whoGetBallFirst(input) == input.car ? 2.0 : -2.0);
        CarData closestCar = BallRace.whoGetBallFirst(input);
        if (closestCar == input.car) {
            importance = MAX_IMPORTANCE;
        } else {
            if (closestCar != null) {
                double myTimeToTarget = BallRace.getTimeToTarget(input.car, input.ball.position.flatten());
//                System.out.println(myTimeToTarget);
                double closestCarTimeToTarget = BallRace.getTimeToTarget(closestCar, input.ball.position.flatten());
                importance = myTimeToTarget / closestCarTimeToTarget;
            } else {
                importance = 0;
            }
        }
        if (input.ball.velocity.magnitude() < 0.1 && input.ball.spin.magnitude() < 0.1) {
            importance += 2.5;
        }
    }

    @Override
    public ControlsOutput run(DataPacket input) {

        // go to the ball
        Vector2 ballPosition = input.ball.position.flatten();
        CarData myCar = input.car;
        Vector2 carPosition = myCar.position.flatten();

        Vector2 driveTarget = new Vector2(ballPosition.x, ballPosition.y + ((myCar.team == 0) ? -100 : 100));
        Vector2 targetDistanceVelOffset = input.ball.velocity.flatten();

        // Subtract the two positions to get a vector pointing from the car to the ball.
        Vector2 carToBall = ballPosition.minus(carPosition);
        double distanceToBall = carToBall.magnitude();
        driveTarget = driveTarget.plus(targetDistanceVelOffset.scaled(carToBall.magnitude() * 0.0004));
        boolean jumpToBall = distanceToBall < 400 && input.ball.position.z > 200 && input.ball.position.z < 400;

        return DriveToTarget.driveToTarget(myCar, driveTarget, true, renderer)
                .withJump(jumpToBall && myCar.hasWheelContact);
    }

    @Override
    public void init(DataPacket input) {
        // nothing needed
    }

    @Override
    public double getMaxImportance() {
        return MAX_IMPORTANCE;
    }

    @Override
    public String getActionName() {
        return "Offense";
    }
}
