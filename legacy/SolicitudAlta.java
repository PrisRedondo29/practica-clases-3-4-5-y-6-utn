package ar.capacitacion.legacy;

import java.sql.Date;

/**
 * Modelo representativo de la tabla SOLICITUDES_ALTA en el sistema legacy.
 * Material de análisis para la capacitación (no ejecutable directamente).
 */
public class SolicitudAlta {

    private long id;
    private String nombre;
    private String email;
    private String telefono;
    private String estado;
    private Date fechaCreacion;
    private Date fechaResolucion;
    private Long clienteId;

    public SolicitudAlta() {
    }

    public SolicitudAlta(long id, String nombre, String email, String telefono,
                         String estado, Date fechaCreacion, Date fechaResolucion, Long clienteId) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaResolucion = fechaResolucion;
        this.clienteId = clienteId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(Date fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
}
