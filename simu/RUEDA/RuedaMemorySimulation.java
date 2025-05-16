package RUEDA;
import javax.swing.*;
import MENU.MainMenu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;

public class RuedaMemorySimulation extends JFrame {
    private final MainMenu parentMenu;
    private final DefaultListModel<String> virtualMemoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> physicalMemoryModel = new DefaultListModel<>();
    private final Queue<String> physicalMemoryQueue = new LinkedList<>();
    private int physicalMemorySize = 4;
    private int addressCounter = 0;
    private int pointer = 0; // Puntero para la RUEDA
    private Runnable updateClock;

    // Clase interna para representar una página con bit R
    static class Page {
        String name;
        String address;
        boolean referenced;
        Page(String name, String address) {
            this.name = name;
            this.address = address;
            this.referenced = true; // Al agregar, R=1
        }
        @Override
        public String toString() {
            return name + " (Dir: " + address + ") [R=" + (referenced ? 1 : 0) + "]";
        }
    }
    private final java.util.List<Page> physicalPages = new java.util.ArrayList<>();

    public RuedaMemorySimulation(MainMenu parentMenu) {
        this.parentMenu = parentMenu;
        setTitle("Simulación de Memoria - Rueda");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de memoria física (actualizado para usar Page)
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
            parentMenu.setVisible(true);
            dispose();
        });

        add(physicalMemoryPanel, BorderLayout.WEST);
        add(virtualMemoryPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
        add(backButton, BorderLayout.NORTH);

        // Panel para el reloj visual del ciclo de RUEDA
        class ClockPanel extends JPanel {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                int size = Math.min(w, h) - 10; // Más grande
                int cx = w / 2, cy = h / 2, r = size / 2;
                g.setColor(Color.LIGHT_GRAY);
                g.fillOval(cx - r, cy - r, size, size);
                g.setColor(Color.BLACK);
                g.drawOval(cx - r, cy - r, size, size);
                // Dibujar sectores y páginas
                int n = physicalPages.size();
                for (int i = 0; i < n; i++) {
                    double angle = 2 * Math.PI * i / (n == 0 ? 1 : n);
                    int px = (int) (cx + (r - 40) * Math.cos(angle - Math.PI / 2));
                    int py = (int) (cy + (r - 40) * Math.sin(angle - Math.PI / 2));
                    Page page = physicalPages.get(i);
                    g.setColor(i == pointer ? Color.RED : (page.referenced ? new Color(0, 128, 0) : Color.BLUE));
                    g.fillOval(px - 25, py - 25, 50, 50);
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 13));
                    g.drawString(page.name, px - 20, py - 2);
                    g.setColor(Color.YELLOW);
                    g.setFont(new Font("Arial", Font.PLAIN, 12));
                    g.drawString("R=" + (page.referenced ? 1 : 0), px - 10, py + 18);
                }
                // Flecha del puntero
                if (n > 0) {
                    double angle = 2 * Math.PI * pointer / n;
                    int fx = (int) (cx + (r - 5) * Math.cos(angle - Math.PI / 2));
                    int fy = (int) (cy + (r - 5) * Math.sin(angle - Math.PI / 2));
                    g.setColor(Color.RED);
                    g.drawLine(cx, cy, fx, fy);
                }
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("RUEDA", cx - 30, cy);
            }
        }
        ClockPanel clockPanel = new ClockPanel();
        clockPanel.setPreferredSize(new Dimension(320, 320)); // Más grande
        add(clockPanel, BorderLayout.CENTER);

        updateClock = clockPanel::repaint;

        addProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String process = processField.getText().trim();
                if (!process.isEmpty()) {
                    addProcess(process);
                    processField.setText("");
                } else {
                    JOptionPane.showMessageDialog(RuedaMemorySimulation.this, "Ingrese un nombre de proceso.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                physicalMemoryModel.clear();
                virtualMemoryModel.clear();
                physicalPages.clear();
                addressCounter = 0;
                pointer = 0;
                updateClock.run();
            }
        });

        memorySizeField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int newSize = Integer.parseInt(memorySizeField.getText().trim());
                    if (newSize > 0) {
                        // Si el tamaño se reduce, mover procesos sobrantes a memoria virtual
                        while (physicalPages.size() > newSize) {
                            Page removed = physicalPages.remove(physicalPages.size() - 1);
                            virtualMemoryModel.addElement(removed.toString());
                        }
                        physicalMemorySize = newSize;
                        JOptionPane.showMessageDialog(RuedaMemorySimulation.this, "Tamaño de memoria física configurado a: " + physicalMemorySize, "Configuración", JOptionPane.INFORMATION_MESSAGE);
                        // Si el puntero queda fuera de rango, reiniciarlo
                        if (pointer >= physicalMemorySize) pointer = 0;
                        updateClock.run();
                    } else {
                        JOptionPane.showMessageDialog(RuedaMemorySimulation.this, "El tamaño debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(RuedaMemorySimulation.this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Doble clic en la lista para simular acceso (R=1)
        physicalMemoryList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = physicalMemoryList.locationToIndex(e.getPoint());
                    if (idx >= 0 && idx < physicalPages.size()) {
                        physicalPages.get(idx).referenced = true;
                        updatePhysicalModel();
                        updateClock.run();
                    }
                }
            }
        });
    }

    private String getNextHexAddress() {
        String hexAddress = Integer.toHexString(addressCounter).toUpperCase();
        addressCounter = (addressCounter + 1) % 16;
        return hexAddress;
    }

    private void updatePhysicalModel() {
        physicalMemoryModel.clear();
        for (Page p : physicalPages) {
            physicalMemoryModel.addElement(p.toString());
        }
    }

    private void addProcess(String process) {
        // Si ya está en memoria física, simular acceso y poner R=1
        for (Page p : physicalPages) {
            if (p.name.equals("Proceso: " + process)) {
                p.referenced = true;
                updatePhysicalModel();
                updateClock.run();
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
        // Si la memoria física está llena, aplicar RUEDA real
        if (physicalPages.size() >= physicalMemorySize) {
            while (true) {
                Page candidate = physicalPages.get(pointer);
                if (!candidate.referenced) {
                    // Reemplazar
                    virtualMemoryModel.addElement(candidate.toString());
                    Page newPage = new Page("Proceso: " + process, getNextHexAddress());
                    physicalPages.set(pointer, newPage);
                    updatePhysicalModel();
                    updateClock.run();
                    pointer = (pointer + 1) % physicalMemorySize;
                    break;
                } else {
                    // Segunda oportunidad
                    candidate.referenced = false;
                    updatePhysicalModel();
                    updateClock.run();
                    pointer = (pointer + 1) % physicalMemorySize;
                }
            }
        } else {
            Page newPage = new Page("Proceso: " + process, getNextHexAddress());
            physicalPages.add(newPage);
            updatePhysicalModel();
            updateClock.run();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            RuedaMemorySimulation simulation = new RuedaMemorySimulation(menu);
            simulation.setVisible(true);
        });
    }
}
