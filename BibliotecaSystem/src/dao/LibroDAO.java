package dao;

import conexion.ConexionBD;
import modelo.Libro;

import java.sql.*;
import java.util.ArrayList;

/**
 * DAO para operaciones CRUD de Libro en la base de datos.
 */
public class LibroDAO {

    /**
     * Obtiene todos los libros activos de la base de datos.
     */
    public ArrayList<Libro> listarLibros() {
        ArrayList<Libro> lista = new ArrayList<>();
        String sql = "SELECT l.id_libro, l.titulo, l.autor, l.isbn, l.editorial, " +
                     "l.anio_publicacion, l.stock_total, l.stock_disponible, " +
                     "l.id_categoria, c.nombre AS categoria, l.estado " +
                     "FROM libros l " +
                     "JOIN categorias c ON l.id_categoria = c.id_categoria " +
                     "WHERE l.estado = TRUE ORDER BY l.titulo";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Libro l = new Libro(
                    rs.getInt("id_libro"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("isbn"),
                    rs.getString("editorial"),
                    rs.getInt("anio_publicacion"),
                    rs.getInt("stock_total"),
                    rs.getInt("stock_disponible"),
                    rs.getInt("id_categoria"),
                    rs.getString("categoria"),
                    rs.getBoolean("estado")
                );
                lista.add(l);
            }
        } catch (Exception e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Registra un nuevo libro en la base de datos.
     */
    public boolean registrarLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, " +
                     "anio_publicacion, stock_total, stock_disponible, id_categoria, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getAnioPublicacion());
            ps.setInt(6, libro.getStockTotal());
            ps.setInt(7, libro.getStockDisponible());
            ps.setInt(8, libro.getIdCategoria());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al registrar libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los datos de un libro existente.
     */
    public boolean actualizarLibro(Libro libro) {
        String sql = "UPDATE libros SET titulo=?, autor=?, isbn=?, editorial=?, " +
                     "anio_publicacion=?, stock_total=?, stock_disponible=?, id_categoria=? " +
                     "WHERE id_libro=?";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getAnioPublicacion());
            ps.setInt(6, libro.getStockTotal());
            ps.setInt(7, libro.getStockDisponible());
            ps.setInt(8, libro.getIdCategoria());
            ps.setInt(9, libro.getIdLibro());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina lógicamente un libro (cambia estado a FALSE).
     */
    public boolean eliminarLibro(int idLibro) {
        String sql = "UPDATE libros SET estado = FALSE WHERE id_libro = ?";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca libros por título o autor.
     */
    public ArrayList<Libro> buscarLibros(String criterio) {
        ArrayList<Libro> lista = new ArrayList<>();
        String sql = "SELECT l.id_libro, l.titulo, l.autor, l.isbn, l.editorial, " +
                     "l.anio_publicacion, l.stock_total, l.stock_disponible, " +
                     "l.id_categoria, c.nombre AS categoria, l.estado " +
                     "FROM libros l " +
                     "JOIN categorias c ON l.id_categoria = c.id_categoria " +
                     "WHERE l.estado = TRUE AND " +
                     "(LOWER(l.titulo) LIKE ? OR LOWER(l.autor) LIKE ?)";
        try {
            Connection cn = ConexionBD.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            String busqueda = "%" + criterio.toLowerCase() + "%";
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Libro l = new Libro(
                    rs.getInt("id_libro"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("isbn"),
                    rs.getString("editorial"),
                    rs.getInt("anio_publicacion"),
                    rs.getInt("stock_total"),
                    rs.getInt("stock_disponible"),
                    rs.getInt("id_categoria"),
                    rs.getString("categoria"),
                    rs.getBoolean("estado")
                );
                lista.add(l);
            }
        } catch (Exception e) {
            System.out.println("Error al buscar libros: " + e.getMessage());
        }
        return lista;
    }
}
