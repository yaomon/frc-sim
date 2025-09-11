package physics;

import java.awt.Graphics2D;
import core.GameObject;
import core.World;
import objects.Cargo;

/**
 * Represents a physical body in the simulation with position, velocity, and forces.
 */
public class Body extends GameObject {
    // State
    public Vec2 position;     // Position in meters
    public Vec2 velocity;     // Velocity in m/s
    public Vec2 force;        // Accumulated force in Newtons

    // Properties
    public double mass;       // Mass in kg
    public boolean isStatic;  // If true, object doesn't move
    public AABB bounds;       // Collision bounds

    /**
     * Creates a new physical body
     */
    public Body(double x, double y, double halfW, double halfH, double mass, boolean isStatic) {
        this.position = new Vec2(x, y);
        this.velocity = new Vec2();
        this.force = new Vec2();
        this.mass = mass;
        this.isStatic = isStatic;
        this.bounds = new AABB(x, y, halfW, halfH);
    }

    /**
     * Adds a force to be applied next physics update
     */
    public void addForce(double fx, double fy) {
        force.x += fx;
        force.y += fy;
    }

    @Override
    public void update(World world, double dt) {
        if (isStatic) {
            // Static objects only update their bounds position
            bounds.x = position.x;
            bounds.y = position.y;
            return;
        }

        // Apply gravity
        addForce(0, mass * world.gravity);

        // Semi-implicit Euler integration
        velocity.x += (force.x / mass) * dt;
        velocity.y += (force.y / mass) * dt;

        // Apply damping for stability
        double damping = Math.pow(world.linearDamping, dt);
        velocity.x *= damping;
        velocity.y *= damping;

        // Apply extra ground friction
        if (position.y - bounds.h <= world.groundY + 0.01) {
            velocity.x *= Math.pow(world.groundFriction, dt);
        }

        // Update position
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        // Resolve collisions with ground and walls
        resolveGroundAndWalls(world);

        // Clear forces for next frame
        force.x = 0;
        force.y = 0;

        // Update collision bounds
        bounds.x = position.x;
        bounds.y = position.y;
    }

    /**
     * Handle collisions with the ground and world boundaries
     */
    protected void resolveGroundAndWalls(World world) {
        // Ground collision
        if (position.y - bounds.h < world.groundY) {
            position.y = world.groundY + bounds.h;
            if (velocity.y < 0) {
                velocity.y = 0;  // Stop vertical motion
            }
        }

        // Wall collisions
        if (position.x - bounds.w < world.leftX) {
            position.x = world.leftX + bounds.w;
            velocity.x = 0;
        }

        if (position.x + bounds.w > world.rightX) {
            position.x = world.rightX - bounds.w;
            velocity.x = 0;
        }

        // Bucket collision if we're a cargo object
        if (this instanceof Cargo && world.robot != null) {
            Vec2 bucketPos = world.robot.getForkTipWorld();
            AABB bucketBounds = world.robot.getForkPickupZone();

            // Check if cargo overlaps with bucket
            if (position.x + bounds.w > bucketPos.x - bucketBounds.w &&
                position.x - bounds.w < bucketPos.x + bucketBounds.w &&
                position.y + bounds.h > bucketPos.y - bucketBounds.h &&
                position.y - bounds.h < bucketPos.y + bucketBounds.h) {

                // Resolve collision by pushing cargo up and out
                position.y = bucketPos.y + bucketBounds.h + bounds.h;
                if (velocity.y < 0) {
                    velocity.y = 0;
                }

                // Add some horizontal velocity to simulate scooping
                if (Math.abs(world.robot.getVelocity().x) > 0.1) {
                    velocity.x = world.robot.getVelocity().x * 1.1;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // Base body class doesn't draw anything
        // Subclasses should implement their own drawing
    }
}
