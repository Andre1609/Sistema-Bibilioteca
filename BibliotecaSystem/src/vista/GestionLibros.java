package vista;
import util.SesionActual;



import dao.CategoriaDAO;
import dao.LibroDAO;
import modelo.Categoria;
import modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Pantalla de gestión de libros con CRUD completo.
 */
public class GestionLibros extends JPanel {
	private static final long serialVersionUID = 1L;

    private JTextField txtTitulo, txtAutor, txtIsbn, txtEditorial, txtAnio, txtStock, txtBuscar;
    private JComboBox<Categoria> cmbCategoria;
    private JButton btnRegistrar, btnEditar, btnEliminar, btnLimpiar, btnBuscar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private LibroDAO libroDAO;
    private CategoriaDAO categoriaDAO;
    private int idLibroSeleccionado = -1;

    public GestionLibros() {
        libroDAO = new LibroDAO();
        categoriaDAO = new CategoriaDAO();
        initComponents();
        cargarCategorias();
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
            "Gestión de Libros",
            0, 0, new Font("Arial", Font.BOLD, 13), Color.WHITE
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 10, 5, 10);
        g.anchor = GridBagConstraints.WEST;

        // Fila 0 - Título
        g.gridx = 0; g.gridy = 0;
        panelForm.add(label("TÍTULO:"), g);
        g.gridx = 1;
        txtTitulo = campo(200);
        panelForm.add(txtTitulo, g);

        // Fila 0 - Autor
        g.gridx = 2;
        panelForm.add(label("AUTOR:"), g);
        g.gridx = 3;
        txtAutor = campo(200);
        panelForm.add(txtAutor, g);

        // Fila 1 - ISBN
        g.gridx = 0; g.gridy = 1;
        panelForm.add(label("ISBN:"), g);
        g.gridx = 1;
        txtIsbn = campo(200);
        panelForm.add(txtIsbn, g);

        // Fila 1 - Editorial
        g.gridx = 2;
        panelForm.add(label("EDITORIAL:"), g);
        g.gridx = 3;
        txtEditorial = campo(200);
        panelForm.add(txtEditorial, g);

        // Fila 2 - Año
        g.gridx = 0; g.gridy = 2;
        panelForm.add(label("AÑO:"), g);
        g.gridx = 1;
        txtAnio = campo(200);
        panelForm.add(txtAnio, g);

        // Fila 2 - Stock
        g.gridx = 2;
        panelForm.add(label("STOCK:"), g);
        g.gridx = 3;
        txtStock = campo(200);
        panelForm.add(txtStock, g);

        // Fila 3 - Categoría
        g.gridx = 0; g.gridy = 3;
        panelForm.add(label("CATEGORÍA:"), g);
        g.gridx = 1;
        cmbCategoria = new JComboBox<>();
        cmbCategoria.setPreferredSize(new Dimension(200, 25));
        cmbCategoria.setBackground(Color.BLACK);
        cmbCategoria.setForeground(Color.WHITE);
        panelForm.add(cmbCategoria, g);

        // Fila 3 - Búsqueda
        g.gridx = 2;
        panelForm.add(label("BUSCAR:"), g);
        g.gridx = 3;
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelBuscar.setBackground(new Color(30, 30, 30));
        txtBuscar = campo(140);
        btnBuscar = boton("BUSCAR", new Color(70, 130, 180));
        btnBuscar.addActionListener(e -> buscarLibro());
        panelBuscar.add(txtBuscar);
        panelBuscar.add(btnBuscar);
        panelForm.add(panelBuscar, g);

        // Fila 4 - Botones
        g.gridx = 0; g.gridy = 4;
        g.gridwidth = 4;
        g.anchor = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBackground(new Color(30, 30, 30));

        btnRegistrar = boton("REGISTRAR", new Color(0, 128, 0));
        btnEditar    = boton("EDITAR", new Color(184, 134, 11));
        btnEliminar  = boton("ELIMINAR", new Color(178, 34, 34));
        btnLimpiar   = boton("LIMPIAR", new Color(80, 80, 80));

        btnRegistrar.addActionListener(e -> registrarLibro());
        btnEditar.addActionListener(e -> editarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelForm.add(panelBotones, g);

        add(panelForm, BorderLayout.NORTH);

        // ── Panel inferior: tabla ──
        String[] columnas = {"ID", "Título", "Autor", "ISBN", "Editorial", "Año", "Stock Total", "Disponible", "Categoría"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setBackground(new Color(50, 50, 50));
        tabla.setForeground(Color.WHITE);
        tabla.setGridColor(Color.DARK_GRAY);
        tabla.getTableHeader().setBackground(Color.BLACK);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(70, 130, 180));
        tabla.setRowHeight(22);
        tabla.getColumnModel().getColumn(0).setMaxWidth(40);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarDatosDesdeTabla();
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(new Color(50, 50, 50));
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Crea un JLabel estilizado.
     */
    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        return l;
    }

    /**
     * Crea un JTextField estilizado.
     */
    private JTextField campo(int ancho) {
        JTextField t = new JTextField();
        t.setPreferredSize(new Dimension(ancho, 25));
        t.setBackground(Color.BLACK);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return t;
    }

    /**
     * Crea un JButton estilizado.
     */
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
     * Carga las categorías en el JComboBox.
     */
    private void cargarCategorias() {
        cmbCategoria.removeAllItems();
        ArrayList<Categoria> cats = categoriaDAO.listarCategorias();
        for (Categoria c : cats) cmbCategoria.addItem(c);
    }

    /**
     * Carga todos los libros en la tabla.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        ArrayList<Libro> lista = libroDAO.listarLibros();

        // Limpiar catálogo anterior y cargar nuevos libros
        SesionActual.getEstructuras().getCatalogo().clear();
        for (Libro l : lista) {
            SesionActual.getEstructuras().agregarLibroCatalogo(l);
        }

        // Ordenar con BubbleSort
        SesionActual.getEstructuras().ordenarCatalogoPorTitulo();

        // Mostrar la lista YA ORDENADA en la tabla
        for (Libro l : SesionActual.getEstructuras().getCatalogo()) {
            modeloTabla.addRow(new Object[]{
                l.getIdLibro(), l.getTitulo(), l.getAutor(),
                l.getIsbn(), l.getEditorial(), l.getAnioPublicacion(),
                l.getStockTotal(), l.getStockDisponible(), l.getNombreCategoria()
            });
        }
    }

    /**
     * Carga los datos del libro seleccionado en el formulario.
     */
    private void cargarDatosDesdeTabla() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        idLibroSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtTitulo.setText((String) modeloTabla.getValueAt(fila, 1));
        txtAutor.setText((String) modeloTabla.getValueAt(fila, 2));
        txtIsbn.setText((String) modeloTabla.getValueAt(fila, 3));
        txtEditorial.setText((String) modeloTabla.getValueAt(fila, 4));
        txtAnio.setText(String.valueOf(modeloTabla.getValueAt(fila, 5)));
        txtStock.setText(String.valueOf(modeloTabla.getValueAt(fila, 6)));
    }

    /**
     * Registra un nuevo libro en la base de datos.
     */
    private void registrarLibro() {
        if (!validarCampos()) return;
        Libro l = obtenerLibroDelFormulario();
        if (libroDAO.registrarLibro(l)) {
            // Registrar en Stack
            SesionActual.getEstructuras().registrarOperacion(
                "REGISTRAR LIBRO: " + l.getTitulo()
            );
            JOptionPane.showMessageDialog(this, "Libro registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Edita el libro seleccionado en la tabla.
     */
    private void editarLibro() {
        if (idLibroSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla.");
            return;
        }
        if (!validarCampos()) return;
        Libro l = obtenerLibroDelFormulario();
        l.setIdLibro(idLibroSeleccionado);
        if (libroDAO.actualizarLibro(l)) {
            JOptionPane.showMessageDialog(this, "Libro actualizado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina lógicamente el libro seleccionado.
     */
    private void eliminarLibro() {
        if (idLibroSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar este libro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (libroDAO.eliminarLibro(idLibroSeleccionado)) {
                // Registrar en Stack
                SesionActual.getEstructuras().registrarOperacion(
                    "ELIMINAR LIBRO ID: " + idLibroSeleccionado
                );
                JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.");
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Busca libros por título o autor.
     */
    private void buscarLibro() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            cargarTabla();
            return;
        }
        modeloTabla.setRowCount(0);
        ArrayList<Libro> lista = libroDAO.buscarLibros(criterio);
        for (Libro l : lista) {
            modeloTabla.addRow(new Object[]{
                l.getIdLibro(), l.getTitulo(), l.getAutor(),
                l.getIsbn(), l.getEditorial(), l.getAnioPublicacion(),
                l.getStockTotal(), l.getStockDisponible(), l.getNombreCategoria()
            });
        }
    }

    /**
     * Obtiene un objeto Libro con los datos del formulario.
     */
    private Libro obtenerLibroDelFormulario() {
        Categoria cat = (Categoria) cmbCategoria.getSelectedItem();
        Libro l = new Libro();
        l.setTitulo(txtTitulo.getText().trim());
        l.setAutor(txtAutor.getText().trim());
        l.setIsbn(txtIsbn.getText().trim());
        l.setEditorial(txtEditorial.getText().trim());
        l.setAnioPublicacion(Integer.parseInt(txtAnio.getText().trim()));
        l.setStockTotal(Integer.parseInt(txtStock.getText().trim()));
        l.setStockDisponible(Integer.parseInt(txtStock.getText().trim()));
        l.setIdCategoria(cat.getIdCategoria());
        return l;
    }

    /**
     * Valida que los campos obligatorios no estén vacíos.
     */
    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty() ||
            txtAutor.getText().trim().isEmpty() ||
            txtStock.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Título, Autor y Stock son obligatorios.");
            return false;
        }
        try {
            Integer.parseInt(txtAnio.getText().trim());
            Integer.parseInt(txtStock.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Año y Stock deben ser números.");
            return false;
        }
        return true;
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        txtAnio.setText("");
        txtStock.setText("");
        txtBuscar.setText("");
        cmbCategoria.setSelectedIndex(0);
        idLibroSeleccionado = -1;
        tabla.clearSelection();
    }
}
