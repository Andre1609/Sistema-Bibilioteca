package vista;

import util.SesionActual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pantalla principal del sistema con menú lateral por roles.
 */
public class PanelPrincipal extends JFrame {
	private static final long serialVersionUID = 1L; 

    private JPanel panelMenu;
    private JPanel panelContenido;
    private JLabel lblUsuario;

    public PanelPrincipal() {
        initComponents();
        configurarMenuPorRol();
    }

    /**
     * Inicializa y configura los componentes de la ventana.
     */
    private void initComponents() {
        setTitle("Sistema Biblioteca");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(900, 50));
        header.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));

        lblUsuario = new JLabel("USUARIO: " + SesionActual.getUsuario().getUsuario().toUpperCase()
                + " | ROL: " + SesionActual.getRolActual());
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(lblUsuario);

        // Botón cerrar sesión
        JButton btnCerrar = new JButton("CERRAR SESIÓN");
        btnCerrar.setBackground(Color.BLACK);
        btnCerrar.setForeground(Color.RED);
        btnCerrar.setBorder(BorderFactory.createLineBorder(Color.RED));
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> cerrarSesion());
        header.add(btnCerrar);

        add(header, BorderLayout.NORTH);

        // Panel Menú lateral
        panelMenu = new JPanel();
        panelMenu.setBackground(new Color(20, 20, 20));
        panelMenu.setPreferredSize(new Dimension(200, 550));
        panelMenu.setLayout(new GridLayout(10, 1, 5, 5));

        add(panelMenu, BorderLayout.WEST);

        // Panel Contenido
        panelContenido = new JPanel();
        panelContenido.setBackground(new Color(40, 40, 40));
        panelContenido.setLayout(new BorderLayout());

        JLabel lblBienvenida = new JLabel("PANEL PRINCIPAL", SwingConstants.CENTER);
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 24));
        panelContenido.add(lblBienvenida, BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);
    }

    /**
     * Configura el menú lateral según el rol del usuario logueado.
     */
    private void configurarMenuPorRol() {
        String rol = SesionActual.getRolActual();

        switch (rol) {
            case "ADMINISTRADOR":
                agregarBotonMenu("LIBROS");
                agregarBotonMenu("PRÉSTAMOS");
                agregarBotonMenu("REPORTES");
                agregarBotonMenu("USUARIOS");
                agregarBotonMenu("MULTAS");
                break;

            case "BIBLIOTECARIO":
                agregarBotonMenu("LIBROS");
                agregarBotonMenu("PRÉSTAMOS");
                agregarBotonMenu("DEVOLUCIONES");
                agregarBotonMenu("ESTUDIANTES");
                break;

            case "ESTUDIANTE":
                agregarBotonMenu("CATÁLOGO");
                agregarBotonMenu("MIS PRÉSTAMOS");
                agregarBotonMenu("MIS MULTAS");
                break;

            default:
                break;
        }
    }

    /**
     * Agrega un botón al menú lateral.
     */
    private void agregarBotonMenu(String nombre) {
        JButton btn = new JButton(nombre);
        btn.setBackground(new Color(20, 20, 20));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarNavegacion(nombre);
            }
        });

        panelMenu.add(btn);
    }

    /**
     * Maneja la navegación entre pantallas según el botón del menú.
     */
    private void manejarNavegacion(String opcion) {
        panelContenido.removeAll();
        switch (opcion) {
            case "LIBROS":
                panelContenido.add(new GestionLibros(), BorderLayout.CENTER);
                break;
            default:
                JLabel lblTemp = new JLabel("Módulo: " + opcion, SwingConstants.CENTER);
                lblTemp.setForeground(Color.WHITE);
                lblTemp.setFont(new Font("Arial", Font.BOLD, 20));
                panelContenido.add(lblTemp, BorderLayout.CENTER);
                break;
        }
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /**
     * Cierra la sesión actual y regresa al Login.
     */
    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SesionActual.cerrarSesion();
            new LoginForm().setVisible(true);
            this.dispose();
        }
    }
}