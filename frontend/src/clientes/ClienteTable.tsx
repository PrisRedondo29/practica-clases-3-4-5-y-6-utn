import type { ClienteDTO } from "./Clientedto";
import { ClienteRow } from "./ClienteRow";

interface ClienteTableProps {
  clientes: ClienteDTO[];
}

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
        {clientes.map((c) => (
          <ClienteRow key={c.id} cliente={c} />
        ))}
      </tbody>
    </table>
  );
}