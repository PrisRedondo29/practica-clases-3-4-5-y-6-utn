package ar.capacitacion.clientes;

import java.time.LocalDate;

/** Contrato JSON del detalle, con datos adicionales al resumen del listado. */
public record ClienteDetalleDTO(
        long id,
        String nombre,
        EstadoCliente estado,
        String email,
        String telefono,
        LocalDate fechaAlta) {
}
