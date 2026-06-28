package vista;

import conexion.ConexionBD;
import dao.PrestamoDAO;
import modelo.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Pantalla de reportes del sistema.
 */
public class Reportes extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable tablaReporte;
    private DefaultTableModel modeloTabla;
    private JButton btnLibrosMasPrestados, btnPrestamosActivos, btnHistorial;
    private JLabel lblTitulo;
    private PrestamoDAO prestamoDAO;

    public Reportes() {
        prestamoDAO = new PrestamoDAO();
        initComponents();
    }

    /**
     * Inicializa y configura los componentes de la pantalla.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        // ── Panel superior: botones de reportes ──
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(30, 30, 30));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            "Reportes",
            0, 0, new Font("Arial", Font.BOLD, 13), Color.WHITE
        ));

        btnLibrosMasPrestados = boton("LIBROS MÁS PRESTADOS", new Color(70, 130, 180));
        btnPrestamosActivos   = boton("PRÉSTAMOS ACTIVOS", new Color(0, 128, 0));
        btnHistorial          = boton("HISTORIAL GENERAL", new Color(184, 134, 11));

        btnLibrosMasPrestados.addActionListener(e -> reporteLibrosMasPrestados());
        btnPrestamosActivos.addActionListener(e -> reportePrestamosActivos());
        btnHistorial.addActionListener(e -> reporteHistorial());

        panelBotones.add(btnLibrosMasPrestados);
        panelBotones.add(btnPrestamosActivos);
        panelBotones.add(btnHistorial);

        add(panelBotones, BorderLayout.NORTH);

        // ── Panel central: título + tabla ──
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(new Color(40, 40, 40));

        lblTitulo = new JLabel("Seleccione un reporte", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelCentral.add(lblTitulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaReporte = new JTable(modeloTabla);
        tablaReporte.setBackground(new Color(50, 50, 50));
        tablaReporte.setForeground(Color.WHITE);
        tablaReporte.setGridColor(Color.DARK_GRAY);
        tablaReporte.getTableHeader().setBackground(Color.BLACK);
        tablaReporte.getTableHeader().setForeground(Color.WHITE);
        tablaReporte.setSelectionBackground(new Color(70, 130, 180));
        tablaReporte.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(tablaReporte);
        scroll.getViewport().setBackground(new Color(50, 50, 50));
        panelCentral.add(scroll, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);
    }

    /**
     * Reporte de libros más prestados ordenados por cantidad.
     */
    private void reporteLibrosMasPrestados() {
        lblTitulo.setText("Libros Más Prestados");
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(new String[]{
            "Título", "Autor", "Categoría", "Total Préstamos"
        });

        String sql = "SELECT l.titulo, l.autor, c.nombre AS categoria, " +
                     "COUNT(dp.id_libro) AS total_prestamos " +
                     "FROM detalle_prestamo dp " +
                     "JOIN libros l ON dp.id_libro = l.id_libro " +
                     "JOIN categorias c ON l.id_categoria = c.id_categoria " +
                     "GROUP BY l.titulo, l.autor, c.nombre " +
                     "ORDER BY total_prestamos DESC";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("categoria"),
                    rs.getInt("total_prestamos")
                });
            }
        } catch (Exception e) {
            System.out.println("Error en reporte libros: " + e.getMessage());
        }
    }

    /**
     * Reporte de préstamos activos actualmente.
     */
    private void reportePrestamosActivos() {
        lblTitulo.setText("Préstamos Activos");
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(new String[]{
            "ID", "Estudiante", "Código", "Libro", "Fecha Préstamo", "Fecha Vencimiento"
        });

        String sql = "SELECT p.id_prestamo, e.nombres || ' ' || e.apellidos AS estudiante, " +
                     "e.codigo, l.titulo, p.fecha_prestamo, p.fecha_vencimiento " +
                     "FROM prestamos p " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "JOIN detalle_prestamo dp ON p.id_prestamo = dp.id_prestamo " +
                     "JOIN libros l ON dp.id_libro = l.id_libro " +
                     "WHERE p.estado = 'ACTIVO' " +
                     "ORDER BY p.fecha_vencimiento ASC";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("id_prestamo"),
                    rs.getString("estudiante"),
                    rs.getString("codigo"),
                    rs.getString("titulo"),
                    rs.getDate("fecha_prestamo").toLocalDate().format(fmt),
                    rs.getDate("fecha_vencimiento").toLocalDate().format(fmt)
                });
            }
        } catch (Exception e) {
            System.out.println("Error en reporte activos: " + e.getMessage());
        }
    }

    /**
     * Reporte del historial completo de préstamos.
     */
    private void reporteHistorial() {
        lblTitulo.setText("Historial General de Préstamos");
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnIdentifiers(new String[]{
            "ID", "Estudiante", "Código", "Libro", "Fecha Préstamo", "Fecha Vencimiento", "Estado"
        });

        String sql = "SELECT p.id_prestamo, e.nombres || ' ' || e.apellidos AS estudiante, " +
                     "e.codigo, l.titulo, p.fecha_prestamo, p.fecha_vencimiento, p.estado " +
                     "FROM prestamos p " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "JOIN detalle_prestamo dp ON p.id_prestamo = dp.id_prestamo " +
                     "JOIN libros l ON dp.id_libro = l.id_libro " +
                     "ORDER BY p.fecha_prestamo DESC";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
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
            System.out.println("Error en reporte historial: " + e.getMessage());
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
