package ar.capacitacion.clientes;

/** Contrato JSON: { "id": 1, "nombre": "Ana", "estado": "ACTIVO" } */
public record ClienteDTO(long id, String nombre, EstadoCliente estado) {
}
