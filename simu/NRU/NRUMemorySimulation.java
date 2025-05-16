package NRU;
import javax.swing.*;
import MENU.MainMenu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

public class NRUMemorySimulation extends JFrame {
    private final MainMenu parentMenu;
    private final DefaultListModel<String> physicalMemoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> virtualMemoryModel = new DefaultListModel<>();
    private final Queue<String> physicalMemoryQueue = new LinkedList<>();
    private int physicalMemorySize = 4;
    private int addressCounter = 0;

    public NRUMemorySimulation(MainMenu parentMenu) {
        this.parentMenu = parentMenu;
        setTitle("Simulación de Memoria - NRU");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel physicalMemoryPanel = new JPanel(new BorderLayout());
        physicalMemoryPanel.setBorder(BorderFactory.createTitledBorder("Memoria Física"));
        JList<String> physicalMemoryList = new JList<>(physicalMemoryModel);
        physicalMemoryPanel.add(new JScrollPane(physicalMemoryList), BorderLayout.CENTER);

        JPanel virtualMemoryPanel = new JPanel(new BorderLayout());
        virtualMemoryPanel.setBorder(BorderFactory.createTitledBorder("Memoria Virtual"));
        JList<String> virtualMemoryList = new JList<>(virtualMemoryModel);
        virtualMemoryPanel.add(new JScrollPane(virtualMemoryList), BorderLayout.CENTER);

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
            parentMenu.setVisible(true);
            dispose();
        });

        add(physicalMemoryPanel, BorderLayout.WEST);
        add(virtualMemoryPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
        add(backButton, BorderLayout.NORTH);

        addProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String process = processField.getText().trim();
                if (!process.isEmpty()) {
                    addProcess(process);
                    processField.setText("");
                } else {
                    JOptionPane.showMessageDialog(NRUMemorySimulation.this, "Ingrese un nombre de proceso.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                physicalMemoryModel.clear();
                virtualMemoryModel.clear();
                physicalMemoryQueue.clear();
                addressCounter = 0;
            }
        });

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
                        JOptionPane.showMessageDialog(NRUMemorySimulation.this, "Tamaño de memoria física configurado a: " + physicalMemorySize, "Configuración", JOptionPane.INFORMATION_MESSAGE);
                        if (addressCounter >= physicalMemorySize) addressCounter = 0;
                    } else {
                        JOptionPane.showMessageDialog(NRUMemorySimulation.this, "El tamaño debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(NRUMemorySimulation.this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private String getNextHexAddress() {
        String hexAddress = Integer.toHexString(addressCounter).toUpperCase();
        addressCounter = (addressCounter + 1) % 16;
        return hexAddress;
    }

    private void addProcess(String process) {
        String processWithAddress = "Proceso: " + process + " (Dir: " + getNextHexAddress() + ")";
        // Si ya está en memoria física, no hacer nada
        if (physicalMemoryQueue.stream().anyMatch(p -> p.contains("Proceso: " + process + " "))) {
            return;
        }
        // Si está en memoria virtual, eliminarlo de ahí
        for (int i = 0; i < virtualMemoryModel.size(); i++) {
            if (virtualMemoryModel.get(i).contains("Proceso: " + process + " ")) {
                virtualMemoryModel.remove(i);
                break;
            }
        }
        // Si la memoria física está llena, reemplazar usando NRU (aquí, simplemente el primero)
        if (physicalMemoryQueue.size() >= physicalMemorySize) {
            String removedProcess = physicalMemoryQueue.poll();
            int indexToReplace = physicalMemoryModel.indexOf(removedProcess);
            virtualMemoryModel.addElement(removedProcess);
            physicalMemoryModel.set(indexToReplace, processWithAddress);
            physicalMemoryQueue.add(processWithAddress);
        } else {
            physicalMemoryModel.addElement(processWithAddress);
            physicalMemoryQueue.add(processWithAddress);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            NRUMemorySimulation simulation = new NRUMemorySimulation(menu);
            simulation.setVisible(true);
        });
    }
}
