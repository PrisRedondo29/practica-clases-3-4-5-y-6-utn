import { ClienteDetalleDTO } from '../types/ClienteDetalleDTO';
import type { ClienteDTO } from '../types/ClienteDTO';
import { ClienteUpdateRequest } from '../types/ClienteUpdateRequest';

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

export async function updateCliente( id: number, datos: ClienteUpdateRequest): Promise<ClienteDetalleDTO> {
  const response = await fetch(
    `${import.meta.env.VITE_API_URL}/api/clientes/${id}`,
    {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos),
    },
  );

  if (!response.ok) {
    throw new Error(`No se pudo actualizar el cliente (HTTP ${response.status})`);
  }

  return (await response.json()) as ClienteDetalleDTO;
}

import type { ClienteFormData } from '../components/ClienteForm';

export async function createCliente(datos: ClienteFormData): Promise<void> {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/api/clientes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos),
  });

  if (!response.ok) {
    throw new Error('No se pudo crear el cliente.');
  }
}

// Baja lógica: el servidor cambia el estado a INACTIVO
export async function deleteCliente(id: number): Promise<void> {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/api/clientes/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw new Error('No se pudo dar de baja el cliente.');
  }
}