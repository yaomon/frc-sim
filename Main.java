import javax.swing.*;
import ui.SimulationPanel;

public class Main {
    public static void main(String[] args) {
        // Create UI on EDT
        SwingUtilities.invokeLater(() -> {
            // Create window
            JFrame frame = new JFrame("FRC Forklift Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create and add simulation panel
            SimulationPanel panel = new SimulationPanel(1200, 700);
            frame.add(panel);

            // Pack and show
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Start simulation loop
            panel.start();
        });
    }
}
