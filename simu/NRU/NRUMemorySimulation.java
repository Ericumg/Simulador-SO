package NRU;
import javax.swing.*;
import MENU.MainMenu;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class NRUMemorySimulation extends JFrame {
    private final MainMenu parentMenu;
    private final DefaultListModel<String> physicalMemoryModel = new DefaultListModel<>();
    private final DefaultListModel<String> virtualMemoryModel = new DefaultListModel<>();
    private final java.util.List<Page> physicalPages = new java.util.ArrayList<>();
    private int physicalMemorySize = 4;
    private int addressCounter = 0;

    static class Page {
        String name;
        String address;
        boolean referenced;
        boolean modified;
        public Page(String name, String address) {
            this.name = name;
            this.address = address;
            this.referenced = false;
            this.modified = false;
        }
        public String toString() {
            return name + " (Dir: " + address + ") [R=" + (referenced ? 1 : 0) + ", M=" + (modified ? 1 : 0) + "]";
        }
    }

    static int getNRUClass(Page p) {
        if (!p.referenced && !p.modified) return 0;
        if (!p.referenced && p.modified) return 1;
        if (p.referenced && !p.modified) return 2;
        return 3;
    }

    public NRUMemorySimulation(MainMenu parentMenu) {
        this.parentMenu = parentMenu;
        setTitle("Simulación de Memoria - NRU");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel physicalMemoryPanel = new JPanel(new BorderLayout());
        physicalMemoryPanel.setBorder(BorderFactory.createTitledBorder("Memoria Física (NRU)"));
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
        JButton refreshButton = new JButton("Actualizar Clases NRU");

        controlPanel.add(new JLabel("Proceso:"));
        controlPanel.add(processField);
        controlPanel.add(addProcessButton);
        controlPanel.add(new JLabel("Tamaño Memoria Física:"));
        controlPanel.add(memorySizeField);
        controlPanel.add(clearButton);
        controlPanel.add(refreshButton);

        JButton backButton = new JButton("Regresar");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentMenu.setVisible(true);
                dispose();
            }
        });

        add(physicalMemoryPanel, BorderLayout.WEST);
        add(virtualMemoryPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
        add(backButton, BorderLayout.NORTH);

        // Barra de progreso para el ciclo de limpieza de R
        JProgressBar cleanCycleBar = new JProgressBar(0, 100);
        cleanCycleBar.setValue(100);
        cleanCycleBar.setStringPainted(true);
        cleanCycleBar.setString("Ciclo de limpieza de R");
        add(cleanCycleBar, BorderLayout.CENTER);

        // Campo para definir el tiempo del ciclo de limpieza
        JPanel timerPanel = new JPanel();
        timerPanel.add(new JLabel("MS ciclo limpieza R:"));
        JTextField msField = new JTextField("2000", 6); // valor por defecto 2000 ms
        timerPanel.add(msField);
        JButton setMsButton = new JButton("Aplicar");
        timerPanel.add(setMsButton);
        add(timerPanel, BorderLayout.NORTH);

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
                physicalPages.clear();
                addressCounter = 0;
            }
        });

        memorySizeField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int newSize = Integer.parseInt(memorySizeField.getText().trim());
                    if (newSize > 0) {
                        while (physicalPages.size() > newSize) {
                            Page removed = physicalPages.remove(physicalPages.size() - 1);
                            virtualMemoryModel.addElement(removed.toString());
                        }
                        physicalMemorySize = newSize;
                        JOptionPane.showMessageDialog(NRUMemorySimulation.this, "Tamaño de memoria física configurado a: " + physicalMemorySize, "Configuración", JOptionPane.INFORMATION_MESSAGE);
                        updatePhysicalModel();
                    } else {
                        JOptionPane.showMessageDialog(NRUMemorySimulation.this, "El tamaño debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(NRUMemorySimulation.this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        physicalMemoryList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = physicalMemoryList.locationToIndex(e.getPoint());
                    if (index >= 0 && index < physicalPages.size()) {
                        Page page = physicalPages.get(index);
                        String[] options = {"Cambiar Referenciado (R)", "Cambiar Modificado (M)", "Cancelar"};
                        int choice = JOptionPane.showOptionDialog(NRUMemorySimulation.this,
                                "¿Qué bit desea cambiar de la página?\n" + page,
                                "Modificar bits NRU",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null, options, options[0]);
                        if (choice == 0) page.referenced = !page.referenced;
                        if (choice == 1) page.modified = !page.modified;
                        updatePhysicalModel();
                    }
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNRUClasses();
            }
        });

        updatePhysicalModel();
        // Temporizador visual para el ciclo de limpieza de R
        final int[] msPerCycle = {2000};
        final int[] progress = {100};
        Timer cleanTimer = new Timer(msPerCycle[0] / 20, null); // 20 pasos
        cleanTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                progress[0] -= 5;
                if (progress[0] <= 0) {
                    for (Page p : physicalPages) {
                        p.referenced = false;
                    }
                    updatePhysicalModel();
                    progress[0] = 100;
                }
                cleanCycleBar.setValue(progress[0]);
            }
        });
        cleanTimer.start();

        setMsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int ms = Integer.parseInt(msField.getText().trim());
                    if (ms < 200) ms = 200; // mínimo 200 ms para evitar errores
                    msPerCycle[0] = ms;
                    cleanTimer.setDelay(ms / 20);
                    progress[0] = 100;
                    cleanCycleBar.setValue(100);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(NRUMemorySimulation.this, "Ingrese un número válido de milisegundos.", "Error", JOptionPane.ERROR_MESSAGE);
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
        // Si ya está en memoria física, poner R=1 automáticamente y preguntar solo por M
        for (Page p : physicalPages) {
            if (p.name.equals("Proceso: " + process)) {
                p.referenced = true; // ¡Siempre actualizar R a 1!
                int modificado = JOptionPane.showConfirmDialog(this, "¿El proceso fue modificado? (Bit M=1)", "Bit Modificado (M)", JOptionPane.YES_NO_OPTION);
                if (modificado == JOptionPane.YES_OPTION) {
                    p.modified = true;
                }
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
        // Si la memoria física está llena, reemplazar usando NRU
        if (physicalPages.size() >= physicalMemorySize) {
            int victim = selectNRUVictim();
            if (victim != -1) {
                Page removed = physicalPages.remove(victim);
                virtualMemoryModel.addElement(removed.toString());
            }
        }
        // Preguntar al usuario si la página fue modificada (M=1)
        int modificado = JOptionPane.showConfirmDialog(this, "¿El proceso fue modificado? (Bit M=1)", "Bit Modificado (M)", JOptionPane.YES_NO_OPTION);
        boolean isModified = (modificado == JOptionPane.YES_OPTION);
        // Al agregar, R=1 (acceso), M según usuario
        Page newPage = new Page("Proceso: " + process, getNextHexAddress());
        newPage.referenced = true;
        newPage.modified = isModified;
        physicalPages.add(newPage);
        updatePhysicalModel();
    }

    private void updatePhysicalModel() {
        physicalMemoryModel.clear();
        for (Page p : physicalPages) {
            int nruClass = getNRUClass(p);
            String classLabel = "Clase " + nruClass + (nruClass == 0 ? " (mejor)" : (nruClass == 3 ? " (peor)" : ""));
            physicalMemoryModel.addElement(p.toString() + "  ← " + classLabel);
        }
    }

    private int selectNRUVictim() {
        java.util.List<Integer> class0 = new java.util.ArrayList<>();
        java.util.List<Integer> class1 = new java.util.ArrayList<>();
        java.util.List<Integer> class2 = new java.util.ArrayList<>();
        java.util.List<Integer> class3 = new java.util.ArrayList<>();
        for (int i = 0; i < physicalPages.size(); i++) {
            Page p = physicalPages.get(i);
            if (!p.referenced && !p.modified) class0.add(i);
            else if (!p.referenced && p.modified) class1.add(i);
            else if (p.referenced && !p.modified) class2.add(i);
            else class3.add(i);
        }
        Random rand = new Random();
        if (!class0.isEmpty()) return class0.get(rand.nextInt(class0.size()));
        if (!class1.isEmpty()) return class1.get(rand.nextInt(class1.size()));
        if (!class2.isEmpty()) return class2.get(rand.nextInt(class2.size()));
        if (!class3.isEmpty()) return class3.get(rand.nextInt(class3.size()));
        return -1;
    }

    private void showNRUClasses() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b>Clases NRU:</b><br>");
        for (int c = 0; c < 4; c++) {
            sb.append("Clase " + c + ": ");
            for (Page p : physicalPages) {
                boolean inClass = (c == 0 && !p.referenced && !p.modified) ||
                                  (c == 1 && !p.referenced && p.modified) ||
                                  (c == 2 && p.referenced && !p.modified) ||
                                  (c == 3 && p.referenced && p.modified);
                if (inClass) sb.append(p.name + " (R=" + (p.referenced ? 1 : 0) + ", M=" + (p.modified ? 1 : 0) + ")  ");
            }
            sb.append("<br>");
        }
        sb.append("</html>");
        JOptionPane.showMessageDialog(this, sb.toString(), "Estado de Clases NRU", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MENU.MainMenu menu = new MENU.MainMenu();
            NRUMemorySimulation simulation = new NRUMemorySimulation(menu);
            simulation.setVisible(true);
        });
    }
}
