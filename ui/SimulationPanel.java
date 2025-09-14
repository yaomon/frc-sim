package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.File;
import java.util.List;
import core.*;
import objects.*;
import physics.*;
import java.awt.image.BufferedImage;
/**
 * Main simulation panel that handles rendering and game loop
 */
public class SimulationPanel extends JPanel implements Runnable {
    private BufferedImage background;

    // Pixels per meter for rendering
    public static final int PPM = 50; // Matches SpriteLoader.DEFAULT_PIXELS_PER_METER for 1:1 mapping

    // Input handler
    public static Input input = new Input();

    // Panel dimensions
    private final int widthPx;
    private final int heightPx;

    // Game loop
    private Thread loop;
    private volatile boolean running = false;
    private World world = new World();
    private double accumulator = 0;

    // Game state
    private int score = 0;
    private double timeSec = 0;

    public SimulationPanel(int widthPx, int heightPx) {
        this.widthPx = widthPx;
        this.heightPx = heightPx;
        setPreferredSize(new Dimension(widthPx, heightPx));
        setFocusable(true);
        addKeyListener(input);
        world.reset();

        try {
            // Load background image from resources
            background = ImageIO.read(new File("assets/sprites/bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        running = true;
        loop = new Thread(this, "sim-loop");
        loop.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final double dt = 1.0/120.0; // Physics timestep (s)

        while (running) {
            long now = System.nanoTime();
            double frameTime = (now - lastTime) / 1e9;
            lastTime = now;

            if (!input.pause) {
                accumulator += Math.min(frameTime, 0.05);
            }

            while (accumulator >= dt) {
                update(dt);
                accumulator -= dt;
            }

            repaint();

            try {
                Thread.sleep(1000/120);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    private void update(double dt) {
        timeSec += dt;

        if (input.reset) {
            world.reset();
            score = 0;
            timeSec = 0;
            input.reset = false;
        }

        // Update all objects
        for (GameObject obj : world.objects) {
            obj.update(world, dt);
        }

        // Handle cargo-ground collisions
        for (Cargo cargo : world.cargos) {
            if (cargo.position.y - cargo.bounds.h <= world.groundY + 0.01) {
                cargo.position.y = world.groundY + cargo.bounds.h;
                if (cargo.velocity.y < 0) {
                    cargo.velocity.y = 0;
                }
            }
        }

        score = 0;
        for (Cargo cargo : world.cargos) {
            if (world.bucket.isInside(cargo.position.x, cargo.position.y)) {
                score++;
            }
        }
        score = Math.min(score, world.cargos.size());

    }

    @Override
    protected void paintComponent(Graphics gRaw) {
        super.paintComponent(gRaw);
        Graphics2D g = (Graphics2D) gRaw;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw background image scaled to panel size
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Draw sky background
            g.setColor(new Color(235, 244, 255));
            g.fillRect(0, 0, widthPx, heightPx);
        }

        // Draw ground
        drawRectCenter(g, (world.leftX + world.rightX)/2, world.groundY/2, widthPx / (double)PPM, world.groundY, true);

        // Draw all objects
        for (GameObject obj : world.objects) {
            obj.draw(g);
        }

        // Draw HUD
        drawHUD(g);
    }

    private void drawHUD(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(10, 10, 330, 118, 12, 12);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        g.drawString("FRC Forklift 2D Simulator", 24, 34);

        g.setFont(g.getFont().deriveFont(Font.PLAIN, 13f));
        g.drawString("A/D: drive    W/S: lift    Q/E: tilt    SPACE: brake", 24, 56);
        g.drawString("R: reset    P: pause", 24, 74);
        g.drawString(String.format("Score (in bucket): %d / %d", score, world.cargos.size()), 24, 96);

        String status = String.format("t=%.1fs  pos=(%.1f, %.1f)  v=(%.1f, %.1f)",
            timeSec, world.robot.position.x, world.robot.position.y,
            world.robot.velocity.x, world.robot.velocity.y);
        g.drawString(status, 24, 114);
    }

    /**
     * Convert world coordinates (meters) to screen coordinates (pixels)
     */
    public static Point toScreen(double xMeters, double yMeters) {
        int x = (int)Math.round(xMeters * PPM + 50); // Add 50px margin on left
        int y = (int)Math.round(700 - (yMeters * PPM + 50)); // Add 50px margin on bottom
        return new Point(x, y);
    }

    /**
     * Convert a rectangle from world to screen coordinates
     */
    public static Rect toScreenRect(double cx, double cy, double w, double h) {
        Point p = toScreen(cx, cy);
        int W = (int)Math.round(w * PPM);
        int H = (int)Math.round(h * PPM);
        return new Rect(p.x - W/2, p.y - H/2, W, H);
    }
    /**
     * Draw a rectangle centered at (cx, cy) with width w and height h in world coordinates
     * @param g Graphics2D context
     * @param cx Center x in meters
     * @param cy Center y in meters
     * @param w Width in meters
     * @param h Height in meters
     * @param fill True to fill, false to stroke
     */
    public static void drawRectCenter(Graphics2D g, double cx, double cy, double w, double h, boolean fill) {
        Point screenPos = toScreen(cx, cy);
        int screenW = (int)(w * PPM);
        int screenH = (int)(h * PPM);
        int screenX = screenPos.x - screenW/2;
        int screenY = screenPos.y - screenH/2;

        if (fill) {
            g.fillRect(screenX, screenY, screenW, screenH);
        } else {
            g.drawRect(screenX, screenY, screenW, screenH);
        }
    }
}
