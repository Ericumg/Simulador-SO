package FIFO;
import javax.swing.*;
import MENU.MainMenu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

public class MemorySimulation extends JFrame {
    private final MainMenu parentMenu; // Menú principal
    private final DefaultListModel<String> physicalMemoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> virtualMemoryModel = new DefaultListModel<>();
    private final Queue<String> physicalMemoryQueue = new LinkedList<>();
    private int physicalMemorySize = 4; // Tamaño inicial de la memoria física
    private int addressCounter = 0; // Contador inicial en decimal

    public MemorySimulation(MainMenu parentMenu) {
        this.parentMenu = parentMenu; // Inicializar el menú principal
        setTitle("Simulación de Memoria - FIFO");
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

        JButton backButton = new JButton("Regresar");
        backButton.addActionListener(e -> {
            parentMenu.setVisible(true); // Mostrar el menú principal
            dispose(); // Cerrar la ventana actual
        });

        // Agregar paneles al marco
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
                    JOptionPane.showMessageDialog(MemorySimulation.this, "Ingrese un nombre de proceso.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción para limpiar las memorias
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                physicalMemoryModel.clear();
                virtualMemoryModel.clear();
                physicalMemoryQueue.clear();
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
                        // Si el tamaño se reduce, mover procesos sobrantes a memoria virtual
                        while (physicalMemoryQueue.size() > newSize) {
                            String removed = ((LinkedList<String>) physicalMemoryQueue).removeLast();
                            physicalMemoryModel.removeElement(removed);
                            virtualMemoryModel.addElement(removed);
                        }
                        physicalMemorySize = newSize;
                        JOptionPane.showMessageDialog(MemorySimulation.this, "Tamaño de memoria física configurado a: " + physicalMemorySize, "Configuración", JOptionPane.INFORMATION_MESSAGE);
                        if (addressCounter >= physicalMemorySize) addressCounter = 0;
                    } else {
                        JOptionPane.showMessageDialog(MemorySimulation.this, "El tamaño debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MemorySimulation.this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
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
        // Asignar dirección hexadecimal al proceso
        String processWithAddress = "Proceso: " + process + " (Dir: " + getNextHexAddress() + ")";

        // Verificar si el proceso ya está en la memoria física
        if (physicalMemoryQueue.stream().anyMatch(p -> p.contains("Proceso: " + process + " "))) {
            return;
        }

        // Si el proceso está en la memoria virtual, eliminarlo de allí
        for (int i = 0; i < virtualMemoryModel.size(); i++) {
            if (virtualMemoryModel.get(i).contains("Proceso: " + process + " ")) {
                virtualMemoryModel.remove(i);
                break;
            }
        }

        // Si la memoria física está llena, reemplazar el proceso más antiguo
        if (physicalMemoryQueue.size() >= physicalMemorySize) {
            String removedProcess = physicalMemoryQueue.poll(); // Obtener y eliminar el proceso más antiguo
            int indexToReplace = physicalMemoryModel.indexOf(removedProcess); // Obtener el índice del proceso más antiguo

            // Mover el proceso más antiguo a la memoria virtual
            virtualMemoryModel.addElement(removedProcess);

            // Reemplazar el proceso más antiguo con el nuevo proceso
            physicalMemoryModel.set(indexToReplace, processWithAddress); // Reemplazar en el mismo índice
        } else {
            // Si la memoria física no está llena, agregar el nuevo proceso
            physicalMemoryModel.addElement(processWithAddress);
        }

        // Agregar el nuevo proceso a la cola de memoria física
        physicalMemoryQueue.add(processWithAddress);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            MemorySimulation simulation = new MemorySimulation(menu);
            simulation.setVisible(true);
        });
    }
}