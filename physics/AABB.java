package physics;

/**
 * Represents an Axis-Aligned Bounding Box for collision detection.
 * The box is defined by its center position and half-extents (half width/height).
 */
public class AABB {
    // Center position
    public double x;
    public double y;

    // Half-extents (half width and height)
    public double w;  // half width
    public double h;  // half height

    /**
     * Creates an AABB with specified center and dimensions
     * @param x Center X position
     * @param y Center Y position
     * @param w Half width
     * @param h Half height
     */
    public AABB(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Checks if this AABB overlaps with another AABB
     * @param other The other AABB to check against
     * @return true if the boxes overlap
     */
    public boolean overlaps(AABB other) {
        return x < other.x + other.w &&
            x + w > other.x &&
            y < other.y + other.h &&
            y + h > other.y;
    }

}
