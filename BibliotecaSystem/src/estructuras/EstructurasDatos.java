package estructuras;
import modelo.Libro;
import modelo.Prestamo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Clase que implementa las estructuras de datos del curso
 * aplicadas al sistema de biblioteca.
 */
public class EstructurasDatos {

    // ── 1. ARRAYLIST — Catálogo de libros en memoria ──
    private ArrayList<Libro> catalogoLibros;

    // ── 2. LINKEDLIST — Historial de navegación entre módulos ──
    private LinkedList<String> historialNavegacion;

    // ── 3. STACK (Pila) — Historial de últimas operaciones realizadas ──
    private Stack<String> pilaOperaciones;

    // ── 4. QUEUE (Cola) — Cola de préstamos pendientes de procesar ──
    private Queue<Prestamo> colaPrestamos;

    public EstructurasDatos() {
        catalogoLibros      = new ArrayList<>();
        historialNavegacion = new LinkedList<>();
        pilaOperaciones     = new Stack<>();
        colaPrestamos       = new LinkedList<>();
    }

    // ════════════════════════════════════════
    // ARRAYLIST — Catálogo de libros
    // ════════════════════════════════════════

    /**
     * Agrega un libro al catálogo en memoria.
     */
    public void agregarLibroCatalogo(Libro libro) {
        catalogoLibros.add(libro);
    }

    /**
     * Busca libros en el catálogo por título usando búsqueda lineal.
     */
    public ArrayList<Libro> buscarEnCatalogo(String titulo) {
        ArrayList<Libro> resultado = new ArrayList<>();
        for (Libro l : catalogoLibros) {
            if (l.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    /**
     * Ordena el catálogo por título usando Bubble Sort.
     */
    public void ordenarCatalogoPorTitulo() {
        int n = catalogoLibros.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (catalogoLibros.get(j).getTitulo()
                        .compareToIgnoreCase(catalogoLibros.get(j + 1).getTitulo()) > 0) {
                    Libro temp = catalogoLibros.get(j);
                    catalogoLibros.set(j, catalogoLibros.get(j + 1));
                    catalogoLibros.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Retorna el catálogo completo.
     */
    public ArrayList<Libro> getCatalogo() {
        return catalogoLibros;
    }

    // ════════════════════════════════════════
    // LINKEDLIST — Historial de navegación
    // ════════════════════════════════════════

    /**
     * Registra el módulo al que navegó el usuario.
     */
    public void registrarNavegacion(String modulo) {
        historialNavegacion.addLast(modulo);
        // Mantener solo los últimos 10 módulos visitados
        if (historialNavegacion.size() > 10) {
            historialNavegacion.removeFirst();
        }
    }

    /**
     * Retorna el último módulo visitado.
     */
    public String ultimoModuloVisitado() {
        if (!historialNavegacion.isEmpty()) {
            return historialNavegacion.getLast();
        }
        return "Ninguno";
    }

    /**
     * Retorna todo el historial de navegación.
     */
    public LinkedList<String> getHistorialNavegacion() {
        return historialNavegacion;
    }

    // ════════════════════════════════════════
    // STACK (Pila) — Historial de operaciones
    // ════════════════════════════════════════

    /**
     * Registra una operación realizada en el sistema.
     * Ejemplo: "REGISTRAR LIBRO - Introducción a Java"
     */
    public void registrarOperacion(String operacion) {
        pilaOperaciones.push(operacion);
    }

    /**
     * Retorna la última operación realizada (sin eliminarla).
     */
    public String ultimaOperacion() {
        if (!pilaOperaciones.isEmpty()) {
            return pilaOperaciones.peek();
        }
        return "Sin operaciones";
    }

    /**
     * Retorna y elimina la última operación (deshacer).
     */
    public String deshacerOperacion() {
        if (!pilaOperaciones.isEmpty()) {
            return pilaOperaciones.pop();
        }
        return "No hay operaciones para deshacer";
    }

    /**
     * Retorna toda la pila de operaciones.
     */
    public Stack<String> getPilaOperaciones() {
        return pilaOperaciones;
    }

    // ════════════════════════════════════════
    // QUEUE (Cola) — Cola de préstamos
    // ════════════════════════════════════════

    /**
     * Agrega un préstamo a la cola de procesamiento.
     */
    public void encolarPrestamo(Prestamo prestamo) {
        colaPrestamos.offer(prestamo);
    }

    /**
     * Procesa y retorna el primer préstamo de la cola.
     */
    public Prestamo procesarPrestamo() {
        return colaPrestamos.poll();
    }

    /**
     * Retorna el primer préstamo sin eliminarlo de la cola.
     */
    public Prestamo verSiguientePrestamo() {
        return colaPrestamos.peek();
    }

    /**
     * Retorna la cantidad de préstamos en cola.
     */
    public int cantidadEnCola() {
        return colaPrestamos.size();
    }

    /**
     * Verifica si la cola está vacía.
     */
    public boolean colaVacia() {
        return colaPrestamos.isEmpty();
    }
}
