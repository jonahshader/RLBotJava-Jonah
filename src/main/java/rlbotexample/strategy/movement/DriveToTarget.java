package rlbotexample.strategy.movement;

import rlbotexample.input.CarData;
import rlbotexample.output.ControlsOutput;
import rlbotexample.vector.Vector2;

public class DriveToTarget {
    public static ControlsOutput driveToTarget(CarData car, Vector2 target, boolean jumpOnWalls, boolean allowBoost) {
        Vector2 carPosition = car.position.flatten();
        Vector2 carDirection = car.orientation.noseVector.flatten();

        Vector2 carToTarget = target.minus(carPosition);

        double steerCorrectionsRadians = carDirection.correctionAngle(carToTarget);
        boolean steerTooStrong = Math.abs(steerCorrectionsRadians) < Math.PI / 2.5;
        boolean boost = Math.abs(steerCorrectionsRadians) < Math.PI / 8.0;
        double turnSpeed = steerCorrectionsRadians * 5;

        return new ControlsOutput()
                .withBoost(!steerTooStrong && boost && allowBoost)
                .withSteer(steerTooStrong ? (float) turnSpeed : (float) -turnSpeed)
                .withSlide(steerTooStrong)
                .withJump(car.position.z > 100 && car.hasWheelContact && jumpOnWalls)
                .withThrottle(steerTooStrong ? -1 : 1);
    }
}
