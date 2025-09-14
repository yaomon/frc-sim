package objects.robot.subsystems;
import core.GameObject;
import core.World;
public class Motor extends GameObject{
    private double power;
    private double speed;

    public Motor() {
        this.power = 0;
        this.speed = 0;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getPower() {
        return power;
    }

    public void update(World world, double dt) {
        speed = power * dt * 1000; // Arbitrary scaling factor for speed
    }

    public double getSpeed() {
        return speed;
    }

    public void draw(java.awt.Graphics2D g) {
        // Motors are not drawn
    }
}
