package ar.capacitacion.legacy;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet clásico encargado de procesar la aprobación de solicitudes de alta de clientes.
 * Material de análisis para la capacitación (no ejecutable directamente).
 */
public class AprobarSolicitudServlet extends HttpServlet {

    private final SolicitudAltaDAO solicitudDAO = new SolicitudAltaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lectura y validación de parámetros de entrada
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro 'id' requerido");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de ID inválido");
            return;
        }

        boolean simularFallo = "true".equalsIgnoreCase(request.getParameter("simularFallo"));

        try {
            // 2. Consulta y validación de la solicitud
            SolicitudAlta solicitud = solicitudDAO.buscarPorId(id);
            if (solicitud == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Solicitud no encontrada");
                return;
            }

            if (!"PENDIENTE".equals(solicitud.getEstado())) {
                response.sendError(HttpServletResponse.SC_CONFLICT,
                        "La solicitud " + id + " ya fue resuelta (estado actual: " + solicitud.getEstado() + ")");
                return;
            }

            // 3. Creación del cliente a partir de los datos de la solicitud
            long nuevoClienteId = clienteDAO.insertar(
                    solicitud.getNombre(),
                    solicitud.getEmail(),
                    solicitud.getTelefono()
            );

            // 4. Punto de interrupción / verificación de fallo técnico
            if (simularFallo) {
                throw new IllegalStateException(
                        "Fallo técnico simulado posterior a la creación del cliente (demostración didáctica)");
            }

            // 5. Actualización de estado y vinculación de la solicitud
            solicitudDAO.actualizarAprobada(id, nuevoClienteId, new Date(System.currentTimeMillis()));

            // 6. Respuesta al cliente HTTP
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"mensaje\":\"Solicitud aprobada con éxito\",\"clienteId\":" + nuevoClienteId + "}");

        } catch (IllegalStateException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de base de datos: " + e.getMessage());
        }
    }
}
