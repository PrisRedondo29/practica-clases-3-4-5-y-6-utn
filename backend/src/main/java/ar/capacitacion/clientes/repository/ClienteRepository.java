package ar.capacitacion.clientes.repository;

import ar.capacitacion.clientes.entity.ClienteEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository de clientes respaldado por Oracle mediante Spring Data JPA.
 *
 * Las operaciones CRUD (save, findById, findAll, existsById, etc.) las provee
 * JpaRepository; los identificadores los genera la secuencia CLIENTES_SEQ.
 */
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    /**
     * Listado ordenado por ID ascendente, para conservar el orden natural
     * que ofrecía la implementación en memoria (ConcurrentSkipListMap).
     */
    List<ClienteEntity> findAllByOrderByIdAsc();
}