package rlbotexample;

import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.cppinterop.RLBotDll;
import rlbot.flat.GameTickPacket;
import rlbot.flat.QuickChatSelection;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;
import rlbotexample.boost.BoostManager;
import rlbotexample.dropshot.DropshotTile;
import rlbotexample.dropshot.DropshotTileManager;
import rlbotexample.dropshot.DropshotTileState;
import rlbotexample.input.CarData;
import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;
import rlbotexample.strategy.ActionPlanner;
import rlbotexample.vector.Vector2;
import rlbotexample.vector.Vector3;

import java.awt.*;

public class SampleBot implements Bot {

    private ActionPlanner actionPlanner;

    private final int playerIndex;

    public SampleBot(int playerIndex) {
        this.playerIndex = playerIndex;
        actionPlanner = new ActionPlanner(this);
    }

    /**
     * This is where we keep the actual bot logic. This function shows how to chase the ball.
     * Modify it to make your bot smarter!
     */
    private ControlsOutput processInput(DataPacket input) {
//        Vector2 ballPosition = input.ball.position.flatten();
//        CarData myCar = input.car;
//        Vector2 carPosition = myCar.position.flatten();
//        Vector2 carDirection = myCar.orientation.noseVector.flatten();
//
//
//        Vector2 driveTarget = new Vector2(ballPosition.x, ballPosition.y + ((myCar.team == 0) ? -100 : 100));
//        Vector2 targetDistanceVelOffset = input.ball.velocity.flatten();
//
//
////        Vector2 driveTarget = new Vector2(ballPosition.x, ballPosition.y);
//
//        // Subtract the two positions to get a vector pointing from the car to the ball.
//
//        Vector2 carToBall = ballPosition.minus(carPosition);
//
//        driveTarget = driveTarget.plus(targetDistanceVelOffset.scaled(carToBall.magnitude() * 0.0005));
//
//        Vector2 carToBallTarget = driveTarget.minus(carPosition);
////        System.out.println(targetDistanceVelOffset.y);
//
//        // How far does the car need to rotate before it's pointing exactly at the ball?
//        double steerCorrectionRadians = carDirection.correctionAngle(carToBallTarget);
//
////        System.out.println(myCar.team);
//        boolean goLeft = steerCorrectionRadians > 0;
//
//
//        boolean steerTooStrong = Math.abs(steerCorrectionRadians) > 3 * Math.PI / 4;
//
//        // This is optional!
//        drawDebugLines(input, myCar, goLeft, driveTarget);
//
//        // This is also optional!
////        if (input.ball.position.z > 300) {
////            RLBotDll.sendQuickChat(playerIndex, false, QuickChatSelection.Compliments_NiceOne);
////        }
//        double turnSpeed = steerCorrectionRadians * 5;
//        System.out.println(turnSpeed);
//        //temp
////        System.out.println(input.ball.position.y);
//
//        return new ControlsOutput()
//                .withBoost(!steerTooStrong)
//                .withSteer(steerTooStrong ? (float) turnSpeed : (float) -turnSpeed)
//                .withSlide(steerTooStrong)
//                .withJump(myCar.position.z > 50 && myCar.hasWheelContact)
//                .withThrottle(steerTooStrong ? -1 : 1);
        return actionPlanner.run(input);
    }

    /**
     * This is a nice example of using the rendering feature.
     */
    private void drawDebugLines(DataPacket input, CarData myCar, boolean goLeft, Vector2 path) {
        // Here's an example of rendering debug data on the screen.
        Renderer renderer = BotLoopRenderer.forBotLoop(this);

        // Draw a line from the car to the ball
        renderer.drawLine3d(Color.LIGHT_GRAY, myCar.position, new Vector3(path.x, path.y, input.ball.position.z));

        // Draw a line that points out from the nose of the car.
        renderer.drawLine3d(goLeft ? Color.BLUE : Color.RED,
                myCar.position.plus(myCar.orientation.noseVector.scaled(150)),
                myCar.position.plus(myCar.orientation.noseVector.scaled(300)));

        renderer.drawString3d(goLeft ? "left" : "right", Color.WHITE, myCar.position, 2, 2);

        for (DropshotTile tile: DropshotTileManager.getTiles()) {
            if (tile.getState() == DropshotTileState.DAMAGED) {
                renderer.drawCenteredRectangle3d(Color.YELLOW, tile.getLocation(), 4, 4, true);
            } else if (tile.getState() == DropshotTileState.DESTROYED) {
                renderer.drawCenteredRectangle3d(Color.RED, tile.getLocation(), 4, 4, true);
            }
        }

        // Draw a rectangle on the tile that the car is on
        DropshotTile tile = DropshotTileManager.pointToTile(myCar.position.flatten());
        if (tile != null) renderer.drawCenteredRectangle3d(Color.green, tile.getLocation(), 8, 8, false);
    }


    @Override
    public int getIndex() {
        return this.playerIndex;
    }

    /**
     * This is the most important function. It will automatically get called by the framework with fresh data
     * every frame. Respond with appropriate controls!
     */
    @Override
    public ControllerState processInput(GameTickPacket packet) {

        if (packet.playersLength() <= playerIndex || packet.ball() == null || !packet.gameInfo().isRoundActive()) {
            // Just return immediately if something looks wrong with the data. This helps us avoid stack traces.
            return new ControlsOutput();
        }

        // Update the boost manager and tile manager with the latest data
        BoostManager.loadGameTickPacket(packet);
        DropshotTileManager.loadGameTickPacket(packet);

        // Translate the raw packet data (which is in an unpleasant format) into our custom DataPacket class.
        // The DataPacket might not include everything from GameTickPacket, so improve it if you need to!
        DataPacket dataPacket = new DataPacket(packet, playerIndex);

        // Do the actual logic using our dataPacket.
        ControlsOutput controlsOutput = processInput(dataPacket);

        return controlsOutput;
    }

    public void retire() {
        System.out.println("Retiring bot " + playerIndex);
    }
}
