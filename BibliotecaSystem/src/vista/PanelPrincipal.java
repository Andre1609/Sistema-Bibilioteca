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
        header.setPreferredSize(new Dimension(900, 70));
        header.setLayout(new BorderLayout());

        // Logo UTP
        JLabel lblLogo = new JLabel();
        try {
            java.net.URL imgUrl = getClass().getResource("/recursos/UTP.png");
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage().getScaledInstance(150, 55, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo: " + e.getMessage());
        }
        lblLogo.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        header.add(lblLogo, BorderLayout.WEST);

        // Info usuario
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        panelInfo.setBackground(Color.BLACK);

        lblUsuario = new JLabel("USUARIO: " + SesionActual.getUsuario().getUsuario().toUpperCase()
                + " | ROL: " + SesionActual.getRolActual());
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblUsuario);

        header.add(panelInfo, BorderLayout.CENTER);

        // Botón cerrar sesión
        JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        panelCerrar.setBackground(Color.BLACK);
        JButton btnCerrar = new JButton("CERRAR SESIÓN");
        btnCerrar.setBackground(Color.BLACK);
        btnCerrar.setForeground(Color.RED);
        btnCerrar.setBorder(BorderFactory.createLineBorder(Color.RED));
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> cerrarSesion());
        panelCerrar.add(btnCerrar);
        header.add(panelCerrar, BorderLayout.EAST);

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
        // Registrar en LinkedList el módulo visitado
        SesionActual.getEstructuras().registrarNavegacion(opcion);
        // Registrar en Stack la operación
        SesionActual.getEstructuras().registrarOperacion(
            "NAVEGÓ A: " + opcion + " - Usuario: " + SesionActual.getUsuario().getUsuario()
        );

        panelContenido.removeAll();
        switch (opcion) {
            case "LIBROS":
                panelContenido.add(new GestionLibros(), BorderLayout.CENTER);
                break;
            case "ESTUDIANTES":
                panelContenido.add(new GestionEstudiantes(), BorderLayout.CENTER);
                break;
            case "PRÉSTAMOS":
                panelContenido.add(new GestionPrestamos(), BorderLayout.CENTER);
                break;
            case "DEVOLUCIONES":
                panelContenido.add(new GestionDevoluciones(), BorderLayout.CENTER);
                break;
            case "USUARIOS":
                panelContenido.add(new GestionUsuarios(), BorderLayout.CENTER);
                break;
            case "MULTAS":
                panelContenido.add(new GestionMultas(), BorderLayout.CENTER);
                break;
            case "REPORTES":
                panelContenido.add(new Reportes(), BorderLayout.CENTER);
                break;
            case "CATÁLOGO":
                panelContenido.add(new VistaEstudiante(), BorderLayout.CENTER);
                break;
            case "MIS PRÉSTAMOS":
                panelContenido.add(new VistaEstudiante(), BorderLayout.CENTER);
                break;
            case "MIS MULTAS":
                panelContenido.add(new VistaEstudiante(), BorderLayout.CENTER);
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