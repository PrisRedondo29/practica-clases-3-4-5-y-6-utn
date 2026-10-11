package ar.capacitacion.clientes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/** Datos in-memory, deterministas. */
@Repository
public class ClienteRepository {

    private static final List<ClienteDetalleDTO> DATOS = List.of(
            new ClienteDetalleDTO(1, "Ana", EstadoCliente.ACTIVO, "ana@example.com", "11-4000-1001",
                    LocalDate.of(2022, 3, 15)),
            new ClienteDetalleDTO(2, "Bruno", EstadoCliente.INACTIVO, "bruno@example.com", "11-4000-1002",
                    LocalDate.of(2021, 7, 22)),
            new ClienteDetalleDTO(3, "Carla", EstadoCliente.ACTIVO, "carla@example.com", "11-4000-1003",
                    LocalDate.of(2023, 1, 10)),
            new ClienteDetalleDTO(4, "Diego", EstadoCliente.ACTIVO, "diego@example.com", "11-4000-1004",
                    LocalDate.of(2020, 11, 5)),
            new ClienteDetalleDTO(5, "Elena", EstadoCliente.INACTIVO, "elena@example.com", "11-4000-1005",
                    LocalDate.of(2024, 5, 30)));

    public List<ClienteDTO> findAll() {
        return DATOS.stream()
                .map(cliente -> new ClienteDTO(cliente.id(), cliente.nombre(), cliente.estado()))
                .toList();
    }

    public Optional<ClienteDetalleDTO> findById(long id) {
        return DATOS.stream().filter(c -> c.id() == id).findFirst();
    }
}
