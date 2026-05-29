package dao;

import conexion.ConexionBD;
import modelo.Rol;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO para operaciones de Usuario en la base de datos.
 */
public class UsuarioDAO {

    /**
     * Busca un usuario por nombre de usuario y contraseña para autenticación.
     */
    public Usuario login(String usuario, String contrasena) {
        Usuario u = null;
        String sql = "SELECT u.id_usuario, u.usuario, u.contrasena, u.estado, " +
                     "r.id_rol, r.nombre AS rol " +
                     "FROM usuarios u " +
                     "JOIN roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.usuario = ? AND u.contrasena = ? AND u.estado = TRUE";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Rol rol = new Rol(rs.getInt("id_rol"), rs.getString("rol"));
                u = new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("usuario"),
                    rs.getString("contrasena"),
                    rol,
                    rs.getBoolean("estado")
                );
            }
        } catch (Exception e) {
            System.out.println("Error en login: " + e.getMessage());
        }
        return u;
    }
}
