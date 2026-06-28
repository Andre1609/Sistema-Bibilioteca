package vista;

import dao.EstudianteDAO;
import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;
import util.SesionActual;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Pantalla de gestión de préstamos y devoluciones.
 */
public class GestionPrestamos extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField txtBuscarEstudiante, txtBuscarLibro;
    private JLabel lblEstudiante, lblLibro, lblStock;
    private JButton btnBuscarEstudiante, btnBuscarLibro;
    private JButton btnRegistrarPrestamo, btnRegistrarDevolucion, btnLimpiar;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;
    private PrestamoDAO prestamoDAO;
    private EstudianteDAO estudianteDAO;
    private LibroDAO libroDAO;
    private Estudiante estudianteSeleccionado = null;
    private Libro libroSeleccionado = null;

    public GestionPrestamos() {
        prestamoDAO = new PrestamoDAO();
        estudianteDAO = new EstudianteDAO();
        libroDAO = new LibroDAO();
        initComponents();
        cargarTabla();
    }

    /**
     * Inicializa y configura los componentes de la pantalla.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        // ── Panel superior: formulario ──
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(new Color(30, 30, 30));
        panelForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            "Registro de Préstamo",
            0, 0, new Font("Arial", Font.BOLD, 13), Color.WHITE
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 10, 5, 10);
        g.anchor = GridBagConstraints.WEST;

        // Fila 0 - Buscar Estudiante
        g.gridx = 0; g.gridy = 0;
        panelForm.add(label("ESTUDIANTE:"), g);
        g.gridx = 1;
        JPanel panelEst = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelEst.setBackground(new Color(30, 30, 30));
        txtBuscarEstudiante = campo(120);
        btnBuscarEstudiante = boton("BUSCAR", new Color(70, 130, 180));
        btnBuscarEstudiante.addActionListener(e -> buscarEstudiante());
        panelEst.add(txtBuscarEstudiante);
        panelEst.add(btnBuscarEstudiante);
        panelForm.add(panelEst, g);

        // Fila 0 - Info estudiante
        g.gridx = 2;
        panelForm.add(label("SELECCIONADO:"), g);
        g.gridx = 3;
        lblEstudiante = new JLabel("Ninguno");
        lblEstudiante.setForeground(new Color(100, 200, 100));
        lblEstudiante.setFont(new Font("Arial", Font.BOLD, 12));
        panelForm.add(lblEstudiante, g);

        // Fila 1 - Buscar Libro
        g.gridx = 0; g.gridy = 1;
        panelForm.add(label("LIBRO:"), g);
        g.gridx = 1;
        JPanel panelLib = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelLib.setBackground(new Color(30, 30, 30));
        txtBuscarLibro = campo(120);
        btnBuscarLibro = boton("BUSCAR", new Color(70, 130, 180));
        btnBuscarLibro.addActionListener(e -> buscarLibro());
        panelLib.add(txtBuscarLibro);
        panelLib.add(btnBuscarLibro);
        panelForm.add(panelLib, g);

        // Fila 1 - Info libro
        g.gridx = 2;
        panelForm.add(label("SELECCIONADO:"), g);
        g.gridx = 3;
        lblLibro = new JLabel("Ninguno");
        lblLibro.setForeground(new Color(100, 200, 100));
        lblLibro.setFont(new Font("Arial", Font.BOLD, 12));
        panelForm.add(lblLibro, g);

        // Fila 2 - Stock disponible
        g.gridx = 0; g.gridy = 2;
        panelForm.add(label("STOCK DISPONIBLE:"), g);
        g.gridx = 1;
        lblStock = new JLabel("-");
        lblStock.setForeground(Color.YELLOW);
        lblStock.setFont(new Font("Arial", Font.BOLD, 12));
        panelForm.add(lblStock, g);

        // Fila 3 - Botones
        g.gridx = 0; g.gridy = 3;
        g.gridwidth = 4;
        g.anchor = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBackground(new Color(30, 30, 30));

        btnRegistrarPrestamo  = boton("REGISTRAR PRÉSTAMO", new Color(0, 128, 0));
        btnRegistrarDevolucion = boton("REGISTRAR DEVOLUCIÓN", new Color(184, 134, 11));
        btnLimpiar             = boton("LIMPIAR", new Color(80, 80, 80));

        btnRegistrarPrestamo.addActionListener(e -> registrarPrestamo());
        btnRegistrarDevolucion.addActionListener(e -> registrarDevolucion());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnRegistrarPrestamo);
        panelBotones.add(btnRegistrarDevolucion);
        panelBotones.add(btnLimpiar);
        panelForm.add(panelBotones, g);

        add(panelForm, BorderLayout.NORTH);

        // ── Panel inferior: tabla préstamos activos ──
        String[] columnas = {"ID", "Estudiante", "Código", "Fecha Préstamo", "Fecha Vencimiento", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.setBackground(new Color(50, 50, 50));
        tablaPrestamos.setForeground(Color.WHITE);
        tablaPrestamos.setGridColor(Color.DARK_GRAY);
        tablaPrestamos.getTableHeader().setBackground(Color.BLACK);
        tablaPrestamos.getTableHeader().setForeground(Color.WHITE);
        tablaPrestamos.setSelectionBackground(new Color(70, 130, 180));
        tablaPrestamos.setRowHeight(22);
        tablaPrestamos.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scroll = new JScrollPane(tablaPrestamos);
        scroll.getViewport().setBackground(new Color(50, 50, 50));
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            "Préstamos Activos",
            0, 0, new Font("Arial", Font.BOLD, 12), Color.WHITE
        ));
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Busca un estudiante por código o nombre y lo selecciona.
     */
    private void buscarEstudiante() {
        String criterio = txtBuscarEstudiante.getText().trim();
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código o nombre del estudiante.");
            return;
        }
        ArrayList<Estudiante> lista = estudianteDAO.buscarEstudiantes(criterio);
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontró ningún estudiante.");
            return;
        }
        if (lista.size() == 1) {
            estudianteSeleccionado = lista.get(0);
            lblEstudiante.setText(estudianteSeleccionado.getNombres() + " " +
                                  estudianteSeleccionado.getApellidos() +
                                  " [" + estudianteSeleccionado.getCodigo() + "]");
        } else {
            // Si hay varios resultados mostrar lista para elegir
            String[] opciones = new String[lista.size()];
            for (int i = 0; i < lista.size(); i++) {
                opciones[i] = lista.get(i).getNombres() + " " +
                              lista.get(i).getApellidos() +
                              " - " + lista.get(i).getCodigo();
            }
            String seleccion = (String) JOptionPane.showInputDialog(this,
                "Seleccione el estudiante:", "Resultados",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
            if (seleccion != null) {
                int idx = java.util.Arrays.asList(opciones).indexOf(seleccion);
                estudianteSeleccionado = lista.get(idx);
                lblEstudiante.setText(estudianteSeleccionado.getNombres() + " " +
                                      estudianteSeleccionado.getApellidos() +
                                      " [" + estudianteSeleccionado.getCodigo() + "]");
            }
        }
    }

    /**
     * Busca un libro por título o autor y lo selecciona.
     */
    private void buscarLibro() {
        String criterio = txtBuscarLibro.getText().trim();
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el título o autor del libro.");
            return;
        }
        ArrayList<Libro> lista = libroDAO.buscarLibros(criterio);
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontró ningún libro.");
            return;
        }
        if (lista.size() == 1) {
            libroSeleccionado = lista.get(0);
            lblLibro.setText(libroSeleccionado.getTitulo());
            lblStock.setText(String.valueOf(libroSeleccionado.getStockDisponible()));
        } else {
            String[] opciones = new String[lista.size()];
            for (int i = 0; i < lista.size(); i++) {
                opciones[i] = lista.get(i).getTitulo() + " - " + lista.get(i).getAutor();
            }
            String seleccion = (String) JOptionPane.showInputDialog(this,
                "Seleccione el libro:", "Resultados",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
            if (seleccion != null) {
                int idx = java.util.Arrays.asList(opciones).indexOf(seleccion);
                libroSeleccionado = lista.get(idx);
                lblLibro.setText(libroSeleccionado.getTitulo());
                lblStock.setText(String.valueOf(libroSeleccionado.getStockDisponible()));
            }
        }
    }

    /**
     * Registra un nuevo préstamo con su detalle y reduce el stock.
     */
    private void registrarPrestamo() {
        if (estudianteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante.");
            return;
        }
        if (libroSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro.");
            return;
        }
        if (libroSeleccionado.getStockDisponible() <= 0) {
            JOptionPane.showMessageDialog(this, "El libro no tiene stock disponible.",
                "Sin stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Fechas: hoy y vencimiento en 7 días
        Date fechaHoy = Date.valueOf(LocalDate.now());
        Date fechaVencimiento = Date.valueOf(LocalDate.now().plusDays(7));

        Prestamo p = new Prestamo();
        p.setFechaPrestamo(fechaHoy);
        p.setFechaVencimiento(fechaVencimiento);
        p.setIdUsuario(SesionActual.getUsuario().getIdUsuario());
        p.setIdEstudiante(estudianteSeleccionado.getIdEstudiante());

        int idPrestamo = prestamoDAO.registrarPrestamo(p);
        if (idPrestamo != -1) {
            prestamoDAO.registrarDetalle(idPrestamo, libroSeleccionado.getIdLibro(), 1);
            prestamoDAO.reducirStock(libroSeleccionado.getIdLibro(), 1);
            java.time.format.DateTimeFormatter fmt = 
            	    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            	JOptionPane.showMessageDialog(this, "Préstamo registrado correctamente.\n" +
            	    "Vence el: " + fechaVencimiento.toLocalDate().format(fmt));
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el préstamo.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Registra la devolución del préstamo seleccionado en la tabla.
     */
    private void registrarDevolucion() {
        int fila = tablaPrestamos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo activo de la tabla.");
            return;
        }
        int idPrestamo = (int) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar devolución de este préstamo?", "Confirmar",
            JOptionPane.YES_NO_OPTION);
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

    /**
     * Carga los préstamos activos en la tabla.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        ArrayList<Prestamo> lista = prestamoDAO.listarPrestamosActivos();
        java.time.format.DateTimeFormatter fmt = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Prestamo p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getIdPrestamo(),
                p.getNombreEstudiante(),
                p.getCodigoEstudiante(),
                p.getFechaPrestamo().toLocalDate().format(fmt),
                p.getFechaVencimiento().toLocalDate().format(fmt),
                p.getEstado()
            });
        }
    }

    /**
     * Limpia el formulario y resetea la selección.
     */
    private void limpiarFormulario() {
        txtBuscarEstudiante.setText("");
        txtBuscarLibro.setText("");
        lblEstudiante.setText("Ninguno");
        lblLibro.setText("Ninguno");
        lblStock.setText("-");
        estudianteSeleccionado = null;
        libroSeleccionado = null;
        tablaPrestamos.clearSelection();
    }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        return l;
    }

    private JTextField campo(int ancho) {
        JTextField t = new JTextField();
        t.setPreferredSize(new Dimension(ancho, 25));
        t.setBackground(Color.BLACK);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return t;
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
