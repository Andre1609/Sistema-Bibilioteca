package conexion;

import java.sql.Connection;

/**
 * Cremoas esta clase para probar la conexión a PostgreSQL.
 */
public class TestConexion {
    public static void main(String[] args) {
        Connection cn = ConexionBD.getConexion();
        if (cn != null) {
            System.out.println("✓ Conexión exitosa a EstructuraAlgoritmos");
            ConexionBD.cerrarConexion();
        } else {
            System.out.println("✗ Error al conectar");
        }
    }
}
