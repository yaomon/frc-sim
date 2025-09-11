package core;

import java.util.ArrayList;
import java.util.List;
import objects.Robot;
import objects.Cargo;
import objects.Bucket;

/**
 * Represents the simulation world and contains all simulation objects and parameters.
 */
public class World {
    // Physics parameters
    public double gravity = -9.81;        // m/s^2 (real-world gravity)
    public double linearDamping = 0.60;   // Simple drag coefficient (higher = more friction)
    public double groundFriction = 0.80;  // Additional friction when touching ground

    // World boundaries
    public double groundY = 2;           // Ground level in meters
    public double leftX = 2;             // Left wall position
    public double rightX = 22;           // Right wall position

    // Game objects
    public final List<GameObject> objects = new ArrayList<>();
    public Robot robot;
    public Bucket bucket;
    public final List<Cargo> cargos = new ArrayList<>();

    /**
     * Reset the world to its initial state
     */
    public void reset() {
        // Clear all objects
        objects.clear();
        cargos.clear();

        // Create robot
        robot = new Robot(6, 4);

        // Create scoring bucket
        bucket = new Bucket(16, 4, 4, 3, 0.5); // x, y, innerW, innerH, wallThickness

        // Add main objects
        objects.add(bucket);
        objects.add(robot);

        // Create cargo boxes - start them in a neat stack away from the robot
        for (int i = 0; i < 6; i++) {
            // Stack 3x2: place boxes in two columns of three
            double x = 10 + 2 * i;
            double y = 3 + (i % 3) * 0.7;        // Stack three high
            Cargo c = new Cargo(x, y, 0.3);
            cargos.add(c);
            objects.add(c);
        }
    }
}
