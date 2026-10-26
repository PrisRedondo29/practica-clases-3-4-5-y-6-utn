package ar.capacitacion.legacy;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO JDBC para operaciones sobre la tabla CLIENTES.
 * Material de análisis para la capacitación (no ejecutable directamente).
 */
public class ClienteDAO {

    public long insertar(String nombre, String email, String telefono) throws SQLException {
        String sql = "INSERT INTO CLIENTES (ID, NOMBRE, ESTADO, EMAIL, TELEFONO, FECHA_ALTA) "
                   + "VALUES (CLIENTES_SEQ.NEXTVAL, ?, 'ACTIVO', ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, new String[] { "ID" })) {

            ps.setString(1, nombre != null ? nombre.trim() : null);
            ps.setString(2, email != null ? email.trim() : null);
            ps.setString(3, telefono != null ? telefono.trim() : null);
            ps.setDate(4, new Date(System.currentTimeMillis()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("No se pudo obtener el ID autogenerado del cliente insertado.");
            }
        }
    }
}
