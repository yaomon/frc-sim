package core;

import java.awt.Graphics2D;

/**
 * Base class for all objects in the simulation.
 * Provides update and draw methods that must be implemented by subclasses.
 */
public abstract class GameObject {
    /**
     * Update the object's state
     * @param world The world containing this object
     * @param dt Time step in seconds
     */
    public abstract void update(World world, double dt);

    /**
     * Draw the object
     * @param g Graphics context to draw with
     */
    public abstract void draw(Graphics2D g);
}
