package ar.capacitacion.clientes;

/** Contrato resumido para el listado de clientes. */
public record ClienteDTO(long id, String nombre, EstadoCliente estado) {
}
