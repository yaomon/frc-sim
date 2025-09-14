package objects.robot.subsystems;

import core.World;
import objects.robot.Robot;

public class DriveMotor extends Motor {
    Robot robot;
    public DriveMotor(Robot robot) {
        super();
        this.robot = robot;
    }

    public void update(World world, double dt) {
        super.update(world, dt);
        // Apply drive force to robot based on motor speed
        double forceMagnitude = getSpeed();
        robot.addForce(forceMagnitude, 0); // Apply force in the x direction
    }
}
