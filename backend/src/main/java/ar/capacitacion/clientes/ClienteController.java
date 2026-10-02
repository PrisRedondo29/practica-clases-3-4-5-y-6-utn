package ar.capacitacion.clientes;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
