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
 * Entidad JPA que representa la tabla CLIENTES (migración Flyway V1).
 * El esquema lo gestiona Flyway; Hibernate solo lo valida.
 */
@Entity
@Table(name = "CLIENTES")
public class ClienteEntity {

    // allocationSize = 1 debe coincidir con INCREMENT BY 1 de CLIENTES_SEQ.
    // Con el valor por defecto de Hibernate (50) la validación del esquema falla.
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientes_seq_gen")
    @SequenceGenerator(name = "clientes_seq_gen", sequenceName = "CLIENTES_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoCliente estado;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "TELEFONO", nullable = false, length = 30)
    private String telefono;

    @Column(name = "FECHA_ALTA", nullable = false, columnDefinition = "DATE")
    private LocalDate fechaAlta;

    /** Requerido por JPA. */
    protected ClienteEntity() {
    }

    public ClienteEntity(String nombre, EstadoCliente estado, String email,
                         String telefono, LocalDate fechaAlta) {
        this.nombre = nombre;
        this.estado = estado;
        this.email = email;
        this.telefono = telefono;
        this.fechaAlta = fechaAlta;
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

    public EstadoCliente getEstado() {
        return estado;
    }

    public void setEstado(EstadoCliente estado) {
        this.estado = estado;
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

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}