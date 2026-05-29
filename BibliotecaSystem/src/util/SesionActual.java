package util;

import modelo.Usuario;

/**
 * Clase utilitaria para mantener el usuario logueado durante la sesión.
 */
public class SesionActual {
    private static Usuario usuarioActual = null;

    public static void setUsuario(Usuario u) { usuarioActual = u; }
    public static Usuario getUsuario() { return usuarioActual; }
    public static void cerrarSesion() { usuarioActual = null; }

    public static String getRolActual() {
        if (usuarioActual != null) return usuarioActual.getRol().getNombre();
        return "";
    }
}
