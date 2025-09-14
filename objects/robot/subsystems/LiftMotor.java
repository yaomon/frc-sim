package objects.robot.subsystems;

import objects.robot.Robot;

public class LiftMotor extends Motor {
    Robot robot;
    public LiftMotor(Robot robot) {
        super();
        this.robot = robot;
    }

    public void update(core.World world, double dt) {
        super.update(world, dt);
        robot.lift += getSpeed() * dt;
    }
}
