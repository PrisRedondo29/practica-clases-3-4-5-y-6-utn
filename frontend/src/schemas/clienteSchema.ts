import { z } from "zod";

/**
 * Espeja las validaciones de ClienteRequestDTO.java.
 * Se aplica trim() antes de validar, por lo que el valor enviado queda sin
 * espacios en los extremos y siempre cumple también las reglas del backend.
 */
export const clienteSchema = z.object({
  nombre: z
    .string()
    .trim()
    .min(1, "El nombre es obligatorio")
    .min(2, "El nombre debe tener entre 2 y 100 caracteres")
    .max(100, "El nombre debe tener entre 2 y 100 caracteres"),

  email: z
    .string()
    .trim()
    .min(1, "El email es obligatorio")
    .email("El email debe tener un formato válido")
    .max(100, "El email no puede superar los 100 caracteres"),

  telefono: z
    .string()
    .trim()
    .min(1, "El teléfono es obligatorio")
    .min(6, "El teléfono debe tener entre 6 y 30 caracteres")
    .max(30, "El teléfono debe tener entre 6 y 30 caracteres"),
});

export type ClienteFormValues = z.infer<typeof clienteSchema>;