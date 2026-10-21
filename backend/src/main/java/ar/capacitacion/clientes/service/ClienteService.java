package ar.capacitacion.clientes.service;

import ar.capacitacion.clientes.dto.*;
import ar.capacitacion.clientes.entity.*;
import ar.capacitacion.clientes.repository.ClienteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    /**
     * Listado resumido de clientes ordenado por ID ascendente.
     */
    public List<ClienteDTO> listar() {
        return repository.findAllByOrderByIdAsc().stream()
                .map(this::aResumenDTO)
                .toList();
    }

    public Optional<ClienteDetalleDTO> buscarPorId(long id) {
        return repository.findById(id).map(this::aDetalleDTO);
    }

    /**
     * Alta de cliente con ID autogenerado, fecha de alta actual y estado inicial ACTIVO.
     */
    @Transactional
    public ClienteDetalleDTO crear(ClienteRequestDTO request) {
        ClienteEntity nuevo = new ClienteEntity(
                request.nombre().trim(),
                EstadoCliente.ACTIVO,
                request.email().trim(),
                request.telefono().trim(),
                LocalDate.now());
        return aDetalleDTO(repository.save(nuevo));
    }

    /**
     * Modificación de cliente existente.
     * Actualiza únicamente nombre, email y teléfono, conservando id, fechaAlta y estado.
     * La entidad queda gestionada dentro de la transacción, por lo que los cambios
     * se persisten automáticamente al confirmar.
     */
    @Transactional
    public Optional<ClienteDetalleDTO> actualizar(long id, ClienteRequestDTO request) {
        return repository.findById(id).map(cliente -> {
            cliente.setNombre(request.nombre().trim());
            cliente.setEmail(request.email().trim());
            cliente.setTelefono(request.telefono().trim());
            return aDetalleDTO(cliente);
        });
    }

    /**
     * Baja lógica de cliente.
     * Modifica el estado a INACTIVO conservando el registro en la base de datos.
     * Es idempotente si el cliente ya se encuentra inactivo.
     */
    @Transactional
    public boolean darDeBaja(long id) {
        Optional<ClienteEntity> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return false;
        }
        ClienteEntity actual = opt.get();
        if (actual.getEstado() != EstadoCliente.INACTIVO) {
            actual.setEstado(EstadoCliente.INACTIVO);
        }
        return true;
    }

    private ClienteDTO aResumenDTO(ClienteEntity cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEstado());
    }

    private ClienteDetalleDTO aDetalleDTO(ClienteEntity cliente) {
        return new ClienteDetalleDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEstado(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getFechaAlta());
    }
}