package view;

import model.GridFile;
import utils.MetricsTracker;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MainGridUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MetricsTracker metrics = new MetricsTracker();
            GridFile grid = new GridFile(10, metrics); // 10x10 rejilla
            Random rand = new Random();

            // --- MÉTRICAS DE INSERCIÓN ---
            metrics.resetDiskAccesses();
            metrics.startTimer();
            for (int i = 0; i < 20; i++) {
                int x = rand.nextInt(100);
                int y = rand.nextInt(100);
                grid.insert(new int[]{x, y});
            }
            metrics.endTimer();

            System.out.println("\n-- MÉTRICAS DE INSERCIÓN --");
            System.out.println("Tiempo (ns): " + metrics.getElapsedTime());
            System.out.println("Memoria (bytes): " + metrics.getUsedMemory());
            System.out.println("Accesos simulados a disco: " + metrics.getDiskAccesses());


            // Consulta puntual
            int[] consulta = {50, 50};
            metrics.resetDiskAccesses();
            metrics.startTimer();
            boolean existe = grid.containsPoint(consulta);
            metrics.endTimer();
            System.out.println("¿El punto (" + consulta[0] + ", " + consulta[1] + ") está en el Grid? " + existe);

            System.out.println("\n-- CONSULTA PUNTUAL --");
            System.out.println("Existe: " + existe);
            System.out.println("Tiempo (ns): " + metrics.getElapsedTime());
            System.out.println("Accesos simulados a disco: " + metrics.getDiskAccesses());


            // Consulta por rango rectangular
            System.out.println("\nConsulta por rango: (20 ≤ x ≤ 70, 20 ≤ y ≤ 70)");
            metrics.resetDiskAccesses();
            metrics.startTimer();
            var puntos = grid.rangeQuery(20, 70, 20, 70);
            for (int[] p : puntos) {
                System.out.println("(" + p[0] + ", " + p[1] + ")");
            }
            metrics.endTimer();
            System.out.println("Total encontrados: " + puntos.size() + "\n");

            System.out.println("\n-- CONSULTA POR RANGO --");
            System.out.println("Puntos encontrados: " + puntos.size());
            System.out.println("Tiempo (ns): " + metrics.getElapsedTime());
            System.out.println("Accesos simulados a disco: " + metrics.getDiskAccesses());


            // Vecino más cercano
            metrics.resetDiskAccesses();
            metrics.startTimer();
            int[] vecino = grid.nearestNeighbor(consulta);
            metrics.endTimer();
            System.out.println("Vecino más cercano a (50, 50): (" + vecino[0] + ", " + vecino[1] + ")");

            System.out.println("\n-- VECINO MÁS CERCANO --");
            System.out.println("Vecino: (" + vecino[0] + ", " + vecino[1] + ")");
            System.out.println("Tiempo (ns): " + metrics.getElapsedTime());
            System.out.println("Accesos simulados a disco: " + metrics.getDiskAccesses());


            JFrame frame = new JFrame("Visualización Grid File");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            GridFilePanel panel = new GridFilePanel(grid);
            frame.add(panel, BorderLayout.CENTER);

            frame.pack();
            frame.setVisible(true);

        });
    }
}

