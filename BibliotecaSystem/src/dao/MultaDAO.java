package dao;

import conexion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO para operaciones de Multa en la base de datos.
 */
public class MultaDAO {

    /**
     * Genera una multa para un préstamo vencido.
     */
    public boolean generarMulta(int idPrestamo, double monto, String motivo) {
        // Verificar si ya existe multa para ese préstamo
        if (existeMulta(idPrestamo)) return false;
        String sql = "INSERT INTO multas (id_prestamo, monto, motivo, pagada) VALUES (?,?,?,FALSE)";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idPrestamo);
            ps.setDouble(2, monto);
            ps.setString(3, motivo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al generar multa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si ya existe una multa para el préstamo dado.
     */
    public boolean existeMulta(int idPrestamo) {
        String sql = "SELECT id_multa FROM multas WHERE id_prestamo = ?";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idPrestamo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error al verificar multa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Marca una multa como pagada.
     */
    public boolean pagarMulta(int idMulta) {
        String sql = "UPDATE multas SET pagada = TRUE WHERE id_multa = ?";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idMulta);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al pagar multa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todas las multas con datos del estudiante y préstamo.
     */
    public ArrayList<Object[]> listarMultas() {
        ArrayList<Object[]> lista = new ArrayList<>();
        String sql = "SELECT m.id_multa, e.nombres || ' ' || e.apellidos AS estudiante, " +
                     "e.codigo, p.fecha_prestamo, p.fecha_vencimiento, " +
                     "m.monto, m.motivo, m.pagada " +
                     "FROM multas m " +
                     "JOIN prestamos p ON m.id_prestamo = p.id_prestamo " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "ORDER BY m.pagada ASC, p.fecha_vencimiento ASC";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	java.time.format.DateTimeFormatter fmt = 
            		    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            		lista.add(new Object[]{
            		    rs.getInt("id_multa"),
            		    rs.getString("estudiante"),
            		    rs.getString("codigo"),
            		    rs.getDate("fecha_prestamo").toLocalDate().format(fmt),
            		    rs.getDate("fecha_vencimiento").toLocalDate().format(fmt),
            		    rs.getDouble("monto"),
            		    rs.getString("motivo"),
            		    rs.getBoolean("pagada") ? "PAGADA" : "PENDIENTE"
            		});
            }
        } catch (Exception e) {
            System.out.println("Error al listar multas: " + e.getMessage());
        }
        return lista;
    }
}
