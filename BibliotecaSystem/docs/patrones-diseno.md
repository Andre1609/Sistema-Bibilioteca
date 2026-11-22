# Patrones y estructuras de datos

Este documento distingue lo que ejecuta una ruta de la aplicación de lo que
todavía es una demostración académica. No se atribuyen patrones que el código
no usa.

## Patrones en rutas funcionales

| Patrón o decisión | Problema que resuelve | Archivos e interacción | Evidencia y límite |
|---|---|---|---|
| DAO | Evita que el nuevo panel construya consultas JDBC en la vista. | [ResumenBibliotecaDAO](../src/dao/ResumenBibliotecaDAO.java) consulta métricas y préstamos; [PanelPrincipal](../src/vista/PanelPrincipal.java) presenta el resultado y carga las vistas existentes a través de sus DAOs. | La ruta del panel compila y muestra un error recuperable si la conexión falla. La consulta PostgreSQL debe verificarse con el driver y las variables configurados en [configuracion.md](./configuracion.md). |
| Proveedor de conexiones (sin Singleton) | Evita compartir una conexión JDBC mutable entre operaciones y permite cerrarla al terminar cada consulta. | [ConexionBD](../src/conexion/ConexionBD.java) abre una conexión nueva desde variables de entorno; los DAOs la cierran con `try-with-resources`. | La configuración ausente produce un error explícito. No hay un objeto `Connection` global ni un Singleton de conexión. |
| Factory funcional para vistas | Evita construir y cargar todas las pantallas al abrir el panel. | Cada `NavItem` de [PanelPrincipal](../src/vista/PanelPrincipal.java) contiene un `Supplier<JComponent>`; la vista se crea al primer acceso y conserva la tarjeta para las siguientes visitas. | Las rutas se seleccionan según el rol y reutilizan las vistas existentes. La carga de datos de cada módulo requiere una base configurada. |

`Strategy`, `Observer` y un `Presenter` separado no se incorporaron en esta etapa:
no se identificó una política de cálculo repetida que justifique Strategy ni
un flujo de eventos existente que justifique Observer. Los módulos de gestión
siguen siendo vistas Swing con lógica propia; separarlos debe hacerse junto a
una operación concreta, no declararse como MVC por la estructura de paquetes.

## Estructuras de datos existentes

| Estructura | Qué representa y dónde aparece | Valor y límite actuales |
|---|---|---|
| `ArrayList<Libro>` | Catálogo en memoria de [EstructurasDatos](../src/estructuras/EstructurasDatos.java); [GestionLibros](../src/vista/GestionLibros.java) lo llena a partir de [LibroDAO](../src/dao/LibroDAO.java). | Es útil para practicar recorrido y ordenamiento sobre una colección. La búsqueda es lineal y el Bubble Sort existente es O(n²); no sustituye filtros ni ordenamiento de la base para catálogos grandes. |
| `LinkedList<String>` | Historial de navegación acotado a diez módulos dentro de [EstructurasDatos](../src/estructuras/EstructurasDatos.java). | `addLast`/`removeFirst` representan una ventana FIFO. No se encontró una llamada a `registrarNavegacion` fuera de la clase, por lo que el historial aún no forma parte de una ruta de usuario. |
| `Stack<String>` | Registro LIFO de operaciones de libros desde [GestionLibros](../src/vista/GestionLibros.java). | `ultimaOperacion` y `deshacerOperacion` permiten demostrar `peek`/`pop`. El contenido son descripciones, no cambios reversibles; `deshacerOperacion` no restaura el estado de la base. |
| `Queue<Prestamo>` | Solicitud de préstamo en [GestionPrestamos](../src/vista/GestionPrestamos.java), respaldada por una `LinkedList` en [EstructurasDatos](../src/estructuras/EstructurasDatos.java). | La operación demuestra FIFO, pero actualmente cada préstamo se encola y procesa inmediatamente. No es una cola de trabajo asíncrona ni reemplaza la transacción de persistencia. |

## Verificación con PostgreSQL

1. Configura `POSTGRESQL_JDBC_JAR` en Eclipse.
2. Define `BIBLIOTECA_DB_URL`, `BIBLIOTECA_DB_USER` y `BIBLIOTECA_DB_PASSWORD` en la configuración de ejecución.
3. Inicia sesión con un usuario de cada rol y revisa inicio, circulación,
   catálogo, devoluciones, estudiantes, multas y reportes.
4. Si el panel no puede consultar PostgreSQL, debe mostrar el error y permitir
   reintentar; las métricas no deben presentarse como ceros válidos.

En el entorno de esta refactorización no estaban configurados el driver ni las
variables de conexión; por ello no se afirma que las consultas o los flujos de
persistencia se hayan ejecutado contra PostgreSQL.
