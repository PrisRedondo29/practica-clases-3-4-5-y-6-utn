package ar.capacitacion.clientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar una nueva solicitud de alta de cliente.
 * Mantiene validaciones equivalentes a ClienteRequestDTO para preservar la coherencia.
 */
public record SolicitudAltaRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 100, message = "El email no puede superar los 100 caracteres")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(min = 6, max = 30, message = "El teléfono debe tener entre 6 y 30 caracteres")
        String telefono) {
}
