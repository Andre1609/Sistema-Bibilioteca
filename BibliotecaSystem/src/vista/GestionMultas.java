package vista;

import dao.MultaDAO;
import dao.PrestamoDAO;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Pantalla de gestión de multas del sistema.
 */
public class GestionMultas extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable tablaVencidos, tablaMultas;
    private DefaultTableModel modeloVencidos, modeloMultas;
    private JButton btnGenerarMulta, btnPagarMulta, btnActualizar;
    private MultaDAO multaDAO;
    private PrestamoDAO prestamoDAO;

    public GestionMultas() {
        multaDAO = new MultaDAO();
        prestamoDAO = new PrestamoDAO();
        initComponents();
        cargarTablas();
    }

    /**
     * Inicializa y configura los componentes de la pantalla.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        // ── Panel superior: préstamos vencidos ──
        JPanel panelVencidos = new JPanel(new BorderLayout());
        panelVencidos.setBackground(new Color(30, 30, 30));
        panelVencidos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.RED),
            "Préstamos Vencidos",
            0, 0, new Font("Arial", Font.BOLD, 13), Color.RED
        ));

        String[] colVencidos = {"ID Préstamo", "Estudiante", "Código", "Fecha Préstamo", "Fecha Vencimiento", "Días Vencido"};
        modeloVencidos = new DefaultTableModel(colVencidos, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaVencidos = new JTable(modeloVencidos);
        tablaVencidos.setBackground(new Color(50, 50, 50));
        tablaVencidos.setForeground(Color.WHITE);
        tablaVencidos.setGridColor(Color.DARK_GRAY);
        tablaVencidos.getTableHeader().setBackground(Color.BLACK);
        tablaVencidos.getTableHeader().setForeground(Color.WHITE);
        tablaVencidos.setSelectionBackground(new Color(178, 34, 34));
        tablaVencidos.setRowHeight(22);

        JScrollPane scrollVencidos = new JScrollPane(tablaVencidos);
        scrollVencidos.getViewport().setBackground(new Color(50, 50, 50));
        scrollVencidos.setPreferredSize(new Dimension(0, 150));
        panelVencidos.add(scrollVencidos, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBackground(new Color(30, 30, 30));

        btnGenerarMulta = boton("GENERAR MULTA", new Color(178, 34, 34));
        btnActualizar   = boton("ACTUALIZAR", new Color(70, 130, 180));

        btnGenerarMulta.addActionListener(e -> generarMulta());
        btnActualizar.addActionListener(e -> cargarTablas());

        panelBotones.add(btnGenerarMulta);
        panelBotones.add(btnActualizar);
        panelVencidos.add(panelBotones, BorderLayout.SOUTH);

        add(panelVencidos, BorderLayout.NORTH);

        // ── Panel inferior: listado de multas ──
        JPanel panelMultas = new JPanel(new BorderLayout());
        panelMultas.setBackground(new Color(30, 30, 30));
        panelMultas.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            "Listado de Multas",
            0, 0, new Font("Arial", Font.BOLD, 13), Color.WHITE
        ));

        String[] colMultas = {"ID Multa", "Estudiante", "Código", "Fecha Préstamo", "Fecha Vencimiento", "Monto S/.", "Motivo", "Estado"};
        modeloMultas = new DefaultTableModel(colMultas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaMultas = new JTable(modeloMultas);
        tablaMultas.setBackground(new Color(50, 50, 50));
        tablaMultas.setForeground(Color.WHITE);
        tablaMultas.setGridColor(Color.DARK_GRAY);
        tablaMultas.getTableHeader().setBackground(Color.BLACK);
        tablaMultas.getTableHeader().setForeground(Color.WHITE);
        tablaMultas.setSelectionBackground(new Color(70, 130, 180));
        tablaMultas.setRowHeight(22);

        JScrollPane scrollMultas = new JScrollPane(tablaMultas);
        scrollMultas.getViewport().setBackground(new Color(50, 50, 50));
        panelMultas.add(scrollMultas, BorderLayout.CENTER);

        // Botón pagar multa
        JPanel panelPagar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelPagar.setBackground(new Color(30, 30, 30));
        btnPagarMulta = boton("MARCAR COMO PAGADA", new Color(0, 128, 0));
        btnPagarMulta.addActionListener(e -> pagarMulta());
        panelPagar.add(btnPagarMulta);
        panelMultas.add(panelPagar, BorderLayout.SOUTH);

        add(panelMultas, BorderLayout.CENTER);
    }

    /**
     * Carga ambas tablas al iniciar o actualizar.
     */
    private void cargarTablas() {
        cargarVencidos();
        cargarMultas();
    }

    /**
     * Carga los préstamos vencidos en la tabla superior.
     */
    private void cargarVencidos() {
        modeloVencidos.setRowCount(0);
        ArrayList<Prestamo> lista = prestamoDAO.listarPrestamosVencidos();
        for (Prestamo p : lista) {
            long diasVencido = ChronoUnit.DAYS.between(
                p.getFechaVencimiento().toLocalDate(), LocalDate.now()
            );
            modeloVencidos.addRow(new Object[]{
            	    p.getIdPrestamo(),
            	    p.getNombreEstudiante(),
            	    p.getCodigoEstudiante(),
            	    formatearFecha(p.getFechaPrestamo()),
            	    formatearFecha(p.getFechaVencimiento()),
            	    diasVencido + " día(s)"
            	});
        }
    }

    /**
     * Carga todas las multas en la tabla inferior.
     */
    private void cargarMultas() {
        modeloMultas.setRowCount(0);
        ArrayList<Object[]> lista = multaDAO.listarMultas();
        for (Object[] fila : lista) {
            modeloMultas.addRow(fila);
        }
    }

    /**
     * Genera una multa para el préstamo vencido seleccionado.
     * Calcula el monto según los días vencidos (S/. 1.00 por día).
     */
    private void generarMulta() {
        int fila = tablaVencidos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo vencido.");
            return;
        }

        int idPrestamo = (int) modeloVencidos.getValueAt(fila, 0);
        String diasStr = (String) modeloVencidos.getValueAt(fila, 5);
        long dias = Long.parseLong(diasStr.replace(" día(s)", ""));
        double monto = dias * 1.00; // S/. 1.00 por día de retraso
        String motivo = "Devolución tardía - " + dias + " día(s) de retraso";

        if (multaDAO.existeMulta(idPrestamo)) {
            JOptionPane.showMessageDialog(this, "Este préstamo ya tiene una multa generada.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Generar multa de S/. " + String.format("%.2f", monto) + " para este préstamo?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (multaDAO.generarMulta(idPrestamo, monto, motivo)) {
                JOptionPane.showMessageDialog(this, "Multa generada correctamente.");
                cargarTablas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al generar la multa.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Marca la multa seleccionada como pagada.
     */
    private void pagarMulta() {
        int fila = tablaMultas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una multa de la tabla.");
            return;
        }

        String estado = (String) modeloMultas.getValueAt(fila, 7);
        if (estado.equals("PAGADA")) {
            JOptionPane.showMessageDialog(this, "Esta multa ya fue pagada.");
            return;
        }

        int idMulta = (int) modeloMultas.getValueAt(fila, 0);
        double monto = (double) modeloMultas.getValueAt(fila, 5);

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar pago de S/. " + String.format("%.2f", monto) + "?",
            "Confirmar pago", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (multaDAO.pagarMulta(idMulta)) {
                JOptionPane.showMessageDialog(this, "Multa marcada como pagada.");
                cargarTablas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar pago.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBorder(BorderFactory.createLineBorder(color.darker()));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        return b;
    }
    /**
     * Formatea una fecha SQL a formato DD/MM/YYYY.
     */
    private String formatearFecha(java.sql.Date fecha) {
        if (fecha == null) return "";
        java.time.format.DateTimeFormatter fmt = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.toLocalDate().format(fmt);
    }
}
