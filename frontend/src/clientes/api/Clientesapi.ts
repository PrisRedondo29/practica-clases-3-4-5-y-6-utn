import type { ClienteDTO } from "../Clientedto";

const API_URL: string = import.meta.env.VITE_API_URL;

export async function getClientes(): Promise<ClienteDTO[]> {
  const response = await fetch(`${API_URL}/api/clientes`);

  if (!response.ok) {
    throw new Error(`Error ${response.status} al obtener los clientes.`);
  }

  return (await response.json()) as ClienteDTO[];
}