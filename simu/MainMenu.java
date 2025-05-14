import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Simulador de Memoria");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel central con BoxLayout vertical
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Título de bienvenida
        JLabel titleLabel = new JLabel("¡Bienvenido al Simulador de Memoria!", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        centerPanel.add(titleLabel);

        // Botón para abrir el simulador FIFO
        JButton fifoButton = new JButton("Simulador FIFO");
        fifoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fifoButton.setMaximumSize(new Dimension(200, 40));
        fifoButton.addActionListener(e -> {
            MemorySimulation fifoSimulation = new MemorySimulation(this);
            fifoSimulation.setVisible(true);
            setVisible(false);
        });
        centerPanel.add(fifoButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Espacio entre botones

        // Botón para abrir el simulador LRU
        JButton lruButton = new JButton("Simulador LRU");
        lruButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        lruButton.setMaximumSize(new Dimension(200, 40));
        lruButton.addActionListener(e -> {
            LRUMemorySimulation lruSimulation = new LRUMemorySimulation(this);
            lruSimulation.setVisible(true);
            setVisible(false);
        });
        centerPanel.add(lruButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}