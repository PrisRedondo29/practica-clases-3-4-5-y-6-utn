import type { ClienteDTO } from '../types/ClienteDTO';
import { ClienteRow } from './ClienteRow';

type ClienteTableProps = {
  clientes: ClienteDTO[];
};

export function ClienteTable({ clientes }: ClienteTableProps) {
  if (clientes.length === 0) {
    return <p>No hay clientes.</p>;
  }

  return (
    <table>
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Estado</th>
        </tr>
      </thead>

      <tbody>
        {clientes.map(cliente => (
          <ClienteRow
            key={cliente.id}
            cliente={cliente}
          />
        ))}
      </tbody>
    </table>
  );
}

export default ClienteTable;