package ar.capacitacion.clientes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

/**
 * Persistencia en memoria de clientes.
 *
 * Utiliza ConcurrentSkipListMap para ordenar los clientes de forma natural por ID,
 * garantizando operaciones thread-safe sin necesidad de bloqueos globales y evitando
 * la sobrecarga de copias completas de arrays (como ocurriría con CopyOnWriteArrayList).
 */
@Repository
public class ClienteRepository {

    private static final List<ClienteDetalleDTO> DATOS_INICIALES = List.of(
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

    private final ConcurrentMap<Long, ClienteDetalleDTO> clientes = new ConcurrentSkipListMap<>();
    private final AtomicLong contadorId = new AtomicLong();

    public ClienteRepository() {
        reset();
    }

    /**
     * Restablece los datos al estado inicial determinista.
     * Facilita el aislamiento de pruebas automatizadas.
     */
    public synchronized void reset() {
        clientes.clear();
        for (ClienteDetalleDTO c : DATOS_INICIALES) {
            clientes.put(c.id(), c);
        }
        long maxId = DATOS_INICIALES.stream().mapToLong(ClienteDetalleDTO::id).max().orElse(0L);
        contadorId.set(maxId);
    }

    public List<ClienteDTO> findAll() {
        return clientes.values().stream()
                .map(cliente -> new ClienteDTO(cliente.id(), cliente.nombre(), cliente.estado()))
                .toList();
    }

    public Optional<ClienteDetalleDTO> findById(long id) {
        return Optional.ofNullable(clientes.get(id));
    }

    public ClienteDetalleDTO save(ClienteDetalleDTO cliente) {
        if (cliente.id() <= 0) {
            long nuevoId = contadorId.incrementAndGet();
            ClienteDetalleDTO nuevo = new ClienteDetalleDTO(
                    nuevoId,
                    cliente.nombre(),
                    cliente.estado(),
                    cliente.email(),
                    cliente.telefono(),
                    cliente.fechaAlta());
            clientes.put(nuevoId, nuevo);
            return nuevo;
        } else {
            clientes.put(cliente.id(), cliente);
            return cliente;
        }
    }

    public boolean existsById(long id) {
        return clientes.containsKey(id);
    }
}
