import { useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import type { ClienteDTO } from '../types/ClienteDTO';
import { getClientes } from '../api/clientesapi';
import ClienteFiltro from '../components/ClienteFiltro';
import ClienteTable from '../components/ClienteTable';

function ClientesPage() {
  const [filtro, setFiltro] = useState<string>('');
  const queryClient = useQueryClient();

  const { data, isPending, isError, isSuccess } = useQuery<ClienteDTO[]>({
    queryKey: ['clientes'],
    queryFn: getClientes,
    staleTime: 60_000, // 1 minuto
  });

  const clientes: ClienteDTO[] = data ?? [];

  const termino = filtro.trim().toLowerCase();
  const clientesFiltrados: ClienteDTO[] =
    termino === ''
      ? clientes
      : clientes.filter((cliente) =>
          cliente.nombre.toLowerCase().includes(termino)
        );

  const handleCambioFiltro = (valor: string): void => {
    setFiltro(valor);
  };

  const handleActualizar = (): void => {
    void queryClient.invalidateQueries({ queryKey: ['clientes'] });
  };

  return (
    <div>
      <h1>Clientes</h1>
      <ClienteFiltro valor={filtro} onCambio={handleCambioFiltro} />
      <button type="button" onClick={handleActualizar}>
        Actualizar clientes
      </button>
      {isPending && <p>Cargando clientes...</p>}
      {isError && <p role="alert">No se pudieron cargar los clientes.</p>}
      {isSuccess && clientes.length === 0 && (
        <section
          className="estado-vacio estado-vacio--sin-clientes"
          role="status"
        >
          <h2>Sin clientes registrados</h2>
          <p>Todavía no hay clientes registrados.</p>
        </section>
      )}
      {isSuccess && clientes.length > 0 && clientesFiltrados.length === 0 && (
        <section
          className="estado-vacio estado-vacio--sin-coincidencias"
          role="status"
        >
          <h2>Sin coincidencias</h2>
          <p>Ningún cliente coincide con "{filtro}".</p>
        </section>
      )}
      {isSuccess && clientesFiltrados.length > 0 && (
        <ClienteTable clientes={clientesFiltrados} />
      )}
    </div>
  );
}

export default ClientesPage;