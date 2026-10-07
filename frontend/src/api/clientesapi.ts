import type { ClienteDTO } from '../types/ClienteDTO';

const baseUrl = import.meta.env.VITE_API_URL;

export async function getClientes(): Promise<ClienteDTO[]> {
  const response = await fetch(`${baseUrl}/api/clientes?scenario=error`);

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }

  return response.json();
}