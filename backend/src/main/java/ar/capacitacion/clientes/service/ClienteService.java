package ar.capacitacion.clientes.service;

import ar.capacitacion.clientes.dto.*;
import ar.capacitacion.clientes.entity.EstadoCliente;
import ar.capacitacion.clientes.repository.ClienteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<ClienteDTO> listar() {
        return repository.findAll();
    }

    public Optional<ClienteDetalleDTO> buscarPorId(long id) {
        return repository.findById(id);
    }

    /**
     * Alta de cliente con ID autogenerado, fecha de alta actual y estado inicial ACTIVO.
     */
    public ClienteDetalleDTO crear(ClienteRequestDTO request) {
        ClienteDetalleDTO nuevo = new ClienteDetalleDTO(
                0,
                request.nombre().trim(),
                EstadoCliente.ACTIVO,
                request.email().trim(),
                request.telefono().trim(),
                LocalDate.now());
        return repository.save(nuevo);
    }

    /**
     * Modificación de cliente existente.
     * Actualiza únicamente nombre, email y teléfono, conservando id, fechaAlta y estado.
     */
    public Optional<ClienteDetalleDTO> actualizar(long id, ClienteRequestDTO request) {
        return repository.findById(id).map(actual -> {
            ClienteDetalleDTO modificado = new ClienteDetalleDTO(
                    actual.id(),
                    request.nombre().trim(),
                    actual.estado(),
                    request.email().trim(),
                    request.telefono().trim(),
                    actual.fechaAlta());
            return repository.save(modificado);
        });
    }

    /**
     * Baja lógica de cliente.
     * Modifica el estado a INACTIVO conservando el registro en memoria.
     * Es idempotente si el cliente ya se encuentra inactivo.
     */
    public boolean darDeBaja(long id) {
        Optional<ClienteDetalleDTO> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return false;
        }
        ClienteDetalleDTO actual = opt.get();
        if (actual.estado() != EstadoCliente.INACTIVO) {
            ClienteDetalleDTO inactivo = new ClienteDetalleDTO(
                    actual.id(),
                    actual.nombre(),
                    EstadoCliente.INACTIVO,
                    actual.email(),
                    actual.telefono(),
                    actual.fechaAlta());
            repository.save(inactivo);
        }
        return true;
    }
}
