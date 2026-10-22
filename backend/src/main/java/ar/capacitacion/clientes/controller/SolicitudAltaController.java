package ar.capacitacion.clientes.controller;

import ar.capacitacion.clientes.dto.ClienteDetalleDTO;
import ar.capacitacion.clientes.dto.ClienteRequestDTO;
import ar.capacitacion.clientes.dto.SolicitudAltaDTO;
import ar.capacitacion.clientes.dto.SolicitudAltaRequestDTO;
import ar.capacitacion.clientes.entity.EstadoSolicitud;
import ar.capacitacion.clientes.entity.SolicitudAltaEntity;
import ar.capacitacion.clientes.repository.SolicitudAltaRepository;
import ar.capacitacion.clientes.service.ClienteService;
import ar.capacitacion.clientes.service.SolicitudAltaService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Controlador REST para el circuito de solicitudes de alta de clientes.
 *
 * NOTA DIDÁCTICA CENTRAL (CLASE 8):
 * El método {@code aprobar} implementa DELIBERADAMENTE un antipatrón arquitectónico:
 * coordina la lógica de negocio directamente en el Controller, ejecutando dos escrituras
 * independientes sin un límite transaccional unificado.
 *
 * Durante la práctica en vivo de la Clase 8, la docente mostrará el fallo y refactorizará
 * este método trasladando la coordinación hacia {@link SolicitudAltaService} con {@code @Transactional}.
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudAltaController {

    private final SolicitudAltaService solicitudService;
    private final SolicitudAltaRepository solicitudRepository;
    private final ClienteService clienteService;

    public SolicitudAltaController(
            SolicitudAltaService solicitudService,
            SolicitudAltaRepository solicitudRepository,
            ClienteService clienteService) {
        this.solicitudService = solicitudService;
        this.solicitudRepository = solicitudRepository;
        this.clienteService = clienteService;
    }

    /**
     * GET /api/solicitudes -> 200 con listado de solicitudes
     */
    @GetMapping
    public ResponseEntity<List<SolicitudAltaDTO>> listar() {
        return ResponseEntity.ok(solicitudService.listar());
    }

    /**
     * GET /api/solicitudes/{id} -> 200 con detalle de solicitud o 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable long id) {
        return solicitudService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "Solicitud no encontrada")));
    }

    /**
     * POST /api/solicitudes -> 201 Created con solicitud registrada en estado PENDIENTE
     */
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody SolicitudAltaRequestDTO request) {
        SolicitudAltaDTO creada = solicitudService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * POST /api/solicitudes/{id}/rechazar -> 200 OK con solicitud en estado RECHAZADA
     */
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable long id) {
        try {
            return solicitudService.rechazar(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("mensaje", "Solicitud no encontrada")));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", ex.getMessage()));
        }
    }

    /**
     * POST /api/solicitudes/{id}/aprobar?simularFallo=true|false
     *
     * ANTIPATRÓN INICIAL DELIBERADO:
     * Este método NO TIENE @Transactional.
     * Coordina dos escrituras independientes:
     * 1) Llama a clienteService.crear(), que abre SU PROPIA transacción y hace COMMIT inmediato.
     * 2) Si simularFallo=true, se produce una excepción unchecked.
     *    El cliente ya quedó insertado y confirmado en la base de datos, pero la solicitud
     *    nunca se actualiza a APROBADA (queda PENDIENTE), dejando la base en estado INCONSISTENTE.
     */
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(
            @PathVariable long id,
            @RequestParam(defaultValue = "false") boolean simularFallo) {

        // 1. Recuperar la solicitud
        SolicitudAltaEntity solicitud = solicitudRepository.findById(id).orElse(null);
        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Solicitud no encontrada"));
        }

        // 2. Validar que la solicitud esté PENDIENTE
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "La solicitud " + id + " ya fue resuelta (estado actual: " + solicitud.getEstado() + ")"));
        }

        // 3. PRIMERA ESCRITURA: Llamada a ClienteService.crear(...)
        // Al ejecutarse sin transacción envolvente en el Controller, ClienteService.crear()
        // inicia su propia transacción física (Propagation.REQUIRED), ejecuta el INSERT
        // y hace COMMIT inmediato al retornar.
        ClienteDetalleDTO clienteCreado = clienteService.crear(new ClienteRequestDTO(
                solicitud.getNombre(),
                solicitud.getEmail(),
                solicitud.getTelefono()));

        // 4. FALLA TÉCNICA CONTROLADA (Mecanismo didáctico para la Clase 8)
        // Ocurre DESPUÉS de que el cliente fue creado y confirmado en la BD.
        if (simularFallo) {
            throw new IllegalStateException(
                    "Fallo técnico simulado posterior a la creación del cliente (demostración didáctica Clase 8)");
        }

        // 5. SEGUNDA ESCRITURA: Actualizar la solicitud a APROBADA
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setFechaResolucion(LocalDate.now());
        solicitud.setClienteId(clienteCreado.id());
        SolicitudAltaEntity guardada = solicitudRepository.save(solicitud);

        return ResponseEntity.ok(solicitudService.aDTO(guardada));
    }

    /**
     * Manejo de excepciones de negocio o fallos simulados (500 Internal Server Error).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "mensaje", ex.getMessage()));
    }

    /**
     * Manejador de errores de validación Bean Validation (400 Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Error de validación en los datos de la solicitud");
        respuesta.put("errores", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    /**
     * Manejador de cuerpo de petición no legible o mal formado (400 Bad Request).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", "Cuerpo de solicitud inválido o ausente"));
    }
}
