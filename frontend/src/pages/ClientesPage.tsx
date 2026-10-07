import { useEffect, useState } from 'react';
import type { ClienteDTO } from '../types/ClienteDTO';
import { getClientes } from '../api/clientesapi';
import ClienteFiltro from '../components/ClienteFiltro';
import ClienteTable from '../components/ClienteTable';

function ClientesPage() {
  const [clientes, setClientes] = useState<ClienteDTO[]>([]);
  const [cargando, setCargando] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [filtro, setFiltro] = useState<string>('');

  useEffect(() => {
    let activo = true;

    getClientes()
      .then((datos) => {
        if (activo) {
          setClientes(datos);
        }
      })
      .catch(() => {
        if (activo) {
          setError('No se pudieron cargar los clientes.');
        }
      })
      .finally(() => {
        if (activo) {
          setCargando(false);
        }
      });

    return () => {
      activo = false;
    };
  }, []);

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

  return (
    <div>
      <h1>Clientes</h1>
      <ClienteFiltro valor={filtro} onCambio={handleCambioFiltro} />
      {cargando && <p>Cargando clientes...</p>}
      {!cargando && error !== null && <p role="alert">{error}</p>}
      {!cargando && error === null && clientes.length === 0 && (
        <section
          className="estado-vacio estado-vacio--sin-clientes"
          role="status"
        >
          <h2>Sin clientes registrados</h2>
          <p>Todavía no hay clientes registrados.</p>
        </section>
      )}
      {!cargando && error === null && clientes.length > 0 && clientesFiltrados.length === 0 && (
        <section
          className="estado-vacio estado-vacio--sin-coincidencias"
          role="status"
        >
          <h2>Sin coincidencias</h2>
          <p>Ningún cliente coincide con "{filtro}".</p>
        </section>
      )}
      {!cargando && error === null && clientesFiltrados.length > 0 && (
        <ClienteTable clientes={clientesFiltrados} />
      )}
    </div>
  );
}

export default ClientesPage;