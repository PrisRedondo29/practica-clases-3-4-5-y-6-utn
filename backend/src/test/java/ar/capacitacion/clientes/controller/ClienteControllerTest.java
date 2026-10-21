package ar.capacitacion.clientes.controller;

import ar.capacitacion.clientes.dto.ClienteDTO;
import ar.capacitacion.clientes.dto.ClienteDetalleDTO;
import ar.capacitacion.clientes.dto.ClienteRequestDTO;
import ar.capacitacion.clientes.entity.EstadoCliente;
import ar.capacitacion.clientes.service.ClienteService;
import ar.capacitacion.clientes.controller.CorsConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del contrato HTTP del Controller (rutas, códigos de estado, validaciones y JSON).
 *
 * Usa un slice web (@WebMvcTest): no levanta base de datos, Flyway ni JPA, y el Service
 * está simulado con Mockito. Las reglas de negocio y la persistencia NO se verifican aquí.
 */
@WebMvcTest(ClienteController.class)
@Import(CorsConfig.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Spring Boot 3.3.x (Spring Framework 6.1) usa @MockBean; @MockitoBean llega con Framework 6.2.
    @MockBean
    private ClienteService service;

    private static ClienteDetalleDTO ana() {
        return new ClienteDetalleDTO(1, "Ana", EstadoCliente.ACTIVO,
                "ana@example.com", "11-4000-1001", LocalDate.of(2022, 3, 15));
    }

    private static ClienteDetalleDTO elena() {
        return new ClienteDetalleDTO(5, "Elena", EstadoCliente.INACTIVO,
                "elena@example.com", "11-4000-1005", LocalDate.of(2024, 5, 30));
    }

    // -------------------------------------------------------------------------
    // Consulta (GET /api/clientes y GET /api/clientes/{id})
    // -------------------------------------------------------------------------

    @Test
    void listarClientes_deberiaRetornar5Elementos() throws Exception {
        when(service.listar()).thenReturn(List.of(
                new ClienteDTO(1, "Ana", EstadoCliente.ACTIVO),
                new ClienteDTO(2, "Bruno", EstadoCliente.INACTIVO),
                new ClienteDTO(3, "Carla", EstadoCliente.ACTIVO),
                new ClienteDTO(4, "Diego", EstadoCliente.ACTIVO),
                new ClienteDTO(5, "Elena", EstadoCliente.INACTIVO)));

        mockMvc.perform(get("/api/clientes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
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

        // El escenario didáctico se resuelve en el Controller, sin consultar al Service
        verifyNoInteractions(service);
    }

    @Test
    void listarClientes_escenarioError_deberiaRetornar503() throws Exception {
        mockMvc.perform(get("/api/clientes").param("scenario", "error").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.mensaje", is("Servicio no disponible (escenario didáctico)")));

        verifyNoInteractions(service);
    }

    @Test
    void obtenerPorId_existente1_deberiaRetornar200YContenido() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(ana()));

        mockMvc.perform(get("/api/clientes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Ana")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.email", is("ana@example.com")))
                .andExpect(jsonPath("$.telefono", is("11-4000-1001")))
                .andExpect(jsonPath("$.fechaAlta", is("2022-03-15")));
    }

    @Test
    void obtenerPorId_existente5_deberiaRetornar200() throws Exception {
        when(service.buscarPorId(5L)).thenReturn(Optional.of(elena()));

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
        when(service.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clientes/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Cliente no encontrado")));
    }

    @Test
    void obtenerPorId_escenarioError_deberiaRetornar503() throws Exception {
        mockMvc.perform(get("/api/clientes/1").param("scenario", "error").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());

        verifyNoInteractions(service);
    }

    // -------------------------------------------------------------------------
    // Alta (POST /api/clientes)
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
        when(service.crear(any(ClienteRequestDTO.class))).thenReturn(new ClienteDetalleDTO(
                6, "Franco", EstadoCliente.ACTIVO, "franco@example.com", "11-4000-1006",
                LocalDate.of(2025, 1, 15)));

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
                .andExpect(jsonPath("$.fechaAlta", is("2025-01-15")));

        // El JSON recibido se convirtió correctamente al DTO de entrada que usa el Service
        verify(service).crear(new ClienteRequestDTO("Franco", "franco@example.com", "11-4000-1006"));
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

        // Una solicitud inválida nunca debe llegar al Service
        verify(service, never()).crear(any());
    }

    @Test
    void crearCliente_cuerpoMalformado_deberiaRetornar400() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ esto no es json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", is("Cuerpo de solicitud inválido o ausente")));

        verifyNoInteractions(service);
    }

    // -------------------------------------------------------------------------
    // Modificación (PUT /api/clientes/{id})
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
        when(service.actualizar(eq(1L), any(ClienteRequestDTO.class))).thenReturn(Optional.of(
                new ClienteDetalleDTO(1, "Ana María", EstadoCliente.ACTIVO,
                        "anamaria@example.com", "11-4000-9999", LocalDate.of(2022, 3, 15))));

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

        // El id de la URL y el cuerpo JSON llegan correctamente al Service
        verify(service).actualizar(1L,
                new ClienteRequestDTO("Ana María", "anamaria@example.com", "11-4000-9999"));
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
        when(service.actualizar(eq(999L), any(ClienteRequestDTO.class))).thenReturn(Optional.empty());

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

        verifyNoInteractions(service);
    }

    /**
     * Versión a nivel Controller: verifica que la respuesta expone tal cual el id, el estado
     * y la fechaAlta que devuelve el Service. Que el Service los conserve realmente al
     * actualizar es una regla de negocio y se prueba en un test unitario del Service.
     */
    @Test
    void actualizarCliente_deberiaConservarIdFechaAltaYEstado() throws Exception {
        String payload = """
                {
                    "nombre": "Bruno Actualizado",
                    "email": "bruno.nuevo@example.com",
                    "telefono": "11-4000-8888"
                }
                """;
        when(service.actualizar(eq(2L), any(ClienteRequestDTO.class))).thenReturn(Optional.of(
                new ClienteDetalleDTO(2, "Bruno Actualizado", EstadoCliente.INACTIVO,
                        "bruno.nuevo@example.com", "11-4000-8888", LocalDate.of(2021, 7, 22))));

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
    // Baja lógica (DELETE /api/clientes/{id})
    // -------------------------------------------------------------------------

    @Test
    void darDeBaja_existente_deberiaRetornar204YModificarEstadoAInactivo() throws Exception {
        when(service.darDeBaja(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // A nivel Controller solo se verifica la delegación al Service; el cambio de estado
        // a INACTIVO es una regla del Service.
        verify(service).darDeBaja(1L);
    }

    @Test
    void darDeBaja_inexistente_deberiaRetornar404() throws Exception {
        when(service.darDeBaja(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Cliente no encontrado")));
    }

    // -------------------------------------------------------------------------
    // CORS
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

    // -------------------------------------------------------------------------
    // Escenarios originales que no se pueden verificar con un Service simulado
    // (requieren estado real o lógica del Service):
    //  - crearCliente: estado inicial ACTIVO, fecha de alta actual y trim de los datos.
    //  - crearCliente / actualizarCliente: reflejo posterior en consulta por ID y en el listado.
    //  - darDeBaja_clienteInactivoDebeConservarseEnListado.
    //  - darDeBaja_clienteYaInactivo_deberiaSerIdempotenteYRetornar204.
    //  - listar: orden ascendente por ID.
    // -------------------------------------------------------------------------
}