package modelo;

import java.sql.Date;

/**
 * Modelo que representa un préstamo del sistema.
 */
public class Prestamo {
    private int idPrestamo;
    private Date fechaPrestamo;
    private Date fechaVencimiento;
    private String estado;
    private int idUsuario;
    private int idEstudiante;
    private String nombreEstudiante;
    private String codigoEstudiante;

    public Prestamo() {}

    public Prestamo(int idPrestamo, Date fechaPrestamo, Date fechaVencimiento,
                    String estado, int idUsuario, int idEstudiante,
                    String nombreEstudiante, String codigoEstudiante) {
        this.idPrestamo = idPrestamo;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.idUsuario = idUsuario;
        this.idEstudiante = idEstudiante;
        this.nombreEstudiante = nombreEstudiante;
        this.codigoEstudiante = codigoEstudiante;
    }

    public int getIdPrestamo() { return idPrestamo; }
    public Date getFechaPrestamo() { return fechaPrestamo; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public String getEstado() { return estado; }
    public int getIdUsuario() { return idUsuario; }
    public int getIdEstudiante() { return idEstudiante; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public String getCodigoEstudiante() { return codigoEstudiante; }

    public void setIdPrestamo(int idPrestamo) { this.idPrestamo = idPrestamo; }
    public void setFechaPrestamo(Date fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }
    public void setCodigoEstudiante(String codigoEstudiante) { this.codigoEstudiante = codigoEstudiante; }
}
