package dao;

import conexion.ConexionBD;
import modelo.Prestamo;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO para operaciones de Prestamo en la base de datos.
 */
public class PrestamoDAO {

    /**
     * Registra un nuevo préstamo en la base de datos.
     * Retorna el id generado para luego insertar el detalle.
     */
    public int registrarPrestamo(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (fecha_prestamo, fecha_vencimiento, " +
                     "estado, id_usuario, id_estudiante) VALUES (?,?,?,?,?) RETURNING id_prestamo";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setDate(1, prestamo.getFechaPrestamo());
            ps.setDate(2, prestamo.getFechaVencimiento());
            ps.setString(3, "ACTIVO");
            ps.setInt(4, prestamo.getIdUsuario());
            ps.setInt(5, prestamo.getIdEstudiante());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_prestamo");
        } catch (Exception e) {
            System.out.println("Error al registrar préstamo: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Registra el detalle del préstamo (libro asociado).
     */
    public boolean registrarDetalle(int idPrestamo, int idLibro, int cantidad) {
        String sql = "INSERT INTO detalle_prestamo (id_prestamo, id_libro, cantidad) VALUES (?,?,?)";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idPrestamo);
            ps.setInt(2, idLibro);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al registrar detalle: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reduce el stock disponible del libro al registrar un préstamo.
     */
    public boolean reducirStock(int idLibro, int cantidad) {
        String sql = "UPDATE libros SET stock_disponible = stock_disponible - ? " +
                     "WHERE id_libro = ? AND stock_disponible >= ?";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, cantidad);
            ps.setInt(2, idLibro);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al reducir stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Aumenta el stock disponible del libro al registrar una devolución.
     */
    public boolean aumentarStock(int idLibro, int cantidad) {
        String sql = "UPDATE libros SET stock_disponible = stock_disponible + ? WHERE id_libro = ?";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, cantidad);
            ps.setInt(2, idLibro);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al aumentar stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todos los préstamos activos con datos del estudiante.
     */
    public ArrayList<Prestamo> listarPrestamosActivos() {
        ArrayList<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.id_prestamo, p.fecha_prestamo, p.fecha_vencimiento, " +
                     "p.estado, p.id_usuario, p.id_estudiante, " +
                     "e.nombres || ' ' || e.apellidos AS nombre_estudiante, " +
                     "e.codigo AS codigo_estudiante " +
                     "FROM prestamos p " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "WHERE p.estado = 'ACTIVO' ORDER BY p.fecha_prestamo DESC";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Prestamo(
                    rs.getInt("id_prestamo"),
                    rs.getDate("fecha_prestamo"),
                    rs.getDate("fecha_vencimiento"),
                    rs.getString("estado"),
                    rs.getInt("id_usuario"),
                    rs.getInt("id_estudiante"),
                    rs.getString("nombre_estudiante"),
                    rs.getString("codigo_estudiante")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al listar préstamos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todo el historial de préstamos.
     */
    public ArrayList<Prestamo> listarHistorial() {
        ArrayList<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.id_prestamo, p.fecha_prestamo, p.fecha_vencimiento, " +
                     "p.estado, p.id_usuario, p.id_estudiante, " +
                     "e.nombres || ' ' || e.apellidos AS nombre_estudiante, " +
                     "e.codigo AS codigo_estudiante " +
                     "FROM prestamos p " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "ORDER BY p.fecha_prestamo DESC";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Prestamo(
                    rs.getInt("id_prestamo"),
                    rs.getDate("fecha_prestamo"),
                    rs.getDate("fecha_vencimiento"),
                    rs.getString("estado"),
                    rs.getInt("id_usuario"),
                    rs.getInt("id_estudiante"),
                    rs.getString("nombre_estudiante"),
                    rs.getString("codigo_estudiante")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al listar historial: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Cambia el estado del préstamo a DEVUELTO.
     */
    public boolean registrarDevolucion(int idPrestamo) {
        String sql = "UPDATE prestamos SET estado = 'DEVUELTO' WHERE id_prestamo = ?";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idPrestamo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al registrar devolución: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el id del libro asociado a un préstamo.
     */
    public int obtenerIdLibroDePrestamo(int idPrestamo) {
        String sql = "SELECT id_libro FROM detalle_prestamo WHERE id_prestamo = ?";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idPrestamo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_libro");
        } catch (Exception e) {
            System.out.println("Error al obtener libro del préstamo: " + e.getMessage());
        }
        return -1;
    }
    /**
     * Lista los préstamos vencidos (fecha vencimiento menor a hoy y estado ACTIVO).
     */
    public ArrayList<Prestamo> listarPrestamosVencidos() {
        ArrayList<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.id_prestamo, p.fecha_prestamo, p.fecha_vencimiento, " +
                     "p.estado, p.id_usuario, p.id_estudiante, " +
                     "e.nombres || ' ' || e.apellidos AS nombre_estudiante, " +
                     "e.codigo AS codigo_estudiante " +
                     "FROM prestamos p " +
                     "JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "WHERE p.estado = 'ACTIVO' AND p.fecha_vencimiento < CURRENT_DATE " +
                     "ORDER BY p.fecha_vencimiento ASC";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Prestamo(
                    rs.getInt("id_prestamo"),
                    rs.getDate("fecha_prestamo"),
                    rs.getDate("fecha_vencimiento"),
                    rs.getString("estado"),
                    rs.getInt("id_usuario"),
                    rs.getInt("id_estudiante"),
                    rs.getString("nombre_estudiante"),
                    rs.getString("codigo_estudiante")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al listar vencidos: " + e.getMessage());
        }
        return lista;
    }
}
