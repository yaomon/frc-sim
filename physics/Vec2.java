package physics;

/**
 * Represents a 2D vector with x and y components.
 * Used for positions, velocities, and forces in the simulation.
 */
public class Vec2 {
    // Components
    public double x;
    public double y;

    /**
     * Default constructor - creates zero vector
     */
    public Vec2() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Creates a vector with specified components
     */
    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a copy of this vector
     */
    public Vec2 copy() {
        return new Vec2(x, y);
    }
}
