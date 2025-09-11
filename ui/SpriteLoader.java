package ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpriteLoader {
    private static final Map<String, BufferedImage> sprites = new HashMap<>();
    private static final String SPRITE_PATH = "assets/sprites/";
    private static final int DEFAULT_PIXELS_PER_METER = 50; // Good balance between physical size and visual detail

    public static class SpriteInfo {
        public final BufferedImage image;
        public final double widthMeters;
        public final double heightMeters;

        public SpriteInfo(BufferedImage image) {
            this.image = image;
            if (image != null) {
                this.widthMeters = image.getWidth() / (double)DEFAULT_PIXELS_PER_METER;
                this.heightMeters = image.getHeight() / (double)DEFAULT_PIXELS_PER_METER;
            } else {
                this.widthMeters = 0;
                this.heightMeters = 0;
            }
        }
    }

    public static SpriteInfo getSprite(String name) {
        if (!sprites.containsKey(name)) {
            loadSprite(name);
        }
        return new SpriteInfo(sprites.get(name));
    }

    private static void loadSprite(String name) {
        try {
            File file = new File(SPRITE_PATH + name + ".png");
            String absPath = file.getAbsolutePath();
            System.out.println("Looking for sprite: " + absPath);
            if (!file.exists()) {
                System.out.println("Sprite not found at: " + absPath);
                System.out.println("Using fallback graphics for: " + name);
                sprites.put(name, null);
                return;
            }

            try {
                BufferedImage img = ImageIO.read(file);
                if (img == null) {
                    System.out.println("Failed to load sprite image at: " + absPath);
                    System.out.println("Using fallback graphics for: " + name);
                    sprites.put(name, null);
                    return;
                }

                sprites.put(name, img);
                System.out.println("Successfully loaded sprite: " + name);
                System.out.println("Dimensions: " + img.getWidth() + "x" + img.getHeight() + " pixels");
                System.out.println("In-game size: " +
                    String.format("%.1f", img.getWidth()/(double)DEFAULT_PIXELS_PER_METER) + "x" +
                    String.format("%.1f", img.getHeight()/(double)DEFAULT_PIXELS_PER_METER) + " meters");
            } catch (IOException e) {
                System.out.println("Error reading sprite file: " + absPath);
                e.printStackTrace();
                System.out.println("Using fallback graphics for: " + name);
                sprites.put(name, null);
            }
        } catch (Exception e) {
            System.err.println("Unexpected error loading sprite: " + name);
            e.printStackTrace();
            sprites.put(name, null);
        }
    }

    public static void preloadSprites() {
        // Create sprites directory if it doesn't exist
        new File(SPRITE_PATH).mkdirs();

        // Preload common sprites
        String[] commonSprites = {
            "robot_base", "robot_mast", "robot_forks",
            "cargo", "bucket"
        };

        for (String sprite : commonSprites) {
            getSprite(sprite);
        }
    }
}
