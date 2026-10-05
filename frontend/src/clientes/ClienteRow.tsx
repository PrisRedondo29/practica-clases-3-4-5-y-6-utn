import type { ClienteDTO } from "./Clientedto";

interface ClienteRowProps {
  cliente: ClienteDTO;
}

export function ClienteRow({ cliente }: ClienteRowProps) {
  return (
    <tr>
      <td>{cliente.nombre}</td>
      <td>{cliente.estado}</td>
    </tr>
  );
}