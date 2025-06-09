package view;

import model.QuadTree;
import utils.MetricsTracker;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MainQuadUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            MetricsTracker metrics = new MetricsTracker();
            QuadTree tree = new QuadTree(metrics);

            metrics.resetDiskAccesses();
            metrics.startTimer();
            // Insertar puntos aleatorios
            Random rand = new Random();
            for (int i = 0; i < 20; i++) {
                int x = rand.nextInt(100);
                int y = rand.nextInt(100);
                tree.insert(new int[]{x, y});
            }
            metrics.endTimer();

            // COMPARACION DE METRICAS
            System.out.println("-- MÉTRICAS DE INSERCIÓN --");
            System.out.println("Tiempo (ns): " + metrics.getElapsedTime());
            System.out.println("Memoria (bytes): " + metrics.getUsedMemory());
            System.out.println("Accesos simulados a disco: " + metrics.getDiskAccesses() + "\n");


            // Consulta PUNTUAL
            int[] primerPunto = new int[]{rand.nextInt(100), rand.nextInt(100)};
            metrics.resetDiskAccesses();
            metrics.startTimer();
            tree.insert(primerPunto);
            System.out.println("Insertado: (" + primerPunto[0] + ", " + primerPunto[1] + ")"+ "\n");
            metrics.endTimer();

            // COMPARACION DE METRICAS
            System.out.println("-- CONSULTA PUNTUAL --");
            System.out.println("Consulta puntual para ese punto: " + tree.containsPoint(primerPunto)); // debe ser true
            System.out.println("Tiempo (ns): " + metrics.getElapsedTime());
            System.out.println("Accesos simulados a disco: " + metrics.getDiskAccesses() + "\n");


            // Consulta por RANGO RECTANGULAR
            System.out.println("\nConsulta por rango: (20 ≤ x ≤ 70, 20 ≤ y ≤ 70)");
            metrics.resetDiskAccesses();
            metrics.startTimer();
            var puntos = tree.rangeQuery(20, 70, 20, 70);
            for (int[] p : puntos) {
                System.out.println("(" + p[0] + ", " + p[1] + ")");
            }
            metrics.endTimer();

            // COMPARACION DE METRICAS
            System.out.println("\n" + "-- CONSULTA RANGO RECTANGULAR --");
            System.out.println("Total encontrados: " + puntos.size());
            System.out.println("Tiempo (ns): " + metrics.getElapsedTime());
            System.out.println("Accesos simulados a disco: " + metrics.getDiskAccesses() + "\n");



            JFrame frame = new JFrame("Visualización QuadTree");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            QuadTreePanel panel = new QuadTreePanel(tree);
            frame.add(panel, BorderLayout.CENTER);

            frame.pack();
            frame.setVisible(true);
        });
    }
}
