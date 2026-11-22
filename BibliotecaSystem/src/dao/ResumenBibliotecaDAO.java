package dao;

import conexion.ConexionBD;
import modelo.Prestamo;
import modelo.ResumenBiblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Consulta las metricas y los vencimientos que necesita el panel principal.
 */
public class ResumenBibliotecaDAO {

    public ResumenBiblioteca obtenerResumen() throws SQLException {
        String metricasSql =
            "SELECT " +
            "(SELECT COALESCE(SUM(stock_disponible), 0) FROM libros WHERE estado = TRUE) AS disponibles, " +
            "(SELECT COUNT(*) FROM prestamos WHERE estado = 'ACTIVO') AS activos, " +
            "(SELECT COUNT(*) FROM prestamos " +
                "WHERE estado = 'ACTIVO' AND fecha_vencimiento < CURRENT_DATE) AS vencidos, " +
            "(SELECT COUNT(*) FROM multas WHERE pagada = FALSE) AS multas_pendientes";
        String vencimientosSql =
            "SELECT p.id_prestamo, p.fecha_vencimiento, p.estado, p.id_usuario, p.id_estudiante, " +
            "COALESCE(e.nombres || ' ' || e.apellidos, 'Sin estudiante') AS nombre_estudiante, " +
            "COALESCE(e.codigo, '') AS codigo_estudiante " +
            "FROM prestamos p " +
            "LEFT JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
            "WHERE p.estado = 'ACTIVO' " +
            "ORDER BY p.fecha_vencimiento ASC";

        try (Connection connection = ConexionBD.getConexion();
             PreparedStatement metricas = connection.prepareStatement(metricasSql);
             ResultSet resultadoMetricas = metricas.executeQuery();
             PreparedStatement vencimientos = connection.prepareStatement(vencimientosSql);
             ResultSet resultadoVencimientos = vencimientos.executeQuery()) {
            if (!resultadoMetricas.next()) {
                throw new SQLException("La consulta del resumen no devolvio metricas.");
            }

            ArrayList<Prestamo> prestamos = new ArrayList<>();
            while (resultadoVencimientos.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(resultadoVencimientos.getInt("id_prestamo"));
                prestamo.setFechaVencimiento(resultadoVencimientos.getDate("fecha_vencimiento"));
                prestamo.setEstado(resultadoVencimientos.getString("estado"));
                prestamo.setIdUsuario(resultadoVencimientos.getInt("id_usuario"));
                prestamo.setIdEstudiante(resultadoVencimientos.getInt("id_estudiante"));
                prestamo.setNombreEstudiante(resultadoVencimientos.getString("nombre_estudiante"));
                prestamo.setCodigoEstudiante(resultadoVencimientos.getString("codigo_estudiante"));
                prestamos.add(prestamo);
            }

            return new ResumenBiblioteca(
                resultadoMetricas.getInt("disponibles"),
                resultadoMetricas.getInt("activos"),
                resultadoMetricas.getInt("vencidos"),
                resultadoMetricas.getInt("multas_pendientes"),
                prestamos
            );
        }
    }
}
