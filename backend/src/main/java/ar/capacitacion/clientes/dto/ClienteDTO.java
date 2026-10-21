package ar.capacitacion.clientes.dto;

import ar.capacitacion.clientes.entity.EstadoCliente;

/** Contrato resumido para el listado de clientes. */
public record ClienteDTO(long id, String nombre, EstadoCliente estado) {
}
