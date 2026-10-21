package ar.capacitacion.clientes.controller;

import ar.capacitacion.clientes.dto.*;
import ar.capacitacion.clientes.service.ClienteService;
import ar.capacitacion.clientes.repository.ClienteRepository;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository repository;

    @BeforeEach
    void setUp() {
        // Garantiza aislamiento total entre pruebas restableciendo el repositorio en memoria
        repository.reset();
    }

    // -------------------------------------------------------------------------
    // Tests existentes (compatibilidad Clases 3 a 5)
    // -------------------------------------------------------------------------

    @Test
    void listarClientes_deberiaRetornar5Elementos() throws Exception {
        mockMvc.perform(get("/api/clientes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Ana")))
                .andExpect(jsonPath("$[0].estado", is("ACTIVO")))
                .andExpect(jsonPath("$[0].email").doesNotExist())
                .andExpect(jsonPath("$[0].telefono").doesNotExist())
                .andExpect(jsonPath("$[0].fechaAlta").doesNotExist());
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
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.email", is("ana@example.com")))
                .andExpect(jsonPath("$.telefono", is("11-4000-1001")))
                .andExpect(jsonPath("$.fechaAlta", is("2022-03-15")));
    }

    @Test
    void obtenerPorId_existente5_deberiaRetornar200() throws Exception {
        mockMvc.perform(get("/api/clientes/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.nombre", is("Elena")))
                .andExpect(jsonPath("$.estado", is("INACTIVO")))
                .andExpect(jsonPath("$.email", is("elena@example.com")))
                .andExpect(jsonPath("$.telefono", is("11-4000-1005")))
                .andExpect(jsonPath("$.fechaAlta", is("2024-05-30")));
    }

    @Test
    void obtenerPorId_inexistente999_deberiaRetornar404() throws Exception {
        mockMvc.perform(get("/api/clientes/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Cliente no encontrado")));
    }

    @Test
    void obtenerPorId_escenarioError_deberiaRetornar503() throws Exception {
        mockMvc.perform(get("/api/clientes/1").param("scenario", "error").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());
    }

    // -------------------------------------------------------------------------
    // Tests de Alta (POST /api/clientes)
    // -------------------------------------------------------------------------

    @Test
    void crearCliente_datosValidos_deberiaRetornar201YClienteConsultable() throws Exception {
        String payload = """
                {
                    "nombre": "Franco",
                    "email": "franco@example.com",
                    "telefono": "11-4000-1006"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.nombre", is("Franco")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.email", is("franco@example.com")))
                .andExpect(jsonPath("$.telefono", is("11-4000-1006")))
                .andExpect(jsonPath("$.fechaAlta", is(LocalDate.now().toString())));

        // Verificar que el cliente recién creado es consultable por su ID
        mockMvc.perform(get("/api/clientes/6").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.nombre", is("Franco")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")));

        // Verificar que aparece en el listado general
        mockMvc.perform(get("/api/clientes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[5].id", is(6)))
                .andExpect(jsonPath("$[5].nombre", is("Franco")))
                .andExpect(jsonPath("$[5].estado", is("ACTIVO")));
    }

    @Test
    void crearCliente_datosInvalidos_deberiaRetornar400YDetalleErrores() throws Exception {
        String payloadInvalido = """
                {
                    "nombre": "",
                    "email": "email-no-valido",
                    "telefono": ""
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", is("Error de validación en los datos enviados")))
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.email").exists())
                .andExpect(jsonPath("$.errores.telefono").exists());
    }

    // -------------------------------------------------------------------------
    // Tests de Modificación (PUT /api/clientes/{id})
    // -------------------------------------------------------------------------

    @Test
    void actualizarCliente_existente_deberiaRetornar200YDatosActualizados() throws Exception {
        String payloadModificacion = """
                {
                    "nombre": "Ana María",
                    "email": "anamaria@example.com",
                    "telefono": "11-4000-9999"
                }
                """;

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadModificacion)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Ana María")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.email", is("anamaria@example.com")))
                .andExpect(jsonPath("$.telefono", is("11-4000-9999")))
                .andExpect(jsonPath("$.fechaAlta", is("2022-03-15")));

        // Reflejo en consulta por ID
        mockMvc.perform(get("/api/clientes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Ana María")))
                .andExpect(jsonPath("$.email", is("anamaria@example.com")));

        // Reflejo en listado general
        mockMvc.perform(get("/api/clientes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre", is("Ana María")));
    }

    @Test
    void actualizarCliente_inexistente_deberiaRetornar404() throws Exception {
        String payload = """
                {
                    "nombre": "Inexistente",
                    "email": "noexiste@example.com",
                    "telefono": "11-4000-9999"
                }
                """;

        mockMvc.perform(put("/api/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Cliente no encontrado")));
    }

    @Test
    void actualizarCliente_datosInvalidos_deberiaRetornar400() throws Exception {
        String payloadInvalido = """
                {
                    "nombre": "A",
                    "email": "correo-erroneo",
                    "telefono": "123"
                }
                """;

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", is("Error de validación en los datos enviados")))
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.email").exists())
                .andExpect(jsonPath("$.errores.telefono").exists());
    }

    @Test
    void actualizarCliente_deberiaConservarIdFechaAltaYEstado() throws Exception {
        // Bruno: id=2, estado=INACTIVO, fechaAlta=2021-07-22
        String payload = """
                {
                    "nombre": "Bruno Actualizado",
                    "email": "bruno.nuevo@example.com",
                    "telefono": "11-4000-8888"
                }
                """;

        mockMvc.perform(put("/api/clientes/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.estado", is("INACTIVO")))
                .andExpect(jsonPath("$.fechaAlta", is("2021-07-22")))
                .andExpect(jsonPath("$.nombre", is("Bruno Actualizado")))
                .andExpect(jsonPath("$.email", is("bruno.nuevo@example.com")))
                .andExpect(jsonPath("$.telefono", is("11-4000-8888")));
    }

    // -------------------------------------------------------------------------
    // Tests de Baja Lógica (DELETE /api/clientes/{id})
    // -------------------------------------------------------------------------

    @Test
    void darDeBaja_existente_deberiaRetornar204YModificarEstadoAInactivo() throws Exception {
        // Ana inicialmente ACTIVO
        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());

        // Consultar detalle: ahora debe estar INACTIVO conservando todos sus demás datos
        mockMvc.perform(get("/api/clientes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Ana")))
                .andExpect(jsonPath("$.estado", is("INACTIVO")))
                .andExpect(jsonPath("$.email", is("ana@example.com")))
                .andExpect(jsonPath("$.telefono", is("11-4000-1001")))
                .andExpect(jsonPath("$.fechaAlta", is("2022-03-15")));
    }

    @Test
    void darDeBaja_inexistente_deberiaRetornar404() throws Exception {
        mockMvc.perform(delete("/api/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Cliente no encontrado")));
    }

    @Test
    void darDeBaja_clienteInactivoDebeConservarseEnListado() throws Exception {
        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());

        // El listado debe conservar sus 5 elementos y reflejar el estado INACTIVO
        mockMvc.perform(get("/api/clientes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Ana")))
                .andExpect(jsonPath("$[0].estado", is("INACTIVO")));
    }

    @Test
    void darDeBaja_clienteYaInactivo_deberiaSerIdempotenteYRetornar204() throws Exception {
        // Bruno (id=2) ya es INACTIVO
        mockMvc.perform(delete("/api/clientes/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/clientes/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.estado", is("INACTIVO")));
    }

    // -------------------------------------------------------------------------
    // Test de CORS
    // -------------------------------------------------------------------------

    @Test
    void cors_preflight_deberiaPermitirMetodosABM() throws Exception {
        mockMvc.perform(options("/api/clientes")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("PUT")))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("DELETE")));
    }
}
