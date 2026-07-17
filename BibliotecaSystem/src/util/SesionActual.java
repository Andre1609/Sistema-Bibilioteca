package util;

import modelo.Usuario;
import estructuras.EstructurasDatos;

/**
 * Clase utilitaria para mantener el usuario logueado durante la sesión.
 */
public class SesionActual {
    private static Usuario usuarioActual = null;
    private static EstructurasDatos estructuras = new EstructurasDatos();

    public static void setUsuario(Usuario u) { usuarioActual = u; }
    public static Usuario getUsuario() { return usuarioActual; }
    public static void cerrarSesion() { usuarioActual = null; }

    public static String getRolActual() {
        if (usuarioActual != null) return usuarioActual.getRol().getNombre();
        return "";
    }

    public static EstructurasDatos getEstructuras() {
        return estructuras;
    }
}
