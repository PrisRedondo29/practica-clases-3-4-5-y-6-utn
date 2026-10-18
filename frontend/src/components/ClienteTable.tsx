// Dependencia: npm install @tanstack/react-table
import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table';
import { Link } from 'react-router';
import type { ClienteDTO } from '../types/ClienteDTO';

type ClienteTableProps = {
  clientes: ClienteDTO[];
};

// Definida fuera del componente para que la referencia sea estable
const columns: ColumnDef<ClienteDTO>[] = [
  {
    accessorKey: 'nombre',
    header: 'Nombre',
  },
  {
    accessorKey: 'estado',
    header: 'Estado',
  },
  {
    id: 'acciones',
    header: 'Acción',
    cell: ({ row }) => <Link to={`/clientes/${row.original.id}`}>Abrir</Link>,
  },
];

export function ClienteTable({ clientes }: ClienteTableProps) {
  const table = useReactTable({
    data: clientes,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getRowId: (cliente) => String(cliente.id),
  });

  return (
    <table>
      <thead>
        {table.getHeaderGroups().map((headerGroup) => (
          <tr key={headerGroup.id}>
            {headerGroup.headers.map((header) => (
              <th key={header.id}>
                {flexRender(header.column.columnDef.header, header.getContext())}
              </th>
            ))}
          </tr>
        ))}
      </thead>

      <tbody>
        {table.getRowModel().rows.map((row) => (
          <tr key={row.id}>
            {row.getVisibleCells().map((cell) => (
              <td key={cell.id}>
                {flexRender(cell.column.columnDef.cell, cell.getContext())}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default ClienteTable;