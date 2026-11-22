package vista;

import dao.ResumenBibliotecaDAO;
import modelo.Prestamo;
import modelo.ResumenBiblioteca;
import modelo.Usuario;
import util.SesionActual;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Ventana principal con navegacion por rol y resumen de circulacion.
 */
public class PanelPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PanelPrincipal.class.getName());
    private static final String PAGE_DASHBOARD = "dashboard";
    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es-PE"));

    private final CardLayout pageLayout = new CardLayout();
    private final ScrollableCards pages = new ScrollableCards(pageLayout);
    private final List<NavButton> navButtons = new ArrayList<>();
    private final ResumenBibliotecaDAO resumenDAO = new ResumenBibliotecaDAO();
    private final List<MetricCard> metricCards = new ArrayList<>();
    private final DefaultTableModel dueTableModel = new DefaultTableModel(
        new Object[] {"Id", "Estudiante", "Fecha limite", "Estado"}, 0
    ) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JLabel breadcrumb;
    private JLabel accountLabel;
    private JLabel loadStatus;
    private JButton retryButton;
    private AtlasPanel atlasPanel;
    private JTable dueTable;
    private TableRowSorter<DefaultTableModel> dueTableSorter;
    private boolean dashboardCreated;
    private JPanel dashboardErrorPanel;
    private JComponent dashboardPage;
    private SwingWorker<ResumenBiblioteca, Void> dashboardWorker;

    public PanelPrincipal() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int contentWidth = Math.min(1440, Math.max(960, screen.width - 96));
        int contentHeight = Math.min(900, Math.max(620, screen.height - 140));
        setMinimumSize(new Dimension(
            Math.min(1024, contentWidth + 16),
            Math.min(700, contentHeight + 39)
        ));
        setSize(contentWidth + 16, contentHeight + 39);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.CANVAS);
        root.add(createSidebar(), BorderLayout.WEST);

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(UITheme.CANVAS);
        workspace.add(createTopbar(), BorderLayout.NORTH);

        pages.setBackground(UITheme.CANVAS);
        JScrollPane pageScroll = new JScrollPane(pages);
        pageScroll.setBorder(BorderFactory.createEmptyBorder());
        pageScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pageScroll.getViewport().setBackground(UITheme.CANVAS);
        pageScroll.getVerticalScrollBar().setUnitIncrement(18);
        workspace.add(pageScroll, BorderLayout.CENTER);

        root.add(workspace, BorderLayout.CENTER);
        setContentPane(root);

        List<NavItem> navigation = createNavigation();
        JPanel sidebar = (JPanel) root.getComponent(0);
        addNavigation(sidebar, navigation);

        NavItem initialPage = navigation.get(0);
        JComponent page = initialPage.pageFactory().get();
        page.setName(initialPage.id());
        pages.add(page, initialPage.id());
        setSelectedNavigation(initialPage.id());
        updateTopbar(initialPage.label());
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UITheme.NAVY);
        sidebar.setPreferredSize(new Dimension(256, 0));

        JPanel logoField = new JPanel(new BorderLayout());
        logoField.setBackground(UITheme.SURFACE);
        logoField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        logoField.setPreferredSize(new Dimension(154, 46));

        URL logoUrl = getClass().getResource("/plates/utp-logo.png");
        if (logoUrl == null) {
            LOGGER.warning("No se encontro el recurso /plates/utp-logo.png.");
        } else {
            Image image = new ImageIcon(logoUrl).getImage()
                .getScaledInstance(139, 36, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(image), SwingConstants.CENTER);
            logoField.add(logo, BorderLayout.CENTER);
        }

        JPanel logoPosition = new JPanel(new BorderLayout());
        logoPosition.setOpaque(false);
        logoPosition.setBorder(BorderFactory.createEmptyBorder(20, 28, 0, 74));
        logoPosition.add(logoField, BorderLayout.NORTH);
        sidebar.add(logoPosition, BorderLayout.NORTH);

        JPanel navigationRegion = new JPanel(new BorderLayout());
        navigationRegion.setOpaque(false);
        navigationRegion.setBorder(BorderFactory.createEmptyBorder(24, 16, 0, 16));
        JLabel sectionLabel = new JLabel("BIBLIOTECA");
        sectionLabel.setForeground(UITheme.NAV_LABEL);
        sectionLabel.setFont(UITheme.LABEL);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 14, 0));
        navigationRegion.add(sectionLabel, BorderLayout.NORTH);

        JPanel navigationButtons = new JPanel();
        navigationButtons.setOpaque(false);
        navigationButtons.setLayout(new BoxLayout(navigationButtons, BoxLayout.Y_AXIS));
        navigationRegion.add(navigationButtons, BorderLayout.CENTER);
        sidebar.add(navigationRegion, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 24, 26, 24));
        JSeparator divider = new JSeparator();
        divider.setForeground(UITheme.NAV_DIVIDER);
        divider.setBackground(UITheme.NAV_DIVIDER);
        footer.add(divider);
        footer.add(Box.createVerticalStrut(18));
        JLabel brand = new JLabel("UTP | Gestión bibliotecaria");
        brand.setForeground(UITheme.NAV_LABEL);
        brand.setFont(UITheme.SMALL);
        footer.add(brand);
        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(UITheme.SURFACE);
        topbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(0, 32, 0, 32)
        ));
        topbar.setPreferredSize(new Dimension(0, 74));

        breadcrumb = new JLabel("Biblioteca / Inicio");
        breadcrumb.setForeground(UITheme.MUTED);
        breadcrumb.setFont(UITheme.SMALL);
        topbar.add(breadcrumb, BorderLayout.WEST);

        accountLabel = new JLabel();
        accountLabel.setForeground(UITheme.INK);
        accountLabel.setFont(UITheme.SMALL);
        topbar.add(accountLabel, BorderLayout.EAST);
        updateAccountLabel();
        return topbar;
    }

    private List<NavItem> createNavigation() {
        String role = SesionActual.getRolActual();
        String normalizedRole = role == null ? "" : role.toLowerCase(Locale.ROOT);
        List<NavItem> items = new ArrayList<>();

        if (normalizedRole.contains("estudiante")) {
            items.add(new NavItem("portal", "Portal estudiante", VistaEstudiante::new));
            return items;
        }

        items.add(new NavItem(PAGE_DASHBOARD, "Inicio", this::createDashboard));
        items.add(new NavItem("circulation", "Circulación", this::createCirculationPage));
        items.add(new NavItem("books", "Libros", GestionLibros::new));
        items.add(new NavItem("students", "Estudiantes", GestionEstudiantes::new));
        items.add(new NavItem("fines", "Multas", GestionMultas::new));
        items.add(new NavItem("reports", "Reportes", Reportes::new));
        if (normalizedRole.contains("admin")) {
            items.add(new NavItem("users", "Usuarios", GestionUsuarios::new));
        }
        return items;
    }

    private void addNavigation(JPanel sidebar, List<NavItem> navigation) {
        JPanel navigationRegion = (JPanel) sidebar.getComponent(1);
        JPanel navigationButtons = (JPanel) navigationRegion.getComponent(1);
        for (NavItem item : navigation) {
            NavButton button = new NavButton(item.id(), item.label());
            button.addActionListener(event -> showPage(item));
            navButtons.add(button);
            navigationButtons.add(button);
            navigationButtons.add(Box.createVerticalStrut(4));
        }
    }

    private void showPage(NavItem item) {
        Component[] existingPages = pages.getComponents();
        boolean pageExists = false;
        for (Component page : existingPages) {
            if (item.id().equals(page.getName())) {
                pageExists = true;
                break;
            }
        }
        if (!pageExists) {
            JComponent page = item.pageFactory().get();
            page.setName(item.id());
            pages.add(page, item.id());
        }

        pageLayout.show(pages, item.id());
        setSelectedNavigation(item.id());
        updateTopbar(item.label());
        if (PAGE_DASHBOARD.equals(item.id())) {
            refreshDashboard();
        }
    }

    private void setSelectedNavigation(String id) {
        for (NavButton button : navButtons) {
            button.setCurrentPage(button.pageId().equals(id));
        }
    }

    private void updateTopbar(String label) {
        breadcrumb.setText("Biblioteca / " + label);
        updateAccountLabel();
    }

    private void updateAccountLabel() {
        Usuario user = SesionActual.getUsuario();
        String username = user == null ? "Biblioteca" : user.getUsuario();
        String role = user == null || user.getRol() == null ? "" : user.getRol().getNombre();
        accountLabel.setText(role.isBlank() ? username : username + "  |  " + role);
    }

    private JComponent createDashboard() {
        if (dashboardCreated) {
            return dashboardPage;
        }
        dashboardCreated = true;

        DashboardPanel dashboard = new DashboardPanel();
        dashboard.setBorder(BorderFactory.createEmptyBorder(30, 42, 74, 50));
        dashboard.setPreferredSize(new Dimension(1080, 826));

        JPanel heading = new JPanel(new BorderLayout(24, 0));
        heading.setOpaque(false);
        JPanel headingText = new JPanel(new BorderLayout());
        headingText.setOpaque(false);
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        JLabel title = new JLabel("Buen día, equipo de biblioteca");
        title.setForeground(UITheme.INK);
        title.setFont(UITheme.HEADING);
        headingText.add(title, BorderLayout.NORTH);
        JLabel description = new JLabel("Seguimiento de circulación y vencimientos");
        description.setForeground(UITheme.MUTED);
        description.setFont(UITheme.BODY);
        description.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        headingText.add(description, BorderLayout.CENTER);
        heading.add(headingText, BorderLayout.CENTER);
        JLabel date = new JLabel(LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.forLanguageTag("es-PE"))
        ).toUpperCase(Locale.ROOT));
        date.setForeground(UITheme.INK);
        date.setFont(UITheme.LABEL);
        date.setHorizontalAlignment(SwingConstants.CENTER);
        date.setPreferredSize(new Dimension(192, 44));
        date.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        JPanel datePosition = new JPanel(new BorderLayout());
        datePosition.setOpaque(false);
        datePosition.add(date, BorderLayout.NORTH);
        heading.add(datePosition, BorderLayout.EAST);
        dashboard.add(heading);
        dashboard.add(Box.createVerticalStrut(34));

        JPanel metrics = new JPanel(new GridLayout(1, 4, 14, 0));
        metrics.setOpaque(false);
        metrics.setPreferredSize(new Dimension(1080, 112));
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));
        metrics.add(addMetric("DISPONIBLES", "—", "Ejemplares en catálogo", UITheme.AMBER));
        metrics.add(addMetric("ACTIVOS", "—", "Préstamos vigentes", UITheme.AMBER));
        metrics.add(addMetric("VENCIDOS", "—", "Requieren seguimiento", UITheme.OVERDUE));
        metrics.add(addMetric("MULTAS", "—", "Pendientes de pago", UITheme.AMBER));
        dashboard.add(metrics);
        dashboard.add(Box.createVerticalStrut(22));

        JPanel visualizations = new JPanel(new GridBagLayout());
        visualizations.setOpaque(false);
        visualizations.setPreferredSize(new Dimension(1080, 300));
        visualizations.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 0, 0, 14);
        constraints.weightx = 1.62;
        constraints.weighty = 1;
        atlasPanel = new AtlasPanel();
        visualizations.add(atlasPanel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        visualizations.add(createDuePanel(), constraints);
        dashboard.add(visualizations);
        dashboard.add(Box.createVerticalStrut(16));
        dashboard.add(Box.createVerticalGlue());

        loadStatus = new JLabel("Consultando datos de la biblioteca...");
        loadStatus.setForeground(UITheme.MUTED);
        loadStatus.setFont(UITheme.SMALL);
        loadStatus.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        loadStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboard.add(loadStatus);

        JPanel errorActions = new JPanel(new BorderLayout(12, 0));
        errorActions.setBackground(UITheme.ERROR_SURFACE);
        errorActions.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.ERROR_BORDER, 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        errorActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        JLabel errorMessage = new JLabel(
            "<html>No se pudo cargar el resumen. Verifica la conexión y la configuración " +
            "de PostgreSQL; luego vuelve a intentarlo.</html>"
        );
        errorMessage.setForeground(UITheme.ERROR_TEXT);
        errorMessage.setFont(UITheme.SMALL);
        errorActions.add(errorMessage, BorderLayout.CENTER);
        retryButton = new JButton("Reintentar");
        retryButton.setFont(UITheme.SMALL);
        retryButton.setForeground(UITheme.INK);
        retryButton.setBackground(UITheme.SURFACE);
        retryButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.ERROR_BORDER),
            BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        retryButton.setFocusPainted(true);
        retryButton.addActionListener(event -> refreshDashboard());
        errorActions.add(retryButton, BorderLayout.EAST);
        errorActions.setVisible(false);
        dashboardErrorPanel = errorActions;
        dashboard.add(errorActions);

        refreshDashboard();
        dashboardPage = dashboard;
        return dashboard;
    }

    private MetricCard addMetric(String label, String value, String detail, Color markerColor) {
        MetricCard card = new MetricCard(label, value, detail, markerColor);
        metricCards.add(card);
        return card;
    }

    private JPanel createDuePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));
        panel.getAccessibleContext().setAccessibleName("Próximos vencimientos");

        JLabel title = new JLabel("Próximos vencimientos");
        title.setForeground(UITheme.INK);
        title.setFont(UITheme.SUBHEADING);
        panel.add(title, BorderLayout.NORTH);

        dueTable = new JTable(dueTableModel);
        dueTable.setFont(UITheme.SMALL);
        dueTable.setForeground(UITheme.INK);
        dueTable.setBackground(UITheme.SURFACE);
        dueTable.setSelectionBackground(UITheme.SELECTION);
        dueTable.setSelectionForeground(UITheme.INK);
        dueTable.setGridColor(UITheme.BORDER);
        dueTable.setShowVerticalLines(false);
        dueTable.setRowHeight(32);
        dueTable.setFillsViewportHeight(true);
        dueTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        dueTable.setAutoCreateRowSorter(false);
        dueTable.getTableHeader().setReorderingAllowed(false);
        dueTable.getTableHeader().setBackground(UITheme.SURFACE);
        dueTable.getTableHeader().setForeground(UITheme.MUTED);
        dueTable.getTableHeader().setFont(UITheme.LABEL);
        dueTable.getTableHeader().setPreferredSize(new Dimension(0, 32));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        headerRenderer.setForeground(UITheme.MUTED);
        headerRenderer.setBackground(UITheme.SURFACE);
        headerRenderer.setFont(UITheme.LABEL);
        headerRenderer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        dueTable.getTableHeader().setDefaultRenderer(headerRenderer);
        dueTable.setDefaultRenderer(Object.class, new DueCellRenderer());
        dueTableSorter = new TableRowSorter<>(dueTableModel);
        for (int column = 0; column < dueTableModel.getColumnCount(); column++) {
            dueTableSorter.setSortable(column, false);
        }
        dueTable.setRowSorter(dueTableSorter);
        dueTable.removeColumn(dueTable.getColumnModel().getColumn(0));
        dueTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        dueTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        dueTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        dueTable.getColumnModel().getColumn(0).setMinWidth(90);
        dueTable.getColumnModel().getColumn(1).setMinWidth(64);
        dueTable.getColumnModel().getColumn(2).setMinWidth(78);
        atlasPanel.setAssociatedTable(dueTable);

        JScrollPane tableScroll = new JScrollPane(dueTable);
        tableScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));
        tableScroll.getViewport().setBackground(UITheme.SURFACE);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JTextField search = new JTextField();
        search.setFont(UITheme.SMALL);
        search.setForeground(UITheme.INK);
        search.setToolTipText("Buscar por estudiante, fecha o estado");
        search.getAccessibleContext().setAccessibleName("Buscar vencimientos");
        search.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(7, 9, 7, 9)
        ));
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void applyFilter() {
                String query = search.getText().trim();
                dueTableSorter.setRowFilter(query.isEmpty()
                    ? null
                    : RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent event) {
                applyFilter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent event) {
                applyFilter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent event) {
                applyFilter();
            }
        });

        JLabel searchLabel = new JLabel("Buscar");
        searchLabel.setForeground(UITheme.MUTED);
        searchLabel.setFont(UITheme.SMALL);
        searchLabel.setLabelFor(search);
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchLabel, BorderLayout.WEST);
        searchRow.add(search, BorderLayout.CENTER);

        JPanel tableContent = new JPanel(new BorderLayout(0, 8));
        tableContent.setOpaque(false);
        tableContent.add(searchRow, BorderLayout.NORTH);
        tableContent.add(tableScroll, BorderLayout.CENTER);
        panel.add(tableContent, BorderLayout.CENTER);
        return panel;
    }

    private JComponent createCirculationPage() {
        javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
        tabs.setFont(UITheme.BODY);
        tabs.addTab("Préstamos", new GestionPrestamos());
        tabs.addTab("Devoluciones", new GestionDevoluciones());
        return tabs;
    }

    private void refreshDashboard() {
        if (atlasPanel == null || dashboardWorker != null && !dashboardWorker.isDone()) {
            return;
        }
        setDashboardLoading();
        retryButton.setEnabled(false);

        dashboardWorker = new SwingWorker<ResumenBiblioteca, Void>() {
            @Override
            protected ResumenBiblioteca doInBackground() throws Exception {
                return resumenDAO.obtenerResumen();
            }

            @Override
            protected void done() {
                dashboardWorker = null;
                retryButton.setEnabled(true);
                try {
                    showSummary(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showLoadError(exception);
                } catch (java.util.concurrent.ExecutionException exception) {
                    showLoadError(exception.getCause());
                }
            }
        };
        dashboardWorker.execute();
    }

    private void setDashboardLoading() {
        for (MetricCard card : metricCards) {
            card.setValue("—");
            card.setDetail("Cargando datos");
        }
        atlasPanel.setLoading();
        loadStatus.setText("Consultando datos de la biblioteca...");
        loadStatus.setForeground(UITheme.MUTED);
        retryButton.setVisible(false);
        dashboardErrorPanel.setVisible(false);
    }

    private void showSummary(ResumenBiblioteca summary) {
        metricCards.get(0).setValue(Integer.toString(summary.ejemplaresDisponibles()));
        metricCards.get(0).setDetail("Ejemplares en catálogo");
        metricCards.get(1).setValue(Integer.toString(summary.prestamosActivos()));
        metricCards.get(1).setDetail("Préstamos vigentes");
        metricCards.get(2).setValue(Integer.toString(summary.prestamosVencidos()));
        metricCards.get(2).setDetail("Requieren seguimiento");
        metricCards.get(3).setValue(Integer.toString(summary.multasPendientes()));
        metricCards.get(3).setDetail("Pendientes de pago");

        dueTableModel.setRowCount(0);
        for (Prestamo loan : summary.proximosVencimientos()) {
            Date sqlDate = loan.getFechaVencimiento();
            LocalDate dueDate = sqlDate == null ? null : sqlDate.toLocalDate();
            dueTableModel.addRow(new Object[] {
                loan.getIdPrestamo(),
                loan.getNombreEstudiante(),
                dueDate == null ? "Sin fecha" : dueDate.format(DATE_FORMAT),
                dueDate == null ? "Sin fecha" : statusFor(dueDate)
            });
        }

        atlasPanel.setLoans(summary.proximosVencimientos());
        loadStatus.setText("Datos actualizados " + LocalDate.now().format(
            DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es-PE"))
        ));
        loadStatus.setForeground(UITheme.MUTED);
        dashboardErrorPanel.setVisible(false);
        pages.revalidate();
        pages.repaint();
    }

    private String statusFor(LocalDate dueDate) {
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        if (daysRemaining < 0) {
            return "Vencido (" + Math.abs(daysRemaining) + " d)";
        }
        if (daysRemaining == 0) {
            return "Vence hoy";
        }
        return "En " + daysRemaining + " d";
    }

    private void showLoadError(Throwable exception) {
        LOGGER.log(Level.SEVERE, "No se pudo cargar el resumen de la biblioteca.", exception);
        for (MetricCard card : metricCards) {
            card.setValue("—");
            card.setDetail("No disponible");
        }
        atlasPanel.setError();
        loadStatus.setText("El resumen no está disponible.");
        loadStatus.setForeground(UITheme.ERROR_TEXT);
        retryButton.setVisible(true);
        dashboardErrorPanel.setVisible(true);
        pages.revalidate();
        pages.repaint();
    }

    private record NavItem(String id, String label, Supplier<JComponent> pageFactory) {}

    private static final class DashboardPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        DashboardPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(UITheme.CANVAS);
        }
    }

    private static final class ScrollableCards extends JPanel implements javax.swing.Scrollable {
        private static final long serialVersionUID = 1L;

        ScrollableCards(CardLayout layout) {
            super(layout);
            setBackground(UITheme.CANVAS);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(1080, 720);
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return Math.max(48, visibleRect.height - 48);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static final class MetricCard extends JPanel {
        private static final long serialVersionUID = 1L;
        private final JLabel value;
        private final JLabel detail;
        private final String labelText;

        MetricCard(String label, String initialValue, String initialDetail, Color markerColor) {
            setOpaque(false);
            setLayout(new BorderLayout(0, 4));
            setBorder(BorderFactory.createEmptyBorder(15, 20, 14, 18));
            labelText = label;
            JLabel title = new JLabel(label);
            title.setForeground(UITheme.MUTED);
            title.setFont(UITheme.LABEL);
            JPanel labelRow = new JPanel(new BorderLayout(8, 0));
            labelRow.setOpaque(false);
            labelRow.add(title, BorderLayout.WEST);
            JComponent marker = new DotMarker(markerColor);
            labelRow.add(marker, BorderLayout.EAST);
            add(labelRow, BorderLayout.NORTH);

            value = new JLabel(initialValue);
            value.setForeground(UITheme.INK);
            value.setFont(UITheme.METRIC);
            add(value, BorderLayout.CENTER);
            detail = new JLabel(initialDetail);
            detail.setForeground(UITheme.MUTED);
            detail.setFont(UITheme.SMALL);
            add(detail, BorderLayout.SOUTH);
            getAccessibleContext().setAccessibleName(label + ": " + initialValue);
        }

        void setValue(String text) {
            value.setText(text);
            getAccessibleContext().setAccessibleName(labelText + ": " + text);
        }

        void setDetail(String text) {
            detail.setText(text);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(UITheme.SURFACE);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class AtlasPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final int LEFT = 44;
        private static final int RIGHT = 24;
        private final List<Prestamo> loans = new ArrayList<>();
        private final List<java.awt.Point> markerPositions = new ArrayList<>();
        private JTable associatedTable;
        private AtlasState state = AtlasState.LOADING;

        AtlasPanel() {
            setOpaque(true);
            setBackground(UITheme.NAVY);
            setBorder(BorderFactory.createEmptyBorder(18, 20, 14, 20));
            setPreferredSize(new Dimension(540, 300));
            setMinimumSize(new Dimension(300, 240));
            setFocusable(true);
            getAccessibleContext().setAccessibleName("Atlas de vencimientos");
            getAccessibleContext().setAccessibleDescription(
                "Representa cada prestamo activo segun los dias que faltan para su vencimiento. " +
                "La tabla contigua ofrece los mismos datos de forma accesible."
            );
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    selectNearestLoan(event.getX(), event.getY());
                }
            });
        }

        void setAssociatedTable(JTable table) {
            associatedTable = table;
        }

        void setLoans(List<Prestamo> items) {
            loans.clear();
            loans.addAll(items);
            state = items.isEmpty() ? AtlasState.EMPTY : AtlasState.DATA;
            repaint();
        }

        void setLoading() {
            state = AtlasState.LOADING;
            repaint();
        }

        void setError() {
            state = AtlasState.ERROR;
            repaint();
        }

        private void selectNearestLoan(int x, int y) {
            int nearest = -1;
            double nearestDistance = 13;
            for (int i = 0; i < markerPositions.size(); i++) {
                java.awt.Point point = markerPositions.get(i);
                double distance = point.distance(x, y);
                if (distance < nearestDistance) {
                    nearest = i;
                    nearestDistance = distance;
                }
            }
            if (nearest >= 0 && associatedTable != null) {
                int loanId = loans.get(nearest).getIdPrestamo();
                for (int modelRow = 0; modelRow < associatedTable.getModel().getRowCount(); modelRow++) {
                    if (Integer.valueOf(loanId).equals(associatedTable.getModel().getValueAt(modelRow, 0))) {
                        int viewRow = associatedTable.convertRowIndexToView(modelRow);
                        if (viewRow >= 0) {
                            associatedTable.setRowSelectionInterval(viewRow, viewRow);
                            associatedTable.scrollRectToVisible(associatedTable.getCellRect(viewRow, 0, true));
                            associatedTable.requestFocusInWindow();
                        }
                        break;
                    }
                }
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            g.setColor(Color.WHITE);
            g.setFont(UITheme.SUBHEADING);
            g.drawString("Atlas de vencimientos", 22, 28);
            g.setColor(UITheme.NAV_SECONDARY);
            g.setFont(UITheme.SMALL);
            g.drawString("Préstamos activos por días hasta el vencimiento", 22, 52);

            int axisY = Math.max(142, height - 79);
            int axisStart = LEFT;
            int axisEnd = Math.max(axisStart + 80, width - RIGHT);
            markerPositions.clear();
            if (state != AtlasState.DATA) {
                g.setColor(UITheme.NAV_SECONDARY);
                String message = switch (state) {
                    case LOADING -> "Consultando préstamos...";
                    case ERROR -> "No se pudieron consultar los préstamos.";
                    case EMPTY -> "No hay préstamos activos.";
                    case DATA -> "";
                };
                g.drawString(message, axisStart, 104);
            } else {
                int minDay = -7;
                int maxDay = 30;
                for (Prestamo loan : loans) {
                    if (loan.getFechaVencimiento() != null) {
                        int days = (int) ChronoUnit.DAYS.between(
                            LocalDate.now(), loan.getFechaVencimiento().toLocalDate()
                        );
                        minDay = Math.min(minDay, days);
                        maxDay = Math.max(maxDay, days);
                    }
                }
                if (minDay < -7) {
                    minDay = Math.floorDiv(minDay, 5) * 5;
                }
                if (maxDay > 30) {
                    maxDay = Math.floorDiv(maxDay + 4, 5) * 5;
                }
                List<Integer> ticks = new ArrayList<>();
                ticks.add(minDay);
                for (int tick : new int[] {-7, 0, 5, 10, 15, 20, 30}) {
                    if (tick > minDay && tick < maxDay) {
                        ticks.add(tick);
                    }
                }
                if (ticks.get(ticks.size() - 1) != maxDay) {
                    ticks.add(maxDay);
                }
                g.setColor(UITheme.NAV_AXIS);
                g.drawLine(axisStart, axisY, axisEnd, axisY);
                for (int tick : ticks) {
                    int x = mapDay(tick, axisStart, axisEnd, minDay, maxDay);
                    g.drawLine(x, axisY - 7, x, axisY + 7);
                    g.setFont(UITheme.SMALL);
                    g.setColor(UITheme.NAV_SECONDARY);
                    g.drawString(tick + " d", x - 10, axisY + 24);
                    g.setColor(UITheme.NAV_AXIS);
                }
                for (int i = 0; i < loans.size(); i++) {
                    Prestamo loan = loans.get(i);
                    LocalDate dueDate = loan.getFechaVencimiento() == null
                        ? LocalDate.now()
                        : loan.getFechaVencimiento().toLocalDate();
                    int days = (int) ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
                    int x = mapDay(days, axisStart, axisEnd, minDay, maxDay);
                    int row = i % 2;
                    int y = axisY - 30 - row * 30;
                    markerPositions.add(new java.awt.Point(x, y));
                    Color color = days < 0 ? UITheme.OVERDUE
                        : days <= 7 ? UITheme.AMBER : UITheme.NAV_MARKER;
                    g.setColor(color);
                    if (days < 0) {
                        g.setStroke(new java.awt.BasicStroke(2.2f));
                        g.drawOval(x - 9, y - 9, 18, 18);
                        g.fillOval(x - 4, y - 4, 8, 8);
                    } else {
                        g.fillOval(x - 6, y - 6, 12, 12);
                    }
                }
            }

            g.setFont(UITheme.SMALL);
            int legendY = height - 20;
            g.setColor(UITheme.OVERDUE);
            g.fillOval(22, legendY - 7, 6, 6);
            g.setColor(UITheme.NAV_SECONDARY);
            g.drawString("Vencido", 34, legendY);
            g.setColor(UITheme.AMBER);
            g.fillOval(105, legendY - 7, 6, 6);
            g.setColor(UITheme.NAV_SECONDARY);
            g.drawString("Próximo (7 días)", 117, legendY);
            g.setColor(UITheme.NAV_MARKER);
            g.fillOval(224, legendY - 7, 6, 6);
            g.setColor(UITheme.NAV_SECONDARY);
            g.drawString("Posterior", 236, legendY);
            g.dispose();
        }

        private int mapDay(int day, int axisStart, int axisEnd, int minDay, int maxDay) {
            double position = (day - minDay) / (double) Math.max(1, maxDay - minDay);
            return axisStart + (int) Math.round(position * (axisEnd - axisStart));
        }

        private enum AtlasState {
            LOADING,
            ERROR,
            EMPTY,
            DATA
        }
    }

    private static final class DotMarker extends JComponent {
        private static final long serialVersionUID = 1L;
        private final Color color;

        DotMarker(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(8, 8));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            graphics.setColor(color);
            int diameter = Math.min(getWidth(), getHeight());
            graphics.fillOval(
                (getWidth() - diameter) / 2,
                (getHeight() - diameter) / 2,
                diameter,
                diameter
            );
        }
    }

    private static final class DueCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        DueCellRenderer() {
            setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        }

        @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean selected, boolean focused, int row, int column
        ) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            if (!selected) {
                setBackground(row % 2 == 0 ? UITheme.SURFACE : UITheme.CANVAS);
                setForeground(UITheme.INK);
            }
            if (column == 2 && value != null && value.toString().startsWith("Vencido")) {
                setForeground(UITheme.OVERDUE_TEXT);
            } else if (column == 2 && value != null && !value.toString().equals("Vence hoy")) {
                setForeground(UITheme.AMBER_TEXT);
            }
            return this;
        }
    }

    private static final class NavButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final String pageId;
        private boolean selected;
        private boolean hovered;

        NavButton(String pageId, String label) {
            super(label);
            this.pageId = pageId;
            setFont(UITheme.BODY);
            setForeground(Color.WHITE);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 10));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(220, 46));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            getAccessibleContext().setAccessibleName(label);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    hovered = false;
                    repaint();
                }
            });
        }

        String pageId() {
            return pageId;
        }

        void setCurrentPage(boolean value) {
            selected = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            if (selected) {
                g.setColor(UITheme.NAV_ACTIVE);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g.setColor(UITheme.AMBER);
                g.fillRoundRect(0, 7, 4, getHeight() - 14, 4, 4);
            } else if (hovered) {
                g.setColor(UITheme.NAV_HOVER);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
            g.dispose();
            super.paintComponent(graphics);
            if (hasFocus()) {
                Graphics2D focus = (Graphics2D) graphics.create();
                focus.setColor(UITheme.AMBER);
                focus.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 7, 7);
                focus.dispose();
            }
        }
    }
}
