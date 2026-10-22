package ar.capacitacion.clientes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Entidad JPA que representa una solicitud de alta de cliente en la tabla SOLICITUDES_ALTA.
 * Mapea el esquema gestionado por Flyway V4.
 */
@Entity
@Table(name = "SOLICITUDES_ALTA")
public class SolicitudAltaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitudes_alta_seq_gen")
    @SequenceGenerator(name = "solicitudes_alta_seq_gen", sequenceName = "SOLICITUDES_ALTA_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "TELEFONO", nullable = false, length = 30)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoSolicitud estado;

    @Column(name = "FECHA_CREACION", nullable = false, columnDefinition = "DATE")
    private LocalDate fechaCreacion;

    @Column(name = "FECHA_RESOLUCION", columnDefinition = "DATE")
    private LocalDate fechaResolucion;

    @Column(name = "CLIENTE_ID")
    private Long clienteId;

    /** Requerido por JPA. */
    protected SolicitudAltaEntity() {
    }

    public SolicitudAltaEntity(String nombre, String email, String telefono, LocalDate fechaCreacion) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
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

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDate fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
}
