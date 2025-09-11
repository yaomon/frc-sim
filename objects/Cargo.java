package objects;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import physics.*;
import core.*;
import ui.SimulationPanel;
import ui.SpriteLoader;

/**
 * Represents a cargo box that can be picked up by the robot
 */
public class Cargo extends Body {
    private Color color;
    private BufferedImage sprite;
    private double spriteWidth;
    private double spriteHeight;

    public Cargo(double x, double y, double size) {
        super(x, y, 0.3, 0.3, 8.0, false); // Fixed size to match sprite (0.6m wide)
        // Generate random pastel color
        float hue = (float)Math.random();
        color = Color.getHSBColor(hue, 0.55f, 0.95f);

        // Load sprite and adjust physics size to match
        SpriteLoader.SpriteInfo spriteInfo = SpriteLoader.getSprite("cargo");
        sprite = spriteInfo.image;
        if (sprite != null) {
            spriteWidth = spriteInfo.widthMeters;
            spriteHeight = spriteInfo.heightMeters;
            // Update collision bounds to match sprite
            bounds.w = spriteWidth / 2;
            bounds.h = spriteHeight / 2;
        } else {
            // Use default size if no sprite
            spriteWidth = size * 2;
            spriteHeight = size * 2;
            bounds.w = size;
            bounds.h = size;
        }
    }

    @Override
    protected void resolveGroundAndWalls(World world) {
        super.resolveGroundAndWalls(world);
        // Check bucket collisions
        if (world.bucket != null) {
            // Check right wall
            AABB rightWall = world.bucket.getRightWall();
            boolean collidingWithRightWall =
                position.x - bounds.w < rightWall.x + rightWall.w/2 &&
                position.x > rightWall.x - rightWall.w/2 &&
                position.y + bounds.h > rightWall.y - rightWall.h/2 &&
                position.y - bounds.h < rightWall.y + rightWall.h/2;

            // Store previous state before collision response
            double prevX = position.x;

            if (collidingWithRightWall) {
                // Move to just touching the wall
                position.x = rightWall.x - rightWall.w/2 - bounds.w;

                if (velocity.x > 0) {
                    velocity.x *= -0.1; // More damping on bounces
                }

                // If this would put us through another object, restore position
                if (position.x < prevX) {
                    position.x = prevX;
                    velocity.x = 0;
                }
            }

            // Check bottom
            AABB bottom = world.bucket.getBottom();
            boolean collidingWithBottom =
                position.x + bounds.w > bottom.x - bottom.w/2 &&
                position.x - bounds.w < bottom.x + bottom.w/2 &&
                position.y - bounds.h < bottom.y + bottom.h/2 &&
                position.y + bounds.h > bottom.y - bottom.h/2; // Added vertical overlap check

            double prevY = position.y;

            if (collidingWithBottom) {
                // Move to just above the bottom
                position.y = bottom.y + bottom.h/2 + bounds.h;

                if (velocity.y < 0) {
                    velocity.y = 0;
                    // Apply more friction when sitting in bucket to prevent sliding through
                    velocity.x *= 0.90; // Increased friction
                }

                // If this would put us through another object, restore position
                if (position.y < prevY) {
                    position.y = prevY;
                    velocity.y = 0;
                }
            }

            // Additional check to prevent escape through corners
            if (collidingWithBottom && collidingWithRightWall) {
                velocity.x *= 0.5; // Extra damping at corners
                velocity.y = 0;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Point screenPos = SimulationPanel.toScreen(position.x, position.y);
        int size = (int)(bounds.w * 2 * SimulationPanel.PPM);

        // Save original transform
        AffineTransform oldTransform = g.getTransform();

        // Rotate slightly based on velocity for visual flair
        double angle = Math.atan2(velocity.y, velocity.x) * 0.2;
        g.rotate(angle, screenPos.x, screenPos.y);

        if (sprite != null) {
            // Draw sprite at native resolution
            g.drawImage(sprite,
                screenPos.x - (int)(spriteWidth * SimulationPanel.PPM / 2),
                screenPos.y - (int)(spriteHeight * SimulationPanel.PPM / 2),
                (int)(spriteWidth * SimulationPanel.PPM),
                (int)(spriteHeight * SimulationPanel.PPM), null);
        } else {
            // Fallback to drawn graphics
            // Draw main box with gradient
            GradientPaint gradient = new GradientPaint(
                screenPos.x - size/2, screenPos.y - size/2, color,
                screenPos.x + size/2, screenPos.y + size/2,
                new Color(Math.max(0, color.getRed()-40),
                         Math.max(0, color.getGreen()-40),
                         Math.max(0, color.getBlue()-40))
            );
            g.setPaint(gradient);
            drawRectCenter(g, position.x, position.y, bounds.w * 2, bounds.h * 2, true);

            // Draw metallic-looking edges
            g.setColor(new Color(220, 220, 220, 140));
            g.setStroke(new BasicStroke(2));
            drawEdgeHighlight(g, position.x, position.y, bounds.w * 2, bounds.h * 2);

            // Draw outline
            g.setColor(new Color(0, 0, 0, 140));
            g.setStroke(new BasicStroke(1));
            drawRectCenter(g, position.x, position.y, bounds.w * 2, bounds.h * 2, false);
        }

        // DEBUG: Draw actual collision bounds
        g.setColor(new Color(255, 0, 255, 100));
        g.setStroke(new BasicStroke(1));
        drawRectCenter(g, bounds.x, bounds.y, bounds.w * 2, bounds.h * 2, false);

        // Restore original transform
        g.setTransform(oldTransform);
    }

    private void drawEdgeHighlight(Graphics2D g, double cx, double cy, double w, double h) {
        Point screenPos = SimulationPanel.toScreen(cx, cy);
        int screenW = (int)(w * SimulationPanel.PPM);
        int screenH = (int)(h * SimulationPanel.PPM);
        int screenX = screenPos.x - screenW/2;
        int screenY = screenPos.y - screenH/2;

        // Draw just the top and left edges for a metallic highlight
        g.drawLine(screenX, screenY, screenX + screenW, screenY);
        g.drawLine(screenX, screenY, screenX, screenY + screenH);
    }

    private void drawRectCenter(Graphics2D g, double cx, double cy, double w, double h, boolean fill) {
        Point screenPos = SimulationPanel.toScreen(cx, cy);
        int screenW = (int)(w * SimulationPanel.PPM);
        int screenH = (int)(h * SimulationPanel.PPM);
        int screenX = screenPos.x - screenW/2;
        int screenY = screenPos.y - screenH/2;

        if (fill) {
            g.fillRect(screenX, screenY, screenW, screenH);
        } else {
            g.drawRect(screenX, screenY, screenW, screenH);
        }
    }
}
