package rlbotexample.strategy.movement;

import rlbot.render.Renderer;
import rlbotexample.input.CarData;
import rlbotexample.output.ControlsOutput;
import rlbotexample.vector.Vector2;
import rlbotexample.vector.Vector3;

import java.awt.*;

public class DriveToTarget {
    public static ControlsOutput driveToTarget(CarData car, Vector2 target, boolean allowBoost, Renderer renderer) {
        Vector2 carPosition = car.position.flatten();
        Vector2 carDirection = car.orientation.noseVector.flatten();

        Vector2 carToTarget = target.minus(carPosition);
//        Vector2 carToTarget = carPosition.minus(target);

        double steerCorrectionsRadians = carDirection.correctionAngle(carToTarget);
        boolean steerTooStrong = Math.abs(steerCorrectionsRadians) > Math.PI / 4.0;
        boolean boost = Math.abs(steerCorrectionsRadians) < Math.PI / 8.0;
        double turnSpeed = steerCorrectionsRadians * 5;
        turnSpeed = Math.pow(turnSpeed, 3);

        if (renderer != null) {
            renderer.drawLine3d(Color.LIGHT_GRAY, car.position, new Vector3(target.x, target.y, car.position.z));
        }

        return new ControlsOutput()
                .withBoost(!steerTooStrong && boost && allowBoost)
                .withSteer((float) -turnSpeed)
                .withSlide(steerTooStrong)
                .withSlide(false)
                .withThrottle(1);
    }
}
