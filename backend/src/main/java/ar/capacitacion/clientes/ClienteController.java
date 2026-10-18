package ar.capacitacion.clientes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    /**
     * GET /api/clientes                  -> 200 con clientes
     * GET /api/clientes?scenario=empty   -> 200 con []
     * GET /api/clientes?scenario=error   -> 503 (solo para demostrar response.ok)
     */
    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) String scenario) {
        if ("error".equals(scenario)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("mensaje", "Servicio no disponible (escenario didáctico)"));
        }
        if ("empty".equals(scenario)) {
            return ResponseEntity.ok(List.<ClienteDTO>of());
        }
        return ResponseEntity.ok(service.listar());
    }

    /**
     * GET /api/clientes/{id}                  -> 200 con detalle de cliente o 404
     * GET /api/clientes/{id}?scenario=error   -> 503
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable long id,
            @RequestParam(required = false) String scenario) {
        if ("error".equals(scenario)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("mensaje", "Servicio no disponible (escenario didáctico)"));
        }
        return service.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "Cliente no encontrado")));
    }

    /**
     * POST /api/clientes -> 201 Created con detalle de cliente, o 400 Bad Request por validación
     */
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ClienteRequestDTO request) {
        ClienteDetalleDTO creado = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * PUT /api/clientes/{id} -> 200 OK con detalle actualizado, 404 Not Found si no existe, o 400 Bad Request
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable long id,
            @Valid @RequestBody ClienteRequestDTO request) {
        return service.actualizar(id, request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "Cliente no encontrado")));
    }

    /**
     * DELETE /api/clientes/{id} -> 204 No Content (baja lógica), o 404 Not Found si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> darDeBaja(@PathVariable long id) {
        boolean existia = service.darDeBaja(id);
        if (!existia) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Cliente no encontrado"));
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Manejador de errores de validación de Bean Validation (400 Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Error de validación en los datos enviados");
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
