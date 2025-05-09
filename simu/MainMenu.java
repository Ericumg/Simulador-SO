import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Simulador de Memoria");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Botón para abrir el simulador FIFO
        JButton fifoButton = new JButton("Simulador FIFO");
        fifoButton.setBounds(50, 50, 140, 40);
        fifoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    MemorySimulation fifoSimulation = new MemorySimulation();
                    fifoSimulation.setVisible(true);
                });
            }
        });

        // Botón para abrir el simulador LRU
        JButton lruButton = new JButton("Simulador LRU");
        lruButton.setBounds(200, 50, 140, 40);
        lruButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    LRUMemorySimulation lruSimulation = new LRUMemorySimulation();
                    lruSimulation.setVisible(true);
                });
            }
        });

        // Agregar botones al marco
        add(fifoButton);
        add(lruButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}