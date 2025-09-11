package core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles keyboard input for the simulation.
 */
public class Input implements KeyListener {
    // Control states
    public volatile boolean left;
    public volatile boolean right;
    public volatile boolean brake;
    public volatile boolean liftUp;
    public volatile boolean liftDown;
    public volatile boolean tiltBack;
    public volatile boolean tiltFwd;
    public volatile boolean reset;
    public volatile boolean pause;

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        setKey(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setKey(e, false);
    }

    /**
     * Updates key states based on keyboard events
     */
    private void setKey(KeyEvent e, boolean down) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_A:
                left = down;
                break;
            case KeyEvent.VK_D:
                right = down;
                break;
            case KeyEvent.VK_SPACE:
                brake = down;
                break;
            case KeyEvent.VK_W:
                liftUp = down;
                break;
            case KeyEvent.VK_S:
                liftDown = down;
                break;
            case KeyEvent.VK_Q:
                tiltBack = down;
                break;
            case KeyEvent.VK_E:
                tiltFwd = down;
                break;
            case KeyEvent.VK_R:
                if (down) {
                    reset = true;
                }
                break;
            case KeyEvent.VK_P:
                if (down) {
                    pause = !pause;
                }
                break;
        }
    }
}
