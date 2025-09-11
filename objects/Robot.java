package objects;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import physics.*;
import core.*;
import ui.SimulationPanel;
import ui.SpriteLoader;

public class Robot extends Body {
    // Robot dimensions and graphics
    private double maxLiftHeight = 14.0;  // Default maximum lift travel
    private double forkLength = 3.0;     // Default fork length

    private BufferedImage robotSprite;
    private BufferedImage mastSprite;
    private BufferedImage forksSprite;
    private double baseWidth = 12.0;    // Default base width
    private double baseHeight = 4.0;    // Default base height
    private double mastWidth = 1.4;     // Default mast width
    private double mastHeight = 14.0;   // Default mast height
    private double forksWidth = 6.0;    // Default forks width
    private double forksHeight = 3.0;   // Default forks height

    // Actuated state
    private double lift = 0.0;        // 0 to mastHeight
    private double tiltDeg = 5.0;     // -20 to +30 (back is +)

    // Performance characteristics
    private final double driveForce = 400;   // Newtons (reduced for slower movement)
    private final double liftRate = 2.0;     // m/s (reduced for better control)
    private final double tiltRate = 20.0;    // deg/s (reduced for better control)

    // Cargo handling
    private Cargo carrying = null;
    private Vec2 cargoOffset = new Vec2();

    public Robot(double x, double y) {
        // Start with default size, will update with sprite sizes
        super(x, y, 6, 2, 60, false);

        // Load sprites and adjust dimensions
        SpriteLoader.SpriteInfo baseInfo = SpriteLoader.getSprite("robot_base");
        if (baseInfo.image != null) {
            robotSprite = baseInfo.image;
            baseWidth = baseInfo.widthMeters;
            baseHeight = baseInfo.heightMeters;
            bounds.w = baseWidth / 2;
            bounds.h = baseHeight / 2;
        }

        SpriteLoader.SpriteInfo mastInfo = SpriteLoader.getSprite("robot_mast");
        if (mastInfo.image != null) {
            mastSprite = mastInfo.image;
            mastWidth = mastInfo.widthMeters;
            mastHeight = mastInfo.heightMeters;
            maxLiftHeight = mastHeight;
        }

        SpriteLoader.SpriteInfo forksInfo = SpriteLoader.getSprite("robot_forks");
        if (forksInfo.image != null) {
            forksSprite = forksInfo.image;
            forksWidth = forksInfo.widthMeters;
            forksHeight = forksInfo.heightMeters;
            forkLength = forksWidth;
        }
    }

    @Override
    public void update(World world, double dt) {
        // Drive commands
        applyDrive(world, dt);

        // Physics update
        super.update(world, dt);

        // Mechanism updates
        applyLift(world, dt);
        applyTilt(world, dt);
    }

    public void applyDrive(World world, double dt) {
        if (SimulationPanel.input.left) {
            addForce(-driveForce, 0);
        }
        if (SimulationPanel.input.right) {
            addForce(driveForce, 0);
        }
        if (SimulationPanel.input.brake) {
            velocity.x *= 0.3; // Stronger braking
            if (Math.abs(velocity.x) < 0.2) { // Stop at lower speed
                velocity.x = 0;
            }
        }
    }

    public void applyLift(World world, double dt) {
        double cmd = 0;
        if (SimulationPanel.input.liftUp) cmd += 1;
        if (SimulationPanel.input.liftDown) cmd -= 1;

        lift += cmd * liftRate * dt;
        lift = clamp(lift, -0.35, mastHeight);
    }

    public void applyTilt(World world, double dt) {
        double cmd = 0;
        if (SimulationPanel.input.tiltBack) cmd += 1;
        if (SimulationPanel.input.tiltFwd) cmd -= 1;

        tiltDeg += cmd * tiltRate * dt;
        tiltDeg = clamp(tiltDeg, -15, 35);
    }

    private Vec2 getForkBaseWorld() {
        double frontX = position.x + bounds.w - 0.1; // Moved back to align with mast
        double baseY = position.y + bounds.h;
        return new Vec2(frontX, baseY + lift);
    }

    public Vec2 getForkTipWorld() {
        Vec2 base = getForkBaseWorld();
        double rad = Math.toRadians(tiltDeg);
        double dx = Math.cos(rad) * forkLength;
        double dy = Math.sin(rad) * forkLength;
        return new Vec2(base.x + dx, base.y + dy);
    }

    public AABB getForkPickupZone() {
        Vec2 tip = getForkTipWorld();
        // Zone starts from 70% of the way to the tip to ensure cargo is well-supported
        double zoneStart = tip.x - forkLength * 0.3;
        double zoneY = tip.y;
        return new AABB(zoneStart, zoneY, 0.2, 0.05); // Smaller zone for more precise pickup
    }

    @Override
    protected void resolveGroundAndWalls(World world) {
        super.resolveGroundAndWalls(world);
    }

    public Vec2 getVelocity() {
        return this.velocity;
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    @Override
    public void draw(Graphics2D g) {
        // Save original transform for each sprite
        AffineTransform oldTransform = g.getTransform();

        if (robotSprite != null && mastSprite != null && forksSprite != null) {
            // Draw base
            // Draw robot base at native resolution
            Point basePos = SimulationPanel.toScreen(position.x, position.y);
            g.drawImage(robotSprite,
                basePos.x - (int)(baseWidth * SimulationPanel.PPM / 2),
                basePos.y - (int)(baseHeight * SimulationPanel.PPM / 2),
                (int)(baseWidth * SimulationPanel.PPM),
                (int)(baseHeight * SimulationPanel.PPM), null);

            // Draw mast at native resolution
            Point mastPos = SimulationPanel.toScreen(position.x + bounds.w - mastWidth/2, position.y + bounds.h + mastHeight/2);
            g.drawImage(mastSprite,
                mastPos.x - (int)(mastWidth * SimulationPanel.PPM / 2),
                mastPos.y - (int)(mastHeight * SimulationPanel.PPM / 2),
                (int)(mastWidth * SimulationPanel.PPM),
                (int)(mastHeight * SimulationPanel.PPM), null);

            // Draw forks with rotation
            Vec2 base = getForkBaseWorld();
            Point forkPos = SimulationPanel.toScreen(base.x, base.y);
            double rad = Math.toRadians(-tiltDeg); // Negative for correct rotation direction

            // Setup fork rotation
            g.rotate(rad, forkPos.x, forkPos.y);

            // Draw forks at native resolution
            int forkW = (int)(forksWidth * SimulationPanel.PPM);
            int forkH = (int)(forksHeight * SimulationPanel.PPM);
            g.drawImage(forksSprite,
                forkPos.x,
                forkPos.y - forkH/2,
                forkW, forkH, null);

            // Reset transform
            g.setTransform(oldTransform);

            // DEBUG: Draw actual collision bounds
            g.setColor(new Color(255, 255, 0, 100));
            g.setStroke(new BasicStroke(1));
            drawRectCenter(g, position.x, position.y, bounds.w * 2, bounds.h * 2, false);

            // DEBUG: Draw fork length
            Vec2 forkBase = getForkBaseWorld();
            Vec2 tip = getForkTipWorld();
            g.setColor(new Color(0, 255, 0, 100));
            drawLine(g, forkBase.x, forkBase.y, tip.x, tip.y);

        } else {
            // Fallback to drawn graphics
            // Draw chassis
            g.setColor(new Color(40, 120, 200));
            drawRectCenter(g, position.x, position.y, bounds.w * 2, bounds.h * 2, true);
            g.setColor(Color.BLACK);
            drawRectCenter(g, position.x, position.y, bounds.w * 2, bounds.h * 2, false);

            // Draw mast
            Vec2 base = getForkBaseWorld();
            g.setColor(new Color(60, 60, 60));
            drawRectCenter(g, base.x - 0.7, position.y + bounds.h + mastHeight/2, 1.4, mastHeight, true);

            // Draw forks
            double rad = Math.toRadians(tiltDeg);
            double dx = Math.cos(rad);
            double dy = Math.sin(rad);

            g.setStroke(new BasicStroke(3));
            g.setColor(new Color(90, 60, 20));

            drawLine(g, base.x, base.y + 0.3,
                       base.x + dx * forkLength,
                       base.y + 0.3 + dy * forkLength);
        }

        // Draw pickup zone - only show when forks are level enough
        if (tiltDeg >= -2 && tiltDeg <= 5) {
            AABB zone = getForkPickupZone();
            g.setColor(new Color(255, 0, 0, 20)); // Very transparent fill
            drawRectCenter(g, zone.x, zone.y, zone.w * 2, zone.h * 2, true);
            g.setColor(new Color(255, 0, 0, 60)); // Subtle outline
            drawRectCenter(g, zone.x, zone.y, zone.w * 2, zone.h * 2, false);

            // Show a small target line above the forks
            g.setColor(new Color(255, 0, 0, 40));
            Vec2 tip = getForkTipWorld();
            Point p1 = SimulationPanel.toScreen(tip.x - forkLength * 0.3, tip.y + 0.3);
            Point p2 = SimulationPanel.toScreen(tip.x, tip.y + 0.3);
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
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

    private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2) {
        Point p1 = SimulationPanel.toScreen(x1, y1);
        Point p2 = SimulationPanel.toScreen(x2, y2);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
}
