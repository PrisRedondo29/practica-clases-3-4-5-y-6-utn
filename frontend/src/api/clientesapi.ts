import { ClienteDetalleDTO } from '../types/ClienteDetalleDTO';
import type { ClienteDTO } from '../types/ClienteDTO';

const baseUrl = import.meta.env.VITE_API_URL;

export async function getClientes(): Promise<ClienteDTO[]> {
  const response = await fetch(`${baseUrl}/api/clientes`);

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }

  return response.json();
}

export async function getCliente( id: number ): Promise<ClienteDetalleDTO | null> {
  const response = await fetch(`${baseUrl}/api/clientes/${id}`);

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }

  return response.json();
}