package objects.robot.subsystems;
import core.GameObject;

public class FrontDistSensor extends GameObject {
    private double distance;

    public FrontDistSensor() {
        this.distance = 0;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void update(core.World world, double dt) {
        // Sensors might not need to update anything each frame
    }

    public void draw(java.awt.Graphics2D g) {
        // Sensors are not drawn
    }
}
