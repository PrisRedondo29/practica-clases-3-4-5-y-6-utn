export type ClienteDetalleDTO = {
  id: number;
  nombre: string;
  estado: "ACTIVO" | "INACTIVO";
  email: string;
  telefono: string;
  fechaAlta: string; // o el tipo adecuado para una fecha
};