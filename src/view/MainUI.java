package view;

import model.*;
import utils.MetricsTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

public class MainUI extends JFrame {
    private JComboBox<String> estructuraSelector;
    private JTextArea metricasArea;
    private JPanel canvasPanel;
    private KDTreePanel kdPanel;
    private QuadTreePanel quadPanel;
    private GridFilePanel gridPanel;

    private KDTree kdTree;
    private QuadTree quadTree;
    private GridFile gridFile;
    private MetricsTracker metrics;

    private JTextField xField, yField;
    private JTextField xMinField, xMaxField, yMinField, yMaxField;

    public MainUI() {
        super("Explorador Espacial");
        setSize(1200, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        metrics = new MetricsTracker();
        kdTree = new KDTree(metrics);
        quadTree = new QuadTree(metrics);
        gridFile = new GridFile(10, metrics);

        kdPanel = new KDTreePanel(kdTree);
        quadPanel = new QuadTreePanel(quadTree);
        gridPanel = new GridFilePanel(gridFile);

        canvasPanel = new JPanel(new CardLayout());
        canvasPanel.add(kdPanel, "KD");
        canvasPanel.add(quadPanel, "QUAD");
        canvasPanel.add(gridPanel, "GRID");

        estructuraSelector = new JComboBox<>(new String[]{"KD-Tree", "QuadTree", "Grid File"});
        estructuraSelector.addActionListener(e -> switchPanel());

        JButton insertarBtn = new JButton("Insertar aleatorios");
        insertarBtn.addActionListener(e -> insertarPuntos());

        JButton consultaPuntualBtn = new JButton("Consulta puntual");
        consultaPuntualBtn.addActionListener(e -> consultaPuntual());

        JButton consultaRangoBtn = new JButton("Consulta por rango");
        consultaRangoBtn.addActionListener(e -> consultaRango());

        JButton vecinoBtn = new JButton("Vecino más cercano");
        vecinoBtn.addActionListener(e -> vecinoMasCercano());

        xField = new JTextField(3);
        yField = new JTextField(3);
        xMinField = new JTextField(3);
        xMaxField = new JTextField(3);
        yMinField = new JTextField(3);
        yMaxField = new JTextField(3);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Estructura:"));
        inputPanel.add(estructuraSelector);
        inputPanel.add(insertarBtn);
        inputPanel.add(new JLabel("x:"));
        inputPanel.add(xField);
        inputPanel.add(new JLabel("y:"));
        inputPanel.add(yField);
        inputPanel.add(consultaPuntualBtn);
        inputPanel.add(vecinoBtn);

        inputPanel.add(new JLabel("xMin:"));
        inputPanel.add(xMinField);
        inputPanel.add(new JLabel("xMax:"));
        inputPanel.add(xMaxField);
        inputPanel.add(new JLabel("yMin:"));
        inputPanel.add(yMinField);
        inputPanel.add(new JLabel("yMax:"));
        inputPanel.add(yMaxField);
        inputPanel.add(consultaRangoBtn);

        metricasArea = new JTextArea(6, 70);
        metricasArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(metricasArea);

        add(inputPanel, BorderLayout.NORTH);
        add(canvasPanel, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void switchPanel() {
        CardLayout cl = (CardLayout) canvasPanel.getLayout();
        String selection = (String) estructuraSelector.getSelectedItem();
        if (selection.equals("KD-Tree")) cl.show(canvasPanel, "KD");
        else if (selection.equals("QuadTree")) cl.show(canvasPanel, "QUAD");
        else cl.show(canvasPanel, "GRID");
    }

    private void insertarPuntos() {
        Random rand = new Random();
        metrics.resetDiskAccesses();
        metrics.startTimer();
        for (int i = 0; i < 20; i++) {
            int x = rand.nextInt(100);
            int y = rand.nextInt(100);
            if (estructuraSelector.getSelectedItem().equals("KD-Tree")) {
                kdTree.insert(new int[]{x, y});
            } else if (estructuraSelector.getSelectedItem().equals("QuadTree")) {
                quadTree.insert(new int[]{x, y});
            } else {
                gridFile.insert(new int[]{x, y});
            }
        }
        metrics.endTimer();
        repaint();
        mostrarMetricas("Inserción");
    }

    private void consultaPuntual() {
        try {
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());
            boolean existe;

            metrics.resetDiskAccesses();
            metrics.startTimer();
            if (estructuraSelector.getSelectedItem().equals("KD-Tree")) {
                existe = kdTree.search(new int[]{x, y});
            } else if (estructuraSelector.getSelectedItem().equals("QuadTree")) {
                existe = quadTree.containsPoint(new int[]{x, y});
            } else {
                existe = gridFile.containsPoint(new int[]{x, y});
            }
            metrics.endTimer();
            repaint();
            mostrarMetricas("Consulta puntual", existe ? 1 : 0);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese coordenadas x e y válidas");
        }
    }

    private void consultaRango() {
        try {
            int xMin = Integer.parseInt(xMinField.getText());
            int xMax = Integer.parseInt(xMaxField.getText());
            int yMin = Integer.parseInt(yMinField.getText());
            int yMax = Integer.parseInt(yMaxField.getText());
            List<int[]> encontrados;

            metrics.resetDiskAccesses();
            metrics.startTimer();
            if (estructuraSelector.getSelectedItem().equals("KD-Tree")) {
                encontrados = kdTree.rangeSearch(xMin, xMax, yMin, yMax);
            } else if (estructuraSelector.getSelectedItem().equals("QuadTree")) {
                encontrados = quadTree.rangeQuery(xMin, xMax, yMin, yMax);
            } else {
                encontrados = gridFile.rangeQuery(xMin, xMax, yMin, yMax);
            }
            metrics.endTimer();
            repaint();
            mostrarMetricas("Consulta por rango", encontrados.size());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese todos los valores de rango como números enteros válidos.");
        }
    }

    private void vecinoMasCercano() {
        try {
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());
            int[] target = {x, y};
            int[] vecino;

            metrics.resetDiskAccesses();
            metrics.startTimer();
            if (estructuraSelector.getSelectedItem().equals("KD-Tree")) {
                vecino = kdTree.nearestNeighbor(target);
            } else if (estructuraSelector.getSelectedItem().equals("QuadTree")) {
                vecino = quadTree.nearestNeighbor(target);
            } else {
                vecino = gridFile.nearestNeighbor(target);
            }
            /*if (estructuraSelector.getSelectedItem().equals("KD-Tree")) {
                vecino = kdTree.nearestNeighbor(target);
            } else {
                vecino = gridFile.nearestNeighbor(target);
            }*/
            metrics.endTimer();
            repaint();
            mostrarMetricas("Vecino más cercano", 1, vecino);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese coordenadas x e y válidas para vecino.");
        }
    }

    private void mostrarMetricas(String operacion) {
        mostrarMetricas(operacion, -1);
    }

    private void mostrarMetricas(String operacion, int resultados) {
        mostrarMetricas(operacion, resultados, null);
    }

    private void mostrarMetricas(String operacion, int resultados, int[] punto) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ").append(operacion).append(" ---\n");
        sb.append("Tiempo (ns): ").append(metrics.getElapsedTime()).append("\n");
        sb.append("Memoria usada (bytes): ").append(metrics.getUsedMemory()).append("\n");
        sb.append("Accesos simulados a disco: ").append(metrics.getDiskAccesses()).append("\n");
        if (resultados >= 0) sb.append("\n" + "Resultados encontrados: ").append(resultados).append("\n");
        if (punto != null) sb.append("\n" + "Vecino: (").append(punto[0]).append(", ").append(punto[1]).append(")\n");

        metricasArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}
