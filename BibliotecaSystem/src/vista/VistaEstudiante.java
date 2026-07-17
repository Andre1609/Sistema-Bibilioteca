package vista;

import dao.EstudianteDAO;
import dao.LibroDAO;
import dao.MultaDAO;
import dao.PrestamoDAO;
import modelo.Libro;
import modelo.Prestamo;
import util.SesionActual;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Pantalla principal del rol Estudiante.
 */
public class VistaEstudiante extends JPanel {

    private static final long serialVersionUID = 1L;
    private JPanel panelContenido;
    private LibroDAO libroDAO;
    private PrestamoDAO prestamoDAO;
    private MultaDAO multaDAO;

    public VistaEstudiante() {
        libroDAO = new LibroDAO();
        prestamoDAO = new PrestamoDAO();
        multaDAO = new MultaDAO();
        initComponents();
    }

    /**
     * Inicializa y configura los componentes de la pantalla.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        // ── Panel superior: botones de navegación ──
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(30, 30, 30));
        panelBotones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            "Portal Estudiante",
            0, 0, new Font("Arial", Font.BOLD, 13), Color.WHITE
        ));

        JButton btnCatalogo    = boton("CATÁLOGO DE LIBROS", new Color(70, 130, 180));
        JButton btnPrestamos   = boton("MIS PRÉSTAMOS", new Color(0, 128, 0));
        JButton btnMultas      = boton("MIS MULTAS", new Color(178, 34, 34));

        btnCatalogo.addActionListener(e -> mostrarCatalogo());
        btnPrestamos.addActionListener(e -> mostrarMisPrestamos());
        btnMultas.addActionListener(e -> mostrarMisMultas());

        panelBotones.add(btnCatalogo);
        panelBotones.add(btnPrestamos);
        panelBotones.add(btnMultas);

        add(panelBotones, BorderLayout.NORTH);

        // ── Panel central: contenido dinámico ──
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(new Color(40, 40, 40));

        JLabel lblBienvenida = new JLabel(
            "Bienvenido, " + SesionActual.getUsuario().getUsuario().toUpperCase(),
            SwingConstants.CENTER
        );
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        panelContenido.add(lblBienvenida, BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);
    }
    /**
     * Muestra el catálogo de libros disponibles con opción de solicitar préstamo.
     */
    private void mostrarCatalogo() {
        panelContenido.removeAll();

        String[] columnas = {"ID", "Título", "Autor", "Editorial", "Año", "Disponibles", "Categoría"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        ArrayList<Libro> lista = libroDAO.listarLibros();
        for (Libro l : lista) {
            if (l.getStockDisponible() > 0) {
                modelo.addRow(new Object[]{
                    l.getIdLibro(),
                    l.getTitulo(),
                    l.getAutor(),
                    l.getEditorial(),
                    l.getAnioPublicacion(),
                    l.getStockDisponible(),
                    l.getNombreCategoria()
                });
            }
        }

        JTable tabla = crearTabla(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(new Color(50, 50, 50));

        // Botón solicitar préstamo
        JButton btnSolicitar = boton("SOLICITAR PRÉSTAMO", new Color(0, 128, 0));
        btnSolicitar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panelContenido, "Seleccione un libro de la tabla.");
                return;
            }
            int idLibro = (int) modelo.getValueAt(fila, 0);
            String tituloLibro = (String) modelo.getValueAt(fila, 1);
            int stock = (int) modelo.getValueAt(fila, 5);

            if (stock <= 0) {
                JOptionPane.showMessageDialog(panelContenido, "No hay ejemplares disponibles.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(panelContenido,
                "¿Desea solicitar el préstamo del libro?\n" + tituloLibro,
                "Confirmar Préstamo", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                int idEstudiante = SesionActual.getUsuario().getIdEstudiante();
                if (idEstudiante == 0) {
                    JOptionPane.showMessageDialog(panelContenido,
                        "Tu usuario no está vinculado a un estudiante.\nContacta al administrador.");
                    return;
                }

                java.sql.Date fechaHoy = java.sql.Date.valueOf(java.time.LocalDate.now());
                java.sql.Date fechaVenc = java.sql.Date.valueOf(java.time.LocalDate.now().plusDays(7));

                modelo.Prestamo p = new modelo.Prestamo();
                p.setFechaPrestamo(fechaHoy);
                p.setFechaVencimiento(fechaVenc);
                p.setIdUsuario(SesionActual.getUsuario().getIdUsuario());
                p.setIdEstudiante(idEstudiante);

                int idPrestamo = prestamoDAO.registrarPrestamo(p);
                if (idPrestamo != -1) {
                    prestamoDAO.registrarDetalle(idPrestamo, idLibro, 1);
                    prestamoDAO.reducirStock(idLibro, 1);
                    java.time.format.DateTimeFormatter fmt =
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    JOptionPane.showMessageDialog(panelContenido,
                        "Préstamo registrado correctamente.\n" +
                        "Libro: " + tituloLibro + "\n" +
                        "Fecha de devolución: " + fechaVenc.toLocalDate().format(fmt));
                    mostrarCatalogo();
                } else {
                    JOptionPane.showMessageDialog(panelContenido, "Error al registrar el préstamo.");
                }
            }
        });

        JLabel lblTitulo = new JLabel("Catálogo de Libros Disponibles", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel panelBotonCat = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotonCat.setBackground(new Color(40, 40, 40));
        panelBotonCat.add(btnSolicitar);

        panelContenido.add(lblTitulo, BorderLayout.NORTH);
        panelContenido.add(scroll, BorderLayout.CENTER);
        panelContenido.add(panelBotonCat, BorderLayout.SOUTH);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /**
     * Muestra los préstamos del estudiante logueado con fecha de devolución.
     */
    private void mostrarMisPrestamos() {
        panelContenido.removeAll();

        int idEstudiante = SesionActual.getUsuario().getIdEstudiante();

        String[] columnas = {"ID", "Libro", "Fecha Préstamo", "Fecha Devolución Máxima", "Estado", "Días Restantes"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT p.id_prestamo, l.titulo, p.fecha_prestamo, " +
                     "p.fecha_vencimiento, p.estado " +
                     "FROM prestamos p " +
                     "JOIN detalle_prestamo dp ON p.id_prestamo = dp.id_prestamo " +
                     "JOIN libros l ON dp.id_libro = l.id_libro " +
                     "WHERE p.id_estudiante = ? " +
                     "ORDER BY p.fecha_prestamo DESC";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.sql.Connection cn = conexion.ConexionBD.getConexion();
            java.sql.PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idEstudiante);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                java.time.LocalDate fechaVenc = rs.getDate("fecha_vencimiento").toLocalDate();
                long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.LocalDate.now(), fechaVenc
                );
                String diasStr;
                if (rs.getString("estado").equals("DEVUELTO")) {
                    diasStr = "Devuelto";
                } else if (diasRestantes < 0) {
                    diasStr = "Vencido (" + Math.abs(diasRestantes) + " días)";
                } else {
                    diasStr = diasRestantes + " día(s)";
                }
                modelo.addRow(new Object[]{
                    rs.getInt("id_prestamo"),
                    rs.getString("titulo"),
                    rs.getDate("fecha_prestamo").toLocalDate().format(fmt),
                    fechaVenc.format(fmt),
                    rs.getString("estado"),
                    diasStr
                });
            }
        } catch (Exception e) {
            System.out.println("Error al cargar préstamos: " + e.getMessage());
        }

        JTable tabla = crearTabla(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(new Color(50, 50, 50));

        JLabel lblTitulo = new JLabel("Mis Préstamos", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        panelContenido.add(lblTitulo, BorderLayout.NORTH);
        panelContenido.add(scroll, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /**
     * Muestra las multas del estudiante logueado.
     */
    private void mostrarMisMultas() {
        panelContenido.removeAll();

        int idEstudiante = SesionActual.getUsuario().getIdEstudiante();

        String[] columnas = {"ID Multa", "Libro", "Fecha Vencimiento", "Monto S/.", "Motivo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT m.id_multa, l.titulo, p.fecha_vencimiento, " +
                     "m.monto, m.motivo, m.pagada " +
                     "FROM multas m " +
                     "JOIN prestamos p ON m.id_prestamo = p.id_prestamo " +
                     "JOIN detalle_prestamo dp ON p.id_prestamo = dp.id_prestamo " +
                     "JOIN libros l ON dp.id_libro = l.id_libro " +
                     "WHERE p.id_estudiante = ? " +
                     "ORDER BY m.pagada ASC";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.sql.Connection cn = conexion.ConexionBD.getConexion();
            java.sql.PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idEstudiante);
            java.sql.ResultSet rs = ps.executeQuery();
            double totalPendiente = 0;
            while (rs.next()) {
                double monto = rs.getDouble("monto");
                boolean pagada = rs.getBoolean("pagada");
                if (!pagada) totalPendiente += monto;
                modelo.addRow(new Object[]{
                    rs.getInt("id_multa"),
                    rs.getString("titulo"),
                    rs.getDate("fecha_vencimiento").toLocalDate().format(fmt),
                    String.format("S/. %.2f", monto),
                    rs.getString("motivo"),
                    pagada ? "PAGADA" : "PENDIENTE"
                });
            }

            // Mostrar total pendiente
            JLabel lblTotal = new JLabel(
                "Total pendiente de pago: S/. " + String.format("%.2f", totalPendiente),
                SwingConstants.CENTER
            );
            lblTotal.setForeground(totalPendiente > 0 ? Color.RED : new Color(100, 200, 100));
            lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
            lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            panelContenido.add(lblTotal, BorderLayout.SOUTH);

        } catch (Exception e) {
            System.out.println("Error al cargar multas: " + e.getMessage());
        }

        JTable tabla = crearTabla(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(new Color(50, 50, 50));

        JLabel lblTitulo = new JLabel("Mis Multas", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        panelContenido.add(lblTitulo, BorderLayout.NORTH);
        panelContenido.add(scroll, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /**
     * Crea una JTable estilizada con el modelo dado.
     */
    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setBackground(new Color(50, 50, 50));
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(Color.DARK_GRAY);
        tabla.getTableHeader().setBackground(Color.BLACK);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(70, 130, 180));
        tabla.setRowHeight(22);
        return tabla;
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
