package conexion;

import java.sql.Connection;

/**
 * Cremoas esta clase para probar la conexión a PostgreSQL.
 */
public class TestConexion {
    public static void main(String[] args) throws java.sql.SQLException {
        try (Connection cn = ConexionBD.getConexion()) {
            System.out.println("✓ Conexión exitosa a EstructuraAlgoritmos");
        }
    }
}
