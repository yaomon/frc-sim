package objects.robot.subsystems;

import objects.robot.Robot;

public class TiltMotor extends Motor {
    Robot robot;
    public TiltMotor(Robot robot) {
        super();
        this.robot = robot;
    }

    public void update(core.World world, double dt) {
        super.update(world, dt);
        robot.tiltDeg += getSpeed() * dt;
    }
}
