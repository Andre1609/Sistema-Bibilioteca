package modelo;
/**
 * Modelo que representa un estudiante del sistema.
 */
public class Estudiante {
    private int idEstudiante;
    private String codigo;
    private String nombres;
    private String apellidos;
    private String carrera;
    private String correo;
    private String telefono;
    private boolean estado;

    public Estudiante() {}

    public Estudiante(int idEstudiante, String codigo, String nombres,
                      String apellidos, String carrera, String correo,
                      String telefono, boolean estado) {
        this.idEstudiante = idEstudiante;
        this.codigo = codigo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.carrera = carrera;
        this.correo = correo;
        this.telefono = telefono;
        this.estado = estado;
    }

    public int getIdEstudiante() { return idEstudiante; }
    public String getCodigo() { return codigo; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getCarrera() { return carrera; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public boolean isEstado() { return estado; }

    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
