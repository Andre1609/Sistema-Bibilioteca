package vista;

import dao.UsuarioDAO;
import dao.CategoriaDAO;
import modelo.Rol;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Pantalla de gestión de usuarios con CRUD completo.
 */
public class GestionUsuarios extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField txtUsuario, txtContrasena, txtBuscar;
    private JComboBox<Rol> cmbRol;
    private JButton btnRegistrar, btnEditar, btnEliminar, btnLimpiar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private UsuarioDAO usuarioDAO;
    private int idUsuarioSeleccionado = -1;

    public GestionUsuarios() {
        usuarioDAO = new UsuarioDAO();
        initComponents();
        cargarRoles();
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
            "Gestión de Usuarios",
            0, 0, new Font("Arial", Font.BOLD, 13), Color.WHITE
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 10, 5, 10);
        g.anchor = GridBagConstraints.WEST;

        // Fila 0 - Usuario
        g.gridx = 0; g.gridy = 0;
        panelForm.add(label("USUARIO:"), g);
        g.gridx = 1;
        txtUsuario = campo(200);
        panelForm.add(txtUsuario, g);

        // Fila 0 - Contraseña
        g.gridx = 2;
        panelForm.add(label("CONTRASEÑA:"), g);
        g.gridx = 3;
        txtContrasena = campo(200);
        panelForm.add(txtContrasena, g);

        // Fila 1 - Rol
        g.gridx = 0; g.gridy = 1;
        panelForm.add(label("ROL:"), g);
        g.gridx = 1;
        cmbRol = new JComboBox<>();
        cmbRol.setPreferredSize(new Dimension(200, 25));
        cmbRol.setBackground(Color.BLACK);
        cmbRol.setForeground(Color.WHITE);
        panelForm.add(cmbRol, g);

        // Fila 2 - Botones
        g.gridx = 0; g.gridy = 2;
        g.gridwidth = 4;
        g.anchor = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBackground(new Color(30, 30, 30));

        btnRegistrar = boton("REGISTRAR", new Color(0, 128, 0));
        btnEditar    = boton("EDITAR", new Color(184, 134, 11));
        btnEliminar  = boton("ELIMINAR", new Color(178, 34, 34));
        btnLimpiar   = boton("LIMPIAR", new Color(80, 80, 80));

        btnRegistrar.addActionListener(e -> registrarUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelForm.add(panelBotones, g);

        add(panelForm, BorderLayout.NORTH);

        // ── Panel inferior: tabla ──
        String[] columnas = {"ID", "Usuario", "Contraseña", "Rol"};
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
     * Carga los roles en el JComboBox.
     */
    private void cargarRoles() {
        cmbRol.removeAllItems();
        String sql = "SELECT id_rol, nombre FROM roles ORDER BY nombre";
        try (java.sql.Connection cn = conexion.ConexionBD.getConexion()) {
            java.sql.PreparedStatement ps = cn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbRol.addItem(new Rol(rs.getInt("id_rol"), rs.getString("nombre")));
            }
        } catch (Exception e) {
            System.out.println("Error al cargar roles: " + e.getMessage());
        }
    }

    /**
     * Carga todos los usuarios en la tabla.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        ArrayList<Usuario> lista = usuarioDAO.listarUsuarios();
        for (Usuario u : lista) {
            modeloTabla.addRow(new Object[]{
                u.getIdUsuario(),
                u.getUsuario(),
                u.getContrasena(),
                u.getRol().getNombre(),
            });
        }
    }

    /**
     * Carga los datos del usuario seleccionado en el formulario.
     */
    private void cargarDatosDesdeTabla() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;
        idUsuarioSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtUsuario.setText((String) modeloTabla.getValueAt(fila, 1));
        txtContrasena.setText((String) modeloTabla.getValueAt(fila, 2));
        String rolNombre = (String) modeloTabla.getValueAt(fila, 3);
        for (int i = 0; i < cmbRol.getItemCount(); i++) {
            if (cmbRol.getItemAt(i).getNombre().equals(rolNombre)) {
                cmbRol.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     */
    private void registrarUsuario() {
        if (!validarCampos()) return;
        Rol rol = (Rol) cmbRol.getSelectedItem();
        Usuario u = new Usuario(0, txtUsuario.getText().trim(),
            txtContrasena.getText().trim(), rol, true, 0);
        if (usuarioDAO.registrarUsuario(u)) {
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar. Verifique que el usuario no esté repetido.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    /**
     * Edita el usuario seleccionado en la tabla.
     */
    private void editarUsuario() {
        if (idUsuarioSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
            return;
        }
        if (!validarCampos()) return;
        Rol rol = (Rol) cmbRol.getSelectedItem();
        Usuario u = new Usuario(idUsuarioSeleccionado, txtUsuario.getText().trim(),
            txtContrasena.getText().trim(), rol, true, 0);
        if (usuarioDAO.actualizarUsuario(u)) {
            JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina lógicamente el usuario seleccionado.
     */
    private void eliminarUsuario() {
        if (idUsuarioSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (usuarioDAO.eliminarUsuario(idUsuarioSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
                limpiarFormulario();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Valida que los campos obligatorios no estén vacíos.
     */
    private boolean validarCampos() {
        if (txtUsuario.getText().trim().isEmpty() ||
            txtContrasena.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario y Contraseña son obligatorios.");
            return false;
        }
        return true;
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtUsuario.setText("");
        txtContrasena.setText("");
        cmbRol.setSelectedIndex(0);
        idUsuarioSeleccionado = -1;
        tabla.clearSelection();
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
