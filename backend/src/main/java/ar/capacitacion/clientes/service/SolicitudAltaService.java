package ar.capacitacion.clientes.service;

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
 * Servicio para gestión básica de solicitudes de alta de clientes (registro, consulta y rechazo).
 *
 * NOTA DIDÁCTICA CLASE 8:
 * En este estado inicial, la operación compleja de APROBACIÓN no está implementada aquí;
 * se encuentra deliberadamente ubicada en el SolicitudAltaController para demostrar el antipatrón
 * de coordinación fuera del límite transaccional. Durante la clase en vivo, la docente migrará
 * dicha lógica hacia este Service aplicando @Transactional.
 */
@Service
@Transactional(readOnly = true)
public class SolicitudAltaService {

    private final SolicitudAltaRepository repository;

    public SolicitudAltaService(SolicitudAltaRepository repository) {
        this.repository = repository;
    }

    public List<SolicitudAltaDTO> listar() {
        return repository.findAllByOrderByIdAsc().stream()
                .map(this::aDTO)
                .toList();
    }

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
