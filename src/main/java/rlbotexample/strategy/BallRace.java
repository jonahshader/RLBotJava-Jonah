package rlbotexample.strategy;

import rlbotexample.input.CarData;
import rlbotexample.input.DataPacket;
import rlbotexample.vector.Vector2;

public class BallRace {
    public static CarData whoGetBallFirst(DataPacket input) {
        CarData closestCar = input.car;
        Vector2 ballPosition = input.ball.position.flatten();
        double closestTime = getTimeToTarget(input.car, ballPosition);

        for (CarData carData : input.allCars) {
            if (getTimeToTarget(carData, ballPosition) < closestTime) {
                closestCar = carData;
                closestTime = getTimeToTarget(carData, ballPosition);
            }
        }

        return closestCar;
    }

    public static double getTimeToTarget(CarData car, Vector2 targetPos) {
        Vector2 carPosition = car.position.flatten();
        Vector2 carDirection = car.orientation.noseVector.flatten();
        Vector2 carToTarget = targetPos.minus(carPosition);
        double steerCorrectionRots = carDirection.correctionAngle(carToTarget) / (Math.PI * 2);
        return Math.abs(steerCorrectionRots * 1.0) + (carToTarget.magnitude() / 1400.0);
    }
}
