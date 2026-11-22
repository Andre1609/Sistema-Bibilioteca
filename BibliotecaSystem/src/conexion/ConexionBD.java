package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Proveedor de conexiones a PostgreSQL.
 */
public final class ConexionBD {

    private static final String URL_VARIABLE = "BIBLIOTECA_DB_URL";
    private static final String USER_VARIABLE = "BIBLIOTECA_DB_USER";
    private static final String PASSWORD_VARIABLE = "BIBLIOTECA_DB_PASSWORD";
    private static final Logger LOGGER = Logger.getLogger(ConexionBD.class.getName());

    private ConexionBD() {
    }

    /**
     * Abre una conexión nueva. El llamador debe cerrarla con try-with-resources.
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(
            requiredEnvironmentVariable(URL_VARIABLE),
            requiredEnvironmentVariable(USER_VARIABLE),
            requiredEnvironmentVariable(PASSWORD_VARIABLE)
        );
    }

    private static String requiredEnvironmentVariable(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            throw new SQLException("Debe configurar la variable de entorno " + name + ".");
        }
        return value;
    }

    /**
     * @deprecated Cada llamador debe cerrar su conexión directamente.
     */
    @Deprecated
    public static void cerrarConexion() {
        LOGGER.warning("cerrarConexion() ya no cierra conexiones; cierre cada conexión en su llamador.");
    }
}
