package ar.capacitacion.clientes.dto;

import ar.capacitacion.clientes.entity.EstadoSolicitud;

import java.time.LocalDate;

/**
 * Contrato de respuesta para consultas y operaciones sobre solicitudes de alta.
 */
public record SolicitudAltaDTO(
        long id,
        String nombre,
        String email,
        String telefono,
        EstadoSolicitud estado,
        LocalDate fechaCreacion,
        LocalDate fechaResolucion,
        Long clienteId) {
}
