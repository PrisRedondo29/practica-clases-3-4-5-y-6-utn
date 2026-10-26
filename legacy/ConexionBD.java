package ar.capacitacion.legacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilidad de conexión JDBC para componentes legacy.
 * Material de análisis para la capacitación (no ejecutable directamente).
 */
public class ConexionBD {

    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1";
    private static final String USER = "clase7";
    private static final String PASSWORD = "oracle";

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver JDBC de Oracle", e);
        }
    }

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
