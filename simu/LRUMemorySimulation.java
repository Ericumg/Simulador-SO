import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Queue;

public class LRUMemorySimulation extends JFrame {
    private final MainMenu parentMenu; // Menú principal
    private final DefaultListModel<String> physicalMemoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> virtualMemoryModel = new DefaultListModel<>();
    private final LinkedHashMap<String, String> physicalMemoryMap = new LinkedHashMap<>(16, 0.75f, true); // LRU cache
    private final Queue<String> virtualMemoryQueue = new LinkedList<>();
    private int physicalMemorySize = 4; // Tamaño inicial de la memoria física
    private int addressCounter = 0; // Contador inicial en decimal

    public LRUMemorySimulation(MainMenu parentMenu) {
        this.parentMenu = new MainMenu(); // Inicializar el menú principal
        setTitle("Simulación de Memoria - LRU");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de memoria física
        JPanel physicalMemoryPanel = new JPanel(new BorderLayout());
        physicalMemoryPanel.setBorder(BorderFactory.createTitledBorder("Memoria Física"));
        JList<String> physicalMemoryList = new JList<>(physicalMemoryModel);
        physicalMemoryPanel.add(new JScrollPane(physicalMemoryList), BorderLayout.CENTER);

        // Panel de memoria virtual
        JPanel virtualMemoryPanel = new JPanel(new BorderLayout());
        virtualMemoryPanel.setBorder(BorderFactory.createTitledBorder("Memoria Virtual"));
        JList<String> virtualMemoryList = new JList<>(virtualMemoryModel);
        virtualMemoryPanel.add(new JScrollPane(virtualMemoryList), BorderLayout.CENTER);

        // Panel de controles
        JPanel controlPanel = new JPanel();
        JTextField processField = new JTextField(10);
        JButton addProcessButton = new JButton("Agregar Proceso");
        JButton clearButton = new JButton("Limpiar");
        JTextField memorySizeField = new JTextField(String.valueOf(physicalMemorySize), 5);

        controlPanel.add(new JLabel("Proceso:"));
        controlPanel.add(processField);
        controlPanel.add(addProcessButton);
        controlPanel.add(new JLabel("Tamaño Memoria Física:"));
        controlPanel.add(memorySizeField);
        controlPanel.add(clearButton);

        // Botón de regresar
        JButton backButton = new JButton("Regresar");
        backButton.addActionListener(e -> {
            parentMenu.setVisible(true); // Mostrar el menú principal
            dispose(); // Cerrar la ventana actual
        });

        add(physicalMemoryPanel, BorderLayout.WEST);
        add(virtualMemoryPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
        add(backButton, BorderLayout.NORTH);

        // Acción para agregar un proceso
        addProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String process = processField.getText().trim();
                if (!process.isEmpty()) {
                    addProcess(process);
                    processField.setText("");
                } else {
                    JOptionPane.showMessageDialog(LRUMemorySimulation.this, "Ingrese un nombre de proceso.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción para limpiar las memorias
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                physicalMemoryModel.clear();
                virtualMemoryModel.clear();
                physicalMemoryMap.clear();
                virtualMemoryQueue.clear();
                addressCounter = 0; // Reiniciar el contador de direcciones
            }
        });

        // Acción para cambiar el tamaño de la memoria física
        memorySizeField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int newSize = Integer.parseInt(memorySizeField.getText().trim());
                    if (newSize > 0) {
                        physicalMemorySize = newSize;
                        JOptionPane.showMessageDialog(LRUMemorySimulation.this, "Tamaño de memoria física configurado a: " + physicalMemorySize, "Configuración", JOptionPane.INFORMATION_MESSAGE);
                        // Limpiar las memorias al cambiar el tamaño
                        physicalMemoryModel.clear();
                        virtualMemoryModel.clear();
                        physicalMemoryMap.clear();
                        virtualMemoryQueue.clear();
                        addressCounter = 0; // Reiniciar el contador de direcciones
                    } else {
                        JOptionPane.showMessageDialog(LRUMemorySimulation.this, "El tamaño debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LRUMemorySimulation.this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Agregar paneles al marco
        add(physicalMemoryPanel, BorderLayout.WEST);
        add(virtualMemoryPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private String getNextHexAddress() {
        String hexAddress = Integer.toHexString(addressCounter).toUpperCase(); // Convertir a hexadecimal y en mayúsculas
        addressCounter = (addressCounter + 1) % 16; // Incrementar y reiniciar después de F (16 en decimal)
        return hexAddress;
    }

    private void addProcess(String process) {
        // Asignar una nueva dirección hexadecimal al proceso
        String processWithAddress = "Proceso: " + process + " (Dir: " + getNextHexAddress() + ")";
    
        // Si el proceso ya está en la memoria física, actualizar su dirección en el mismo lugar
        if (physicalMemoryMap.containsKey(process)) {
            String oldProcessWithAddress = physicalMemoryMap.get(process); // Obtener la dirección anterior
            int indexToUpdate = physicalMemoryModel.indexOf(oldProcessWithAddress); // Obtener el índice del proceso
            physicalMemoryModel.set(indexToUpdate, processWithAddress); // Actualizar en el mismo índice
            physicalMemoryMap.put(process, processWithAddress); // Actualizar en el mapa
            return;
        }
    
        // Si el proceso está en la memoria virtual, eliminarlo de allí
        if (virtualMemoryQueue.contains(process)) {
            virtualMemoryQueue.remove(process);
            virtualMemoryModel.removeElement("Proceso: " + process);
        }
    
        // Si la memoria física está llena, mover el proceso más antiguo a la memoria virtual
        if (physicalMemoryMap.size() >= physicalMemorySize) {
            String leastUsedProcess = physicalMemoryMap.keySet().iterator().next(); // Obtener el menos usado (más antiguo)
            String removedProcess = physicalMemoryMap.remove(leastUsedProcess);
            int indexToReplace = physicalMemoryModel.indexOf(removedProcess); // Obtener el índice del proceso más antiguo
    
            // Mover el proceso más antiguo a la memoria virtual
            virtualMemoryQueue.add(leastUsedProcess);
            virtualMemoryModel.addElement(removedProcess);
    
            // Reemplazar el proceso más antiguo con el nuevo proceso
            physicalMemoryModel.set(indexToReplace, processWithAddress); // Reemplazar en el mismo índice
        } else {
            // Si la memoria física no está llena, agregar el nuevo proceso
            physicalMemoryModel.addElement(processWithAddress);
        }
    
        // Agregar el nuevo proceso al mapa de memoria física
        physicalMemoryMap.put(process, processWithAddress);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            LRUMemorySimulation simulation = new LRUMemorySimulation(menu);
            simulation.setVisible(true);
        });
    }
}