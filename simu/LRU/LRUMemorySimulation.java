package LRU;
import javax.swing.*;
import MENU.MainMenu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class LRUMemorySimulation extends JFrame {
    private final MainMenu parentMenu; // Menú principal
    private final DefaultListModel<String> physicalMemoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> virtualMemoryModel = new DefaultListModel<>();
    private final Queue<String> virtualMemoryQueue = new LinkedList<>();
    private int physicalMemorySize = 4; // Tamaño inicial de la memoria física
    private int addressCounter = 0; // Contador inicial en decimal
    private final java.util.List<PageLRU> physicalPages = new java.util.ArrayList<>();
    private int accessCounter = 1;

    // Clase interna para representar una página LRU
    static class PageLRU {
        String name;
        String address;
        int lastUsed;
        PageLRU(String name, String address, int lastUsed) {
            this.name = name;
            this.address = address;
            this.lastUsed = lastUsed;
        }
        @Override
        public String toString() {
            return name + " (Dir: " + address + ") [Acceso: " + lastUsed + "]";
        }
    }

    public LRUMemorySimulation(MainMenu parentMenu) {
        this.parentMenu = parentMenu; // Usar el menú principal recibido como parámetro
        setTitle("Simulación de Memoria - LRU");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de memoria física
        JPanel physicalMemoryPanel = new JPanel(new BorderLayout());
        physicalMemoryPanel.setBorder(BorderFactory.createTitledBorder("Memoria Física"));
        JList<String> physicalMemoryList = new JList<>(physicalMemoryModel);
        physicalMemoryPanel.add(new JScrollPane(physicalMemoryList), BorderLayout.CENTER);
        // Doble clic para simular acceso
        physicalMemoryList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = physicalMemoryList.locationToIndex(e.getPoint());
                    if (idx >= 0 && idx < physicalPages.size()) {
                        physicalPages.get(idx).lastUsed = accessCounter++;
                        updatePhysicalModel();
                    }
                }
            }
        });

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
                physicalPages.clear();
                virtualMemoryQueue.clear();
                addressCounter = 0; // Reiniciar el contador de direcciones
                accessCounter = 1; // Reiniciar el contador de accesos
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
                        if (addressCounter >= physicalMemorySize) addressCounter = 0;
                        updatePhysicalModel();
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

    private void updatePhysicalModel() {
        physicalMemoryModel.clear();
        // Resaltar el candidato a reemplazo si la memoria está llena
        int minIdx = -1, minAccess = Integer.MAX_VALUE;
        if (physicalPages.size() >= physicalMemorySize) {
            for (int i = 0; i < physicalPages.size(); i++) {
                if (physicalPages.get(i).lastUsed < minAccess) {
                    minAccess = physicalPages.get(i).lastUsed;
                    minIdx = i;
                }
            }
        }
        for (int i = 0; i < physicalPages.size(); i++) {
            String s = physicalPages.get(i).toString();
            if (i == minIdx) s += "  ← Próximo a reemplazar";
            physicalMemoryModel.addElement(s);
        }
    }

    private void addProcess(String process) {
        // Si ya está en memoria física, actualizar lastUsed
        for (PageLRU p : physicalPages) {
            if (p.name.equals("Proceso: " + process)) {
                p.lastUsed = accessCounter++;
                updatePhysicalModel();
                return;
            }
        }
        // Si está en memoria virtual, eliminarlo de ahí
        for (int i = 0; i < virtualMemoryModel.size(); i++) {
            if (virtualMemoryModel.get(i).contains("Proceso: " + process + " ")) {
                virtualMemoryModel.remove(i);
                break;
            }
        }
        // Si la memoria física está llena, reemplazar el menos usado
        if (physicalPages.size() >= physicalMemorySize) {
            int minIdx = 0, minAccess = physicalPages.get(0).lastUsed;
            for (int i = 1; i < physicalPages.size(); i++) {
                if (physicalPages.get(i).lastUsed < minAccess) {
                    minAccess = physicalPages.get(i).lastUsed;
                    minIdx = i;
                }
            }
            PageLRU removed = physicalPages.remove(minIdx);
            virtualMemoryModel.addElement(removed.toString());
        }
        // Agregar nuevo proceso
        PageLRU newPage = new PageLRU("Proceso: " + process, getNextHexAddress(), accessCounter++);
        physicalPages.add(newPage);
        updatePhysicalModel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            LRUMemorySimulation simulation = new LRUMemorySimulation(menu);
            simulation.setVisible(true);
        });
    }
}