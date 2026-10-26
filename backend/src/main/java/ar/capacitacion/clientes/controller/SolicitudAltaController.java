package ar.capacitacion.clientes.controller;

import ar.capacitacion.clientes.dto.SolicitudAltaDTO;
import ar.capacitacion.clientes.dto.SolicitudAltaRequestDTO;
import ar.capacitacion.clientes.service.SolicitudAltaService;

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
 * Expone los endpoints para listar, consultar, registrar, rechazar y aprobar
 * solicitudes de alta, delegando la lógica de negocio en {@link SolicitudAltaService}.
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudAltaController {

    private final SolicitudAltaService solicitudService;

    public SolicitudAltaController(SolicitudAltaService solicitudService) {
        this.solicitudService = solicitudService;
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
     */
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(
            @PathVariable long id,
            @RequestParam(defaultValue = "false") boolean simularFallo) {
        try {
            return solicitudService.aprobar(id, simularFallo)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("mensaje", "Solicitud no encontrada")));
        } catch (IllegalStateException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Fallo técnico simulado")) {
                throw ex;
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", ex.getMessage()));
        }
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
