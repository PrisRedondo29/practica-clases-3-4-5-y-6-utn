package ar.capacitacion.clientes.repository;

import ar.capacitacion.clientes.entity.SolicitudAltaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository Spring Data JPA para la entidad SolicitudAltaEntity.
 */
public interface SolicitudAltaRepository extends JpaRepository<SolicitudAltaEntity, Long> {

    /**
     * Listado ordenado por ID ascendente para garantizar orden determinista en la demostración.
     */
    List<SolicitudAltaEntity> findAllByOrderByIdAsc();
}
