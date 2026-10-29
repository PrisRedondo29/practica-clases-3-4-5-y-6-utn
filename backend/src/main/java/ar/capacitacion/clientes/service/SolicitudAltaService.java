package ar.capacitacion.clientes.service;

import ar.capacitacion.clientes.dto.ClienteDetalleDTO;
import ar.capacitacion.clientes.dto.ClienteRequestDTO;
import ar.capacitacion.clientes.dto.SolicitudAltaDTO;
import ar.capacitacion.clientes.dto.SolicitudAltaRequestDTO;
import ar.capacitacion.clientes.entity.EstadoSolicitud;
import ar.capacitacion.clientes.entity.SolicitudAltaEntity;
import ar.capacitacion.clientes.repository.SolicitudAltaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de solicitudes de alta de clientes (registro, consulta, rechazo y aprobación).
 */
@Service
public class SolicitudAltaService {

    private final SolicitudAltaRepository repository;
    private final ClienteService clienteService;

    public SolicitudAltaService(SolicitudAltaRepository repository, ClienteService clienteService) {
        this.repository = repository;
        this.clienteService = clienteService;
    }

    @Transactional(readOnly = true)
    public List<SolicitudAltaDTO> listar() {
        return repository.findAllByOrderByIdAsc().stream()
                .map(this::aDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<SolicitudAltaDTO> buscarPorId(long id) {
        return repository.findById(id).map(this::aDTO);
    }

    /**
     * Registro de una nueva solicitud en estado PENDIENTE.
     */
    @Transactional
    public SolicitudAltaDTO crear(SolicitudAltaRequestDTO request) {
        SolicitudAltaEntity nueva = new SolicitudAltaEntity(
                request.nombre().trim(),
                request.email().trim(),
                request.telefono().trim(),
                LocalDate.now());
        return aDTO(repository.save(nueva));
    }

    /**
     * Rechazo de una solicitud de alta.
     * Si la solicitud está PENDIENTE, pasa a RECHAZADA y se fija la fecha de resolución.
     * No crea ningún registro de cliente.
     */
    @Transactional
    public Optional<SolicitudAltaDTO> rechazar(long id) {
        Optional<SolicitudAltaEntity> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        SolicitudAltaEntity solicitud = opt.get();
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("La solicitud " + id + " no se encuentra en estado PENDIENTE");
        }
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setFechaResolucion(LocalDate.now());
        return Optional.of(aDTO(solicitud));
    }

    /**
     * Aprobación de una solicitud de alta.
     * Crea el cliente a partir de los datos de la solicitud y, si no ocurre ninguna falla,
     * marca la solicitud como APROBADA vinculándola al cliente creado.
     */
    @Transactional
    public Optional<SolicitudAltaDTO> aprobar(long id, boolean simularFallo) {
        Optional<SolicitudAltaEntity> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return Optional.empty();
        }

        SolicitudAltaEntity solicitud = opt.get();
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new SolicitudYaResueltaException(id, solicitud.getEstado());
        }

        ClienteDetalleDTO cliente = clienteService.crear(new ClienteRequestDTO(
                solicitud.getNombre(),
                solicitud.getEmail(),
                solicitud.getTelefono()));

        if (simularFallo) {
            throw new IllegalStateException(
                    "Fallo técnico simulado posterior a la creación del cliente (demostración didáctica Clase 8)");
        }

        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setFechaResolucion(LocalDate.now());
        solicitud.setClienteId(cliente.id());

        return Optional.of(aDTO(repository.save(solicitud)));
    }
        
    public SolicitudAltaDTO aDTO(SolicitudAltaEntity entity) {
        return new SolicitudAltaDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getTelefono(),
                entity.getEstado(),
                entity.getFechaCreacion(),
                entity.getFechaResolucion(),
                entity.getClienteId());
    }
}
