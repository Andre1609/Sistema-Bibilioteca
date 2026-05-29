package conexion;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Clase Singleton para manejar la conexión a PostgreSQL.
 */
public class ConexionBD {

    private static final String URL = "jdbc:postgresql://localhost:5432/EstructuraAlgoritmos";
    private static final String USER = "postgres";
    private static final String PASSWORD = "CarlosF9876";

    private static Connection conexion = null;

    /**
     * Retorna la conexión activa o crea una nueva si no existe.
     */
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("org.postgresql.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión exitosa a PostgreSQL");
            }
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return conexion;
    }

    /**
     * Con esto cierro la conexión activa.
     */
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar: " + e.getMessage());
        }
    }
}
