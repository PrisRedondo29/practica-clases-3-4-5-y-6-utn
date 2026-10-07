package ar.capacitacion.clientes;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/** Datos in-memory, deterministas. */
@Repository
public class ClienteRepository {

    private static final List<ClienteDTO> DATOS = List.of(
            new ClienteDTO(1, "Ana", EstadoCliente.ACTIVO),
            new ClienteDTO(2, "Bruno", EstadoCliente.INACTIVO),
            new ClienteDTO(3, "Carla", EstadoCliente.ACTIVO),
            new ClienteDTO(4, "Diego", EstadoCliente.ACTIVO),
            new ClienteDTO(5, "Elena", EstadoCliente.INACTIVO));

    public List<ClienteDTO> findAll() {
        return DATOS;
    }

    public Optional<ClienteDTO> findById(long id) {
        return DATOS.stream().filter(c -> c.id() == id).findFirst();
    }
}
