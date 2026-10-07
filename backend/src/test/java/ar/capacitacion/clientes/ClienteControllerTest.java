package ar.capacitacion.clientes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarClientes_deberiaRetornar5Elementos() throws Exception {
        mockMvc.perform(get("/api/clientes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    void listarClientes_escenarioEmpty_deberiaRetornar200YListaVacia() throws Exception {
        mockMvc.perform(get("/api/clientes").param("scenario", "empty").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listarClientes_escenarioError_deberiaRetornar503() throws Exception {
        mockMvc.perform(get("/api/clientes").param("scenario", "error").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.mensaje", is("Servicio no disponible (escenario didáctico)")));
    }

    @Test
    void obtenerPorId_existente1_deberiaRetornar200YContenido() throws Exception {
        mockMvc.perform(get("/api/clientes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Ana")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")));
    }

    @Test
    void obtenerPorId_existente5_deberiaRetornar200() throws Exception {
        mockMvc.perform(get("/api/clientes/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.nombre", is("Elena")))
                .andExpect(jsonPath("$.estado", is("INACTIVO")));
    }

    @Test
    void obtenerPorId_inexistente999_deberiaRetornar404() throws Exception {
        mockMvc.perform(get("/api/clientes/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorId_escenarioError_deberiaRetornar503() throws Exception {
        mockMvc.perform(get("/api/clientes/1").param("scenario", "error").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());
    }
}
