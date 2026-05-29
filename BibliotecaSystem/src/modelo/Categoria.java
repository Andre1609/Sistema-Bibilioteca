package modelo;

/**
 * Modelo que representa una categoría de libro.
 */
public class Categoria {
    private int idCategoria;
    private String nombre;

    public Categoria(int idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
    }

    public int getIdCategoria() { return idCategoria; }
    public String getNombre() { return nombre; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Muestra el nombre en el JComboBox.
     */
    @Override
    public String toString() {
        return nombre;
    }
}