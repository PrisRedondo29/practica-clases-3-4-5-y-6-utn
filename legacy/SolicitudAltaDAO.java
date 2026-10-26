package ar.capacitacion.legacy;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO JDBC para operaciones sobre la tabla SOLICITUDES_ALTA.
 * Material de análisis para la capacitación (no ejecutable directamente).
 */
public class SolicitudAltaDAO {

    public SolicitudAlta buscarPorId(long id) throws SQLException {
        String sql = "SELECT ID, NOMBRE, EMAIL, TELEFONO, ESTADO, FECHA_CREACION, FECHA_RESOLUCION, CLIENTE_ID "
                   + "FROM SOLICITUDES_ALTA WHERE ID = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long clienteIdVal = rs.getLong("CLIENTE_ID");
                    Long clienteId = rs.wasNull() ? null : clienteIdVal;

                    return new SolicitudAlta(
                            rs.getLong("ID"),
                            rs.getString("NOMBRE"),
                            rs.getString("EMAIL"),
                            rs.getString("TELEFONO"),
                            rs.getString("ESTADO"),
                            rs.getDate("FECHA_CREACION"),
                            rs.getDate("FECHA_RESOLUCION"),
                            clienteId
                    );
                }
                return null;
            }
        }
    }

    public void actualizarAprobada(long id, long clienteId, Date fechaResolucion) throws SQLException {
        String sql = "UPDATE SOLICITUDES_ALTA "
                   + "SET ESTADO = 'APROBADA', FECHA_RESOLUCION = ?, CLIENTE_ID = ? "
                   + "WHERE ID = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, fechaResolucion);
            ps.setLong(2, clienteId);
            ps.setLong(3, id);

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró la solicitud para actualizar con ID: " + id);
            }
        }
    }
}
