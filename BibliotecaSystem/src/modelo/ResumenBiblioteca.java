package modelo;

import java.util.List;

/**
 * Datos reales que el panel principal resume para el personal bibliotecario.
 */
public record ResumenBiblioteca(
    int ejemplaresDisponibles,
    int prestamosActivos,
    int prestamosVencidos,
    int multasPendientes,
    List<Prestamo> proximosVencimientos
) {
    public ResumenBiblioteca {
        proximosVencimientos = List.copyOf(proximosVencimientos);
    }
}
