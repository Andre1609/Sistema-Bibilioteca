package dao;

import conexion.ConexionBD;
import modelo.Estudiante;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO para operaciones CRUD de Estudiante en la base de datos.
 */
public class EstudianteDAO {

    /**
     * Obtiene todos los estudiantes activos de la base de datos.
     */
    public ArrayList<Estudiante> listarEstudiantes() {
        ArrayList<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id_estudiante, codigo, nombres, apellidos, " +
                     "carrera, correo, telefono, estado " +
                     "FROM estudiantes WHERE estado = TRUE ORDER BY apellidos";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Estudiante(
                    rs.getInt("id_estudiante"),
                    rs.getString("codigo"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("carrera"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getBoolean("estado")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al listar estudiantes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Registra un nuevo estudiante en la base de datos.
     */
    public boolean registrarEstudiante(Estudiante e) {
        String sql = "INSERT INTO estudiantes (codigo, nombres, apellidos, " +
                     "carrera, correo, telefono, estado) VALUES (?,?,?,?,?,?,TRUE)";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, e.getCodigo());
            ps.setString(2, e.getNombres());
            ps.setString(3, e.getApellidos());
            ps.setString(4, e.getCarrera());
            ps.setString(5, e.getCorreo());
            ps.setString(6, e.getTelefono());
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            System.out.println("Error al registrar estudiante: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un estudiante existente.
     */
    public boolean actualizarEstudiante(Estudiante e) {
        String sql = "UPDATE estudiantes SET codigo=?, nombres=?, apellidos=?, " +
                     "carrera=?, correo=?, telefono=? WHERE id_estudiante=?";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, e.getCodigo());
            ps.setString(2, e.getNombres());
            ps.setString(3, e.getApellidos());
            ps.setString(4, e.getCarrera());
            ps.setString(5, e.getCorreo());
            ps.setString(6, e.getTelefono());
            ps.setInt(7, e.getIdEstudiante());
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            System.out.println("Error al actualizar estudiante: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Elimina lógicamente un estudiante (cambia estado a FALSE).
     */
    public boolean eliminarEstudiante(int idEstudiante) {
        String sql = "UPDATE estudiantes SET estado = FALSE WHERE id_estudiante = ?";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idEstudiante);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar estudiante: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca estudiantes por nombre, apellido o código.
     */
    public ArrayList<Estudiante> buscarEstudiantes(String criterio) {
        ArrayList<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id_estudiante, codigo, nombres, apellidos, " +
                     "carrera, correo, telefono, estado " +
                     "FROM estudiantes WHERE estado = TRUE AND " +
                     "(LOWER(nombres) LIKE ? OR LOWER(apellidos) LIKE ? OR LOWER(codigo) LIKE ?)";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            String busqueda = "%" + criterio.toLowerCase() + "%";
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Estudiante(
                    rs.getInt("id_estudiante"),
                    rs.getString("codigo"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("carrera"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getBoolean("estado")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar estudiantes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca un estudiante por su código único.
     */
    public Estudiante buscarPorCodigo(String codigo) {
        String sql = "SELECT id_estudiante, codigo, nombres, apellidos, " +
                     "carrera, correo, telefono, estado " +
                     "FROM estudiantes WHERE codigo = ? AND estado = TRUE";
        try (Connection cn = ConexionBD.getConexion()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Estudiante(
                    rs.getInt("id_estudiante"),
                    rs.getString("codigo"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("carrera"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getBoolean("estado")
                );
            }
        } catch (Exception e) {
            System.out.println("Error al buscar por código: " + e.getMessage());
        }
        return null;
    }
}
