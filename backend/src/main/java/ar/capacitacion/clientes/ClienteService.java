package ar.capacitacion.clientes;

import java.util.List;

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
}
