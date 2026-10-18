import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { ClienteDTO } from '../types/ClienteDTO';
import { deleteCliente, getClientes } from '../api/clientesapi';

export function ClienteBajaPage() {
  const queryClient = useQueryClient();
  const [seleccionadoId, setSeleccionadoId] = useState<number | null>(null);
  const [confirmando, setConfirmando] = useState(false);

  // Misma query que ClientesPage: comparten caché
  const { data, isPending, isError, isSuccess } = useQuery<ClienteDTO[]>({
    queryKey: ['clientes'],
    queryFn: getClientes,
    staleTime: 60_000, // 1 minuto
  });

  const activos = (data ?? []).filter((c) => c.estado === 'ACTIVO');
  const seleccionado = activos.find((c) => c.id === seleccionadoId);

  const mutation = useMutation({
    mutationFn: deleteCliente,
    onSuccess: async (_resultado, id) => {
    // Cambia el estado: se actualizan la lista y el detalle de ese cliente
    await Promise.all([
        queryClient.invalidateQueries({ queryKey: ['clientes'] }),
        queryClient.invalidateQueries({ queryKey: ['cliente', id] }),
    ]);
    setSeleccionadoId(null);
    setConfirmando(false);
    },
  });

  function handleSeleccionar(id: number) {
    setSeleccionadoId(id);
    setConfirmando(false);
    mutation.reset();
  }

  return (
    <section>
      <h2>Baja de cliente</h2>

      {isPending && <p>Cargando clientes...</p>}
      {isError && <p role="alert">No se pudieron cargar los clientes.</p>}
      {isSuccess && activos.length === 0 && <p>No hay clientes activos.</p>}

      {isSuccess && activos.length > 0 && (
        <fieldset disabled={mutation.isPending}>
          <legend>Seleccioná un cliente activo</legend>
          {activos.map((cliente) => (
            <div key={cliente.id}>
              <label>
                <input
                  type="radio"
                  name="clienteBaja"
                  checked={seleccionadoId === cliente.id}
                  onChange={() => handleSeleccionar(cliente.id)}
                />
                {cliente.nombre}
              </label>
            </div>
          ))}
        </fieldset>
      )}

      {seleccionado && !confirmando && (
        <button type="button" onClick={() => setConfirmando(true)}>
          Dar de baja
        </button>
      )}

      {seleccionado && confirmando && (
        <div>
          <p>
            ¿Confirmás la baja de {seleccionado.nombre}? Su estado pasará a
            INACTIVO.
          </p>
          <button
            type="button"
            onClick={() => mutation.mutate(seleccionado.id)}
            disabled={mutation.isPending}
          >
            {mutation.isPending ? 'Procesando...' : 'Confirmar'}
          </button>
          <button
            type="button"
            onClick={() => setConfirmando(false)}
            disabled={mutation.isPending}
          >
            Cancelar
          </button>
        </div>
      )}

      {mutation.isError && <p role="alert">{mutation.error.message}</p>}
      {mutation.isSuccess && (
        <p role="status">Cliente dado de baja correctamente.</p>
      )}
    </section>
  );
}

export default ClienteBajaPage;