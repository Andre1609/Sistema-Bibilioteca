package modelo;

/**
 * Modelo que representa un usuario del sistema.
 */
public class Usuario {
    private int idUsuario;
    private String usuario;
    private String contrasena;
    private Rol rol;
    private boolean estado;
    private int idEstudiante;
    

    public Usuario(int idUsuario, String usuario, String contrasena, Rol rol, boolean estado, int idEstudiante) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado;
        this.idEstudiante = idEstudiante;
    }
    
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public int getIdUsuario() { return idUsuario; }
    public String getUsuario() { return usuario; }
    public String getContrasena() { return contrasena; }
    public Rol getRol() { return rol; }
    public boolean isEstado() { return estado; }

    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setRol(Rol rol) { this.rol = rol; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
