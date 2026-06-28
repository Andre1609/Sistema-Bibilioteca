package vista;

import dao.PrestamoDAO;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Pantalla exclusiva para registrar devoluciones de libros.
 */
public class GestionDevoluciones extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;
    private JButton btnDevolver, btnActualizar;
    private PrestamoDAO prestamoDAO;

    public GestionDevoluciones() {
        prestamoDAO = new PrestamoDAO();
        initComponents();
        cargarTabla();
    }

    /**
     * Inicializa y configura los componentes de la pantalla.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        // ── Panel superior: título e instrucción ──
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(30, 30, 30));
        panelTitulo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            "Registro de Devoluciones",
            0, 0, new Font("Arial", Font.BOLD, 13), Color.WHITE
        ));

        JLabel lblInstruccion = new JLabel(
            "Seleccione un préstamo activo de la tabla y haga clic en REGISTRAR DEVOLUCIÓN",
            SwingConstants.CENTER
        );
        lblInstruccion.setForeground(Color.YELLOW);
        lblInstruccion.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInstruccion.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelTitulo.add(lblInstruccion, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBackground(new Color(30, 30, 30));

        btnDevolver   = boton("REGISTRAR DEVOLUCIÓN", new Color(184, 134, 11));
        btnActualizar = boton("ACTUALIZAR", new Color(70, 130, 180));

        btnDevolver.addActionListener(e -> registrarDevolucion());
        btnActualizar.addActionListener(e -> cargarTabla());

        panelBotones.add(btnDevolver);
        panelBotones.add(btnActualizar);
        panelTitulo.add(panelBotones, BorderLayout.SOUTH);

        add(panelTitulo, BorderLayout.NORTH);

        // ── Panel central: tabla préstamos activos ──
        String[] columnas = {"ID", "Estudiante", "Código", "Libro", "Fecha Préstamo", "Fecha Vencimiento", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.setBackground(new Color(50, 50, 50));
        tablaPrestamos.setForeground(Color.WHITE);
        tablaPrestamos.setGridColor(Color.DARK_GRAY);
        tablaPrestamos.getTableHeader().setBackground(Color.BLACK);
        tablaPrestamos.getTableHeader().setForeground(Color.WHITE);
        tablaPrestamos.setSelectionBackground(new Color(184, 134, 11));
        tablaPrestamos.setRowHeight(22);
        tablaPrestamos.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scroll = new JScrollPane(tablaPrestamos);
        scroll.getViewport().setBackground(new Color(50, 50, 50));
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Carga los préstamos activos con el libro asociado.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT p.id_prestamo, e.nombres || ' ' || e.apellidos AS estudiante, " +
                     "e.codigo, l.titulo, p.fecha_prestamo, p.fecha_vencimiento, p.estado " +
                     "FROM prestamos p " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "JOIN detalle_prestamo dp ON p.id_prestamo = dp.id_prestamo " +
                     "JOIN libros l ON dp.id_libro = l.id_libro " +
                     "WHERE p.estado = 'ACTIVO' " +
                     "ORDER BY p.fecha_vencimiento ASC";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.sql.Connection cn = conexion.ConexionBD.getConexion();
            java.sql.PreparedStatement ps = cn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_prestamo"),
                    rs.getString("estudiante"),
                    rs.getString("codigo"),
                    rs.getString("titulo"),
                    rs.getDate("fecha_prestamo").toLocalDate().format(fmt),
                    rs.getDate("fecha_vencimiento").toLocalDate().format(fmt),
                    rs.getString("estado")
                });
            }
        } catch (Exception e) {
            System.out.println("Error al cargar devoluciones: " + e.getMessage());
        }
    }

    /**
     * Registra la devolución del préstamo seleccionado.
     */
    private void registrarDevolucion() {
        int fila = tablaPrestamos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo de la tabla.");
            return;
        }

        int idPrestamo = (int) modeloTabla.getValueAt(fila, 0);
        String estudiante = (String) modeloTabla.getValueAt(fila, 1);
        String libro = (String) modeloTabla.getValueAt(fila, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar devolución?\n" +
            "Estudiante: " + estudiante + "\n" +
            "Libro: " + libro,
            "Confirmar Devolución", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int idLibro = prestamoDAO.obtenerIdLibroDePrestamo(idPrestamo);
            if (prestamoDAO.registrarDevolucion(idPrestamo)) {
                prestamoDAO.aumentarStock(idLibro, 1);
                JOptionPane.showMessageDialog(this, "Devolución registrada correctamente.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar devolución.",
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
}
