package ar.capacitacion.clientes.controller;

import ar.capacitacion.clientes.dto.SolicitudAltaDTO;
import ar.capacitacion.clientes.dto.SolicitudAltaRequestDTO;
import ar.capacitacion.clientes.entity.EstadoSolicitud;
import ar.capacitacion.clientes.service.SolicitudAltaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del contrato HTTP de SolicitudAltaController.
 *
 * Utiliza slice web (@WebMvcTest): simula el servicio sin levantar la base de datos.
 */
@WebMvcTest(SolicitudAltaController.class)
@Import(CorsConfig.class)
class SolicitudAltaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolicitudAltaService solicitudService;

    // -------------------------------------------------------------------------
    // Consulta y listado
    // -------------------------------------------------------------------------

    @Test
    void listar_deberiaRetornarListaDeSolicitudes() throws Exception {
        when(solicitudService.listar()).thenReturn(List.of(
                new SolicitudAltaDTO(1, "Marcos Paz", "marcos.paz@example.com", "11-4000-2001",
                        EstadoSolicitud.PENDIENTE, LocalDate.of(2025, 2, 1), null, null),
                new SolicitudAltaDTO(2, "Valentina Torres", "valentina.torres@example.com", "11-4000-2002",
                        EstadoSolicitud.PENDIENTE, LocalDate.of(2025, 2, 2), null, null)));

        mockMvc.perform(get("/api/solicitudes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Marcos Paz")))
                .andExpect(jsonPath("$[0].estado", is("PENDIENTE")));
    }

    @Test
    void obtenerPorId_existente_deberiaRetornar200() throws Exception {
        when(solicitudService.buscarPorId(1L)).thenReturn(Optional.of(
                new SolicitudAltaDTO(1, "Marcos Paz", "marcos.paz@example.com", "11-4000-2001",
                        EstadoSolicitud.PENDIENTE, LocalDate.of(2025, 2, 1), null, null)));

        mockMvc.perform(get("/api/solicitudes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Marcos Paz")))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")));
    }

    @Test
    void obtenerPorId_inexistente_deberiaRetornar404() throws Exception {
        when(solicitudService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/solicitudes/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", is("Solicitud no encontrada")));
    }

    // -------------------------------------------------------------------------
    // Registro (POST /api/solicitudes)
    // -------------------------------------------------------------------------

    @Test
    void registrar_datosValidos_deberiaRetornar201() throws Exception {
        String payload = """
                {
                    "nombre": "Esteban Quito",
                    "email": "esteban@example.com",
                    "telefono": "11-4000-3001"
                }
                """;
        when(solicitudService.crear(any(SolicitudAltaRequestDTO.class))).thenReturn(
                new SolicitudAltaDTO(4, "Esteban Quito", "esteban@example.com", "11-4000-3001",
                        EstadoSolicitud.PENDIENTE, LocalDate.now(), null, null));

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.nombre", is("Esteban Quito")))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")));
    }

    @Test
    void registrar_datosInvalidos_deberiaRetornar400() throws Exception {
        String payloadInvalido = """
                {
                    "nombre": "",
                    "email": "invalido",
                    "telefono": ""
                }
                """;

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", is("Error de validación en los datos de la solicitud")));

        verify(solicitudService, never()).crear(any());
    }

    // -------------------------------------------------------------------------
    // Rechazo (POST /api/solicitudes/{id}/rechazar)
    // -------------------------------------------------------------------------

    @Test
    void rechazar_solicitudPendiente_deberiaRetornar200() throws Exception {
        when(solicitudService.rechazar(1L)).thenReturn(Optional.of(
                new SolicitudAltaDTO(1, "Marcos Paz", "marcos.paz@example.com", "11-4000-2001",
                        EstadoSolicitud.RECHAZADA, LocalDate.of(2025, 2, 1), LocalDate.now(), null)));

        mockMvc.perform(post("/api/solicitudes/1/rechazar").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("RECHAZADA")));
    }

    // -------------------------------------------------------------------------
    // Aprobación (POST /api/solicitudes/{id}/aprobar)
    // -------------------------------------------------------------------------

    @Test
    void aprobar_exitoso_deberiaCrearClienteYActualizarSolicitud() throws Exception {
        when(solicitudService.aprobar(eq(3L), eq(false))).thenReturn(Optional.of(
                new SolicitudAltaDTO(3, "Lucía Ramos", "lucia.ramos@example.com", "11-4000-2003",
                        EstadoSolicitud.APROBADA, LocalDate.of(2025, 2, 3), LocalDate.now(), 10L)));

        mockMvc.perform(post("/api/solicitudes/3/aprobar").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("APROBADA")))
                .andExpect(jsonPath("$.clienteId", is(10)));

        verify(solicitudService).aprobar(3L, false);
    }

    @Test
    void aprobar_conFalloSimulado_deberiaCrearClientePeroFallarAntesDeActualizarSolicitud() throws Exception {
        when(solicitudService.aprobar(eq(1L), eq(true))).thenThrow(new IllegalStateException(
                "Fallo técnico simulado posterior a la creación del cliente (demostración didáctica Clase 8)"));

        mockMvc.perform(post("/api/solicitudes/1/aprobar")
                        .param("simularFallo", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje", is("Fallo técnico simulado posterior a la creación del cliente (demostración didáctica Clase 8)")));

        verify(solicitudService).aprobar(1L, true);
    }

    @Test
    void aprobar_solicitudYaAprobada_deberiaRetornar409Conflict() throws Exception {
        when(solicitudService.aprobar(eq(1L), eq(false))).thenThrow(new IllegalStateException(
                "La solicitud 1 ya fue resuelta (estado actual: APROBADA)"));

        mockMvc.perform(post("/api/solicitudes/1/aprobar").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje", is("La solicitud 1 ya fue resuelta (estado actual: APROBADA)")));

        verify(solicitudService).aprobar(1L, false);
    }
}
