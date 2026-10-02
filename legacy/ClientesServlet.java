package ar.capacitacion.legacy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Material de análisis (NO es un proyecto ejecutable).
 * Servlet clásico: lee parámetros, prepara datos, los deja en el request
 * y delega la presentación a la JSP mediante forward.
 */
public class ClientesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Entrada: parámetro de la URL (?q=...)
        String q = request.getParameter("q");
        if (q == null) {
            q = "";
        }

        // 2. Datos: en el legacy real esto sería un DAO/JDBC
        List<Cliente> clientes = obtenerClientes();

        // 3. Filtro aplicado EN EL SERVIDOR
        if (!q.trim().isEmpty()) {
            List<Cliente> filtrados = new ArrayList<Cliente>();
            for (Cliente c : clientes) {
                if (c.getNombre().toLowerCase().contains(q.trim().toLowerCase())) {
                    filtrados.add(c);
                }
            }
            clientes = filtrados;
        }

        // 4. Estado implícito: lo que la JSP "ve" viaja en el request
        request.setAttribute("clientes", clientes);
        request.setAttribute("q", q);

        // 5. Presentación: forward server-side hacia la JSP
        request.getRequestDispatcher("/WEB-INF/jsp/clientes.jsp")
               .forward(request, response);
    }

    private List<Cliente> obtenerClientes() {
        List<Cliente> lista = new ArrayList<Cliente>();
        lista.add(new Cliente(1, "Ana", "ACTIVO"));
        lista.add(new Cliente(2, "Bruno", "INACTIVO"));
        lista.add(new Cliente(3, "Carla", "ACTIVO"));
        return lista;
    }

    /** Bean simple (getters requeridos por EL: ${c.nombre}). */
    public static class Cliente {
        private final int id;
        private final String nombre;
        private final String estado;

        public Cliente(int id, String nombre, String estado) {
            this.id = id;
            this.nombre = nombre;
            this.estado = estado;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getEstado() { return estado; }
    }
}
