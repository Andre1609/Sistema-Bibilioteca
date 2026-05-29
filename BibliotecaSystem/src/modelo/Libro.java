package modelo;

/**
 * Modelo que representa un libro del sistema.
 */
public class Libro {
    private int idLibro;
    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private int anioPublicacion;
    private int stockTotal;
    private int stockDisponible;
    private int idCategoria;
    private String nombreCategoria;
    private boolean estado;

    public Libro() {}

    public Libro(int idLibro, String titulo, String autor, String isbn,
                 String editorial, int anioPublicacion, int stockTotal,
                 int stockDisponible, int idCategoria, String nombreCategoria, boolean estado) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.stockTotal = stockTotal;
        this.stockDisponible = stockDisponible;
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.estado = estado;
    }

    public int getIdLibro() { return idLibro; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }
    public String getEditorial() { return editorial; }
    public int getAnioPublicacion() { return anioPublicacion; }
    public int getStockTotal() { return stockTotal; }
    public int getStockDisponible() { return stockDisponible; }
    public int getIdCategoria() { return idCategoria; }
    public String getNombreCategoria() { return nombreCategoria; }
    public boolean isEstado() { return estado; }

    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }
    public void setStockTotal(int stockTotal) { this.stockTotal = stockTotal; }
    public void setStockDisponible(int stockDisponible) { this.stockDisponible = stockDisponible; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
