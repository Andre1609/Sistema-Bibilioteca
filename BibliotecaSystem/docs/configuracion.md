# Configuración de PostgreSQL

La aplicación obtiene las credenciales de variables de entorno. No guardes usuarios ni contraseñas en el código fuente.

## Variables requeridas

| Variable | Valor |
|---|---|
| `BIBLIOTECA_DB_URL` | URL JDBC de la base de datos PostgreSQL |
| `BIBLIOTECA_DB_USER` | Usuario de la base de datos |
| `BIBLIOTECA_DB_PASSWORD` | Contraseña de ese usuario |

En PowerShell, configura las variables antes de iniciar la aplicación:

```powershell
$env:BIBLIOTECA_DB_URL = "jdbc:postgresql://localhost:5432/<base_de_datos>"
$env:BIBLIOTECA_DB_USER = "<usuario>"
$env:BIBLIOTECA_DB_PASSWORD = "<contraseña>"
```

En Eclipse, define las mismas variables en **Run Configurations > Environment** para la configuración de ejecución del proyecto.

El proyecto usa la variable de classpath `POSTGRESQL_JDBC_JAR` para el driver JDBC, en lugar de una ruta de disco perteneciente a otra computadora. Configúrala en **Java > Build Path > Classpath Variables** para apuntar al archivo JAR local de PostgreSQL.

`ConexionBD.getConexion()` abre una conexión nueva y cada llamador debe cerrarla con `try-with-resources`. Si falta una variable, el proveedor informa cuál debe configurarse.
