package vista;

import dao.EstudianteDAO;
import modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Pantalla de gestión de estudiantes con CRUD completo.
 */
public class GestionEstudiantes extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField txtCodigo, txtNombres, txtApellidos, txtCarrera, txtCorreo, txtTelefono, txtBuscar;
    private JButton btnRegistrar, btnEditar, btnEliminar, btnLimpiar, btnBuscar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private EstudianteDAO estudianteDAO;
    private int idEstudianteSeleccionado = -1;

    public GestionEstudiantes() {
        estudianteDAO = new EstudianteDAO();
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
            "Gestión de Estudiantes",
            0, 0, new Font("Arial", Font.BOLD, 13), Color.WHITE
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 10, 5, 10);
        g.anchor = GridBagConstraints.WEST;

        // Fila 0 - Código
        g.gridx = 0; g.gridy = 0;
        panelForm.add(label("CÓDIGO:"), g);
        g.gridx = 1;
        txtCodigo = campo(200);
        panelForm.add(txtCodigo, g);

        // Fila 0 - Nombres
        g.gridx = 2;
        panelForm.add(label("NOMBRES:"), g);
        g.gridx = 3;
        txtNombres = campo(200);
        panelForm.add(txtNombres, g);

        // Fila 1 - Apellidos
        g.gridx = 0; g.gridy = 1;
        panelForm.add(label("APELLIDOS:"), g);
        g.gridx = 1;
        txtApellidos = campo(200);
        panelForm.add(txtApellidos, g);

        // Fila 1 - Carrera
        g.gridx = 2;
        panelForm.add(label("CARRERA:"), g);
        g.gridx = 3;
        txtCarrera = campo(200);
        panelForm.add(txtCarrera, g);

        // Fila 2 - Correo
        g.gridx = 0; g.gridy = 2;
        panelForm.add(label("CORREO:"), g);
        g.gridx = 1;
        txtCorreo = campo(200);
        panelForm.add(txtCorreo, g);

        // Fila 2 - Teléfono
        g.gridx = 2;
        panelForm.add(label("TELÉFONO:"), g);
        g.gridx = 3;
        txtTelefono = campo(200);
        panelForm.add(txtTelefono, g);

        // Fila 3 - Búsqueda
        g.gridx = 0; g.gridy = 3;
        panelForm.add(label("BUSCAR:"), g);
        g.gridx = 1;
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelBuscar.setBackground(new Color(30, 30, 30));
        txtBuscar = campo(140);
        btnBuscar = boton("BUSCAR", new Color(70, 130, 180));
        btnBuscar.addActionListener(e -> buscarEstudiante());
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

        btnRegistrar.addActionListener(e -> registrarEstudiante());
        btnEditar.addActionListener(e -> editarEstudiante());
        btnEliminar.addActionListener(e -> eliminarEstudiante());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelForm.add(panelBotones, g);

        add(panelForm, BorderLayout.NORTH);

        // ── Panel inferior: tabla ──
        String[] columnas = {"ID", "Código", "Nombres", "Apellidos", "Carrera", "Correo", "Teléfono"};
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
     * Carga todos los estudiantes en la tabla.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        ArrayList<Estudiante> lista = estudianteDAO.listarEstudiantes();
        for (Estudiante e : lista) {
            modeloTabla.addRow(new Object[]{
                e.getIdEstudiante(), e.getCodigo(), e.getNombres(),
                e.getApellidos(), e.getCarrera(), e.getCorreo(), e.getTelefono()
            });
        }
    }

    /**
     * Carga los datos del estudiante seleccionado en el formulario.
     */
    private void cargarDatosDesdeTabla() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        idEstudianteSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtCodigo.setText((String) modeloTabla.getValueAt(fila, 1));
        txtNombres.setText((String) modeloTabla.getValueAt(fila, 2));
        txtApellidos.setText((String) modeloTabla.getValueAt(fila, 3));
        txtCarrera.setText((String) modeloTabla.getValueAt(fila, 4));
        txtCorreo.setText((String) modeloTabla.getValueAt(fila, 5));
        txtTelefono.setText((String) modeloTabla.getValueAt(fila, 6));
    }

    /**
     * Registra un nuevo estudiante en la base de datos.
     */
    private void registrarEstudiante() {
        if (!validarCampos()) return;
        Estudiante e = obtenerEstudianteDelFormulario();
        if (estudianteDAO.registrarEstudiante(e)) {
            JOptionPane.showMessageDialog(this, "Estudiante registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar. Verifique que el código no esté repetido.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Edita el estudiante seleccionado en la tabla.
     */
    private void editarEstudiante() {
        if (idEstudianteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.");
            return;
        }
        if (!validarCampos()) return;
        Estudiante e = obtenerEstudianteDelFormulario();
        e.setIdEstudiante(idEstudianteSeleccionado);
        if (estudianteDAO.actualizarEstudiante(e)) {
            JOptionPane.showMessageDialog(this, "Estudiante actualizado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina lógicamente el estudiante seleccionado.
     */
    private void eliminarEstudiante() {
        if (idEstudianteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar este estudiante?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (estudianteDAO.eliminarEstudiante(idEstudianteSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.");
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Busca estudiantes por nombre, apellido o código.
     */
    private void buscarEstudiante() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            cargarTabla();
            return;
        }
        modeloTabla.setRowCount(0);
        ArrayList<Estudiante> lista = estudianteDAO.buscarEstudiantes(criterio);
        for (Estudiante e : lista) {
            modeloTabla.addRow(new Object[]{
                e.getIdEstudiante(), e.getCodigo(), e.getNombres(),
                e.getApellidos(), e.getCarrera(), e.getCorreo(), e.getTelefono()
            });
        }
    }

    /**
     * Obtiene un objeto Estudiante con los datos del formulario.
     */
    private Estudiante obtenerEstudianteDelFormulario() {
        Estudiante e = new Estudiante();
        e.setCodigo(txtCodigo.getText().trim());
        e.setNombres(txtNombres.getText().trim());
        e.setApellidos(txtApellidos.getText().trim());
        e.setCarrera(txtCarrera.getText().trim());
        e.setCorreo(txtCorreo.getText().trim());
        e.setTelefono(txtTelefono.getText().trim());
        return e;
    }

    /**
     * Valida que los campos obligatorios no estén vacíos.
     */
    private boolean validarCampos() {
        if (txtCodigo.getText().trim().isEmpty() ||
            txtNombres.getText().trim().isEmpty() ||
            txtApellidos.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Código, Nombres y Apellidos son obligatorios.");
            return false;
        }
        return true;
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCarrera.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtBuscar.setText("");
        idEstudianteSeleccionado = -1;
        tabla.clearSelection();
    }
}