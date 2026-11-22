package vista;

import java.awt.Color;
import java.awt.Font;

/**
 * Paleta y tipografia compartidas por las pantallas Swing.
 */
final class UITheme {
    static final Color NAVY = new Color(17, 29, 49);
    static final Color NAV_ACTIVE = new Color(34, 51, 77);
    static final Color NAV_HOVER = new Color(29, 45, 70);
    static final Color NAV_LABEL = new Color(178, 193, 214);
    static final Color NAV_SECONDARY = new Color(199, 210, 225);
    static final Color NAV_AXIS = new Color(86, 105, 132);
    static final Color NAV_MARKER = new Color(239, 243, 248);
    static final Color NAV_DIVIDER = new Color(48, 65, 89);
    static final Color INK = new Color(22, 36, 58);
    static final Color CANVAS = new Color(244, 242, 237);
    static final Color SURFACE = Color.WHITE;
    static final Color MUTED = new Color(86, 101, 121);
    static final Color BORDER = new Color(226, 229, 233);
    static final Color AMBER = new Color(224, 180, 79);
    static final Color AMBER_TEXT = new Color(117, 81, 0);
    static final Color OVERDUE = new Color(209, 86, 86);
    static final Color OVERDUE_TEXT = new Color(155, 48, 48);
    static final Color ERROR_SURFACE = new Color(253, 240, 237);
    static final Color ERROR_BORDER = new Color(231, 184, 175);
    static final Color ERROR_TEXT = new Color(151, 48, 39);
    static final Color SELECTION = new Color(247, 239, 220);
    static final Color PALE = new Color(245, 242, 234);
    static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font LABEL = new Font("Segoe UI", Font.BOLD, 11);
    static final Font HEADING = new Font("Segoe UI", Font.BOLD, 28);
    static final Font SUBHEADING = new Font("Segoe UI", Font.BOLD, 18);
    static final Font METRIC = new Font("Segoe UI", Font.BOLD, 30);

    private UITheme() {
    }
}
