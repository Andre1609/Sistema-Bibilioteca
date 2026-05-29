package vista;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.SesionActual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pantalla de inicio de sesión del sistema.
 */
public class LoginForm extends JFrame {
	private static final long serialVersionUID = 1L;

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JLabel lblError;

    public LoginForm() {
        initComponents();
    }

    /**
     * Inicializa y configura los componentes de la ventana.
     */
    private void initComponents() {
        setTitle("Sistema Biblioteca - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(null);
        setContentPane(panel);

        // Título
        JLabel lblTitulo = new JLabel("SISTEMA BIBLIOTECA");
        lblTitulo.setBounds(80, 20, 250, 35);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo);

        // Label Usuario
        JLabel lblUsuario = new JLabel("USUARIO:");
        lblUsuario.setBounds(60, 80, 100, 25);
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lblUsuario);

        // Campo Usuario
        txtUsuario = new JTextField();
        txtUsuario.setBounds(160, 80, 160, 25);
        txtUsuario.setBackground(Color.BLACK);
        txtUsuario.setForeground(Color.WHITE);
        txtUsuario.setCaretColor(Color.WHITE);
        txtUsuario.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.add(txtUsuario);

        // Label Contraseña
        JLabel lblContrasena = new JLabel("CONTRASEÑA:");
        lblContrasena.setBounds(60, 130, 110, 25);
        lblContrasena.setForeground(Color.WHITE);
        lblContrasena.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lblContrasena);

        // Campo Contraseña
        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(160, 130, 160, 25);
        txtContrasena.setBackground(Color.BLACK);
        txtContrasena.setForeground(Color.WHITE);
        txtContrasena.setCaretColor(Color.WHITE);
        txtContrasena.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.add(txtContrasena);

        // Label Error
        lblError = new JLabel("");
        lblError.setBounds(60, 165, 280, 20);
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("Arial", Font.PLAIN, 11));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblError);

        // Botón Ingresar
        btnIngresar = new JButton("INICIAR SESION");
        btnIngresar.setBounds(120, 195, 150, 35);
        btnIngresar.setBackground(Color.BLACK);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(btnIngresar);

        // Acción del botón
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });
    }

    /**
     * Valida las credenciales del usuario contra la base de datos.
     */
    private void autenticar() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            lblError.setText("Complete todos los campos.");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario u = dao.login(usuario, contrasena);

        if (u != null) {
            SesionActual.setUsuario(u);
            lblError.setText("");
            JOptionPane.showMessageDialog(this, 
                "Bienvenido " + u.getUsuario() + " - Rol: " + u.getRol().getNombre());
            // Aquí abriremos el Panel Principal 
            new PanelPrincipal().setVisible(true);
            this.dispose();
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
            txtContrasena.setText("");
        }
    }

    /**
     * Método principal para probar el Login.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
