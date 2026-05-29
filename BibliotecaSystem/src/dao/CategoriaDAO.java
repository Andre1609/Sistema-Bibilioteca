package dao;

import conexion.ConexionBD;
import modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO para operaciones de Categoria en la base de datos.
 */
public class CategoriaDAO {

    /**
     * Obtiene todas las categorías de la base de datos.
     */
    public ArrayList<Categoria> listarCategorias() {
        ArrayList<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre FROM categorias ORDER BY nombre";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }
        return lista;
    }
}
