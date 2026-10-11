import { Link } from 'react-router';
import type { ClienteDTO } from '../types/ClienteDTO';

type ClienteRowProps = {
  cliente: ClienteDTO;
};

export function ClienteRow({ cliente }: ClienteRowProps) {
  return (
    <tr>
      <td>{cliente.nombre}</td>
      <td>{cliente.estado}</td>
      <td>
        <Link to={`/clientes/${cliente.id}`}>Abrir</Link>
      </td>
    </tr>
  );
}
export default ClienteRow;