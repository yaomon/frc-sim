package objects;

import java.awt.*;
import java.awt.image.BufferedImage;
import core.GameObject;
import core.World;
import ui.SimulationPanel;
import ui.SpriteLoader;
import physics.AABB;

/**
 * Represents a bucket/container where cargo can be scored
 */
public class Bucket extends GameObject {
    private double x, y;
    private double innerW, innerH;
    private double wall;
    private BufferedImage sprite;
    private double spriteWidth;
    private double spriteHeight;

    public Bucket(double x, double y, double innerW, double innerH, double wall) {
        this.x = x;
        this.y = y;
        this.wall = wall;

        // Load sprite and adjust bucket size to match
        SpriteLoader.SpriteInfo spriteInfo = SpriteLoader.getSprite("bucket");
        sprite = spriteInfo.image;
        if (sprite != null) {
            spriteWidth = spriteInfo.widthMeters;
            spriteHeight = spriteInfo.heightMeters;
            // Make inner area match sprite dimensions more precisely
            this.innerW = spriteWidth * 0.6; // 60% of sprite width for scoring area
            this.innerH = spriteHeight * 0.4; // 40% of sprite height for scoring area
            this.wall = spriteWidth * 0.1; // 10% of sprite width for walls
        } else {
            // Use provided dimensions if no sprite
            this.innerW = innerW;
            this.innerH = innerH;
        }
    }

    // Get collision bounds for walls
    public AABB getRightWall() {
        return new AABB(x + innerW/2 + wall/2, y, wall, innerH + wall);
    }

    public AABB getBottom() {
        return new AABB(x, y - innerH/2 - wall/2, innerW + wall*2, wall);
    }

    public boolean isInside(double px, double py) {
        return Math.abs(px - x) <= innerW/2 && Math.abs(py - y) <= innerH/2;
    }

    @Override
    public void update(World world, double dt) {
        // Bucket is static, no update needed
    }

    @Override
    public void draw(Graphics2D g) {
        if (sprite != null) {
            // Draw bucket sprite at native resolution
            Point screenPos = SimulationPanel.toScreen(x, y);
            g.drawImage(sprite,
                screenPos.x - (int)(spriteWidth * SimulationPanel.PPM / 2),
                screenPos.y - (int)(spriteHeight * SimulationPanel.PPM / 2),
                (int)(spriteWidth * SimulationPanel.PPM),
                (int)(spriteHeight * SimulationPanel.PPM), null);
        } else {
            // Fallback to drawn graphics
            Color wallColor = new Color(30, 160, 80);
            g.setColor(wallColor);

            // Right wall
            SimulationPanel.drawRectCenter(g, x + innerW/2 + wall/2, y, wall, innerH + wall, true);

            // Bottom
            SimulationPanel.drawRectCenter(g, x, y - innerH/2 - wall/2, innerW + wall*2, wall, true);
        }

        // Draw scoring zone outline
        g.setColor(new Color(255, 215, 0, 30)); // More transparent gold
        SimulationPanel.drawRectCenter(g, x, y, innerW, innerH, true);
        g.setColor(new Color(255, 215, 0, 100)); // Semi-transparent outline
        g.setStroke(new BasicStroke(1));
        SimulationPanel.drawRectCenter(g, x, y, innerW, innerH, false);
    }
}
