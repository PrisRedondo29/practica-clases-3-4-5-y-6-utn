package ar.capacitacion.clientes.service;

import ar.capacitacion.clientes.entity.EstadoSolicitud;

/** La solicitud ya fue aprobada o rechazada y no admite una nueva resolución. */
public class SolicitudYaResueltaException extends RuntimeException {

    public SolicitudYaResueltaException(long id, EstadoSolicitud estadoActual) {
        super("La solicitud " + id + " ya fue resuelta (estado actual: " + estadoActual + ")");
    }
}