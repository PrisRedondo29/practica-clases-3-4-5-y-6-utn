import type { ClienteDTO } from '../types/ClienteDTO';

type ClienteRowProps = {
  cliente: ClienteDTO;
};

export function ClienteRow({ cliente }: ClienteRowProps) {
  return (
    <tr>
      <td>{cliente.nombre}</td>
      <td>{cliente.estado}</td>
    </tr>
  );
}
export default ClienteRow;