package objects.robot.subsystems;

import core.GameObject;
import objects.robot.Robot;

public class Brakes extends GameObject{
    private double MAX_BRAKE_FORCE = 100;
    private double MIN_BRAKE_FORCE = 0;
    private double brakeForce = 0; // Default brake force
    Robot robot;

    public Brakes(Robot robot) {
        this.robot = robot;
    }

    public void setBrakeForce(double brakeForce) {
        this.brakeForce = Math.max(MIN_BRAKE_FORCE, Math.min(brakeForce, MAX_BRAKE_FORCE));
    }

    @Override
    public void update(core.World world, double dt) {
        robot.addForce(-Math.signum(robot.velocity.x) * brakeForce, 0);
    }

    @Override
    public void draw(java.awt.Graphics2D g) {
        // Brakes are not drawn
    }

}
