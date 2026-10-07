package ar.capacitacion.clientes;

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

    public Optional<ClienteDTO> buscarPorId(long id) {
        return repository.findById(id);
    }
}
