export type EstadoCliente = "ACTIVO" | "INACTIVO";

export interface ClienteDTO {
  id: number;
  nombre: string;
  estado: EstadoCliente;
}