import { useQuery } from "@tanstack/react-query";
import { Link, useParams } from "react-router";
import { getCliente } from "../api/clientesapi"; // ajustar a la ruta real
import type { ClienteDetalleDTO } from "../types/ClienteDetalleDTO"; // ajustar a la ruta real

export function ClienteDetallePage() {
  const { id } = useParams();
  const clienteId = parseClienteId(id);

  const { data, isPending, isError, isSuccess } =
    useQuery<ClienteDetalleDTO | null>({
      queryKey: ["cliente", clienteId],
      queryFn: () => {
        if (clienteId === null) {
          throw new Error("El id del cliente no es válido.");
        }
        return getCliente(clienteId);
      },
      enabled: clienteId !== null,
      staleTime: 60_000, // 1 minuto
    });

  return (
    <main>
      <h2>Detalle del cliente</h2>

      {clienteId === null ? (
        <p>El id del cliente no es válido.</p>
      ) : (
        <>
          {isPending && <p>Cargando cliente...</p>}
          {isError && <p>No se pudo cargar el cliente. Intentá nuevamente.</p>}
          {isSuccess && data === null && <p>Cliente no encontrado.</p>}
          {isSuccess && data !== null && (
            <dl>
              {Object.entries(data).map(([campo, valor]) => (
                <div key={campo}>
                  <dt>{campo}</dt>
                  <dd>{formatearValor(valor)}</dd>
                </div>
              ))}
            </dl>
          )}
        </>
      )}

      <Link to="/clientes">Volver a la lista de clientes</Link>
    </main>
  );
}

function formatearValor(valor: unknown): string {
  if (valor === null || valor === undefined) {
    return "—";
  }

  if (typeof valor === "object") {
    return JSON.stringify(valor);
  }

  return String(valor);
}

function parseClienteId(rawId: string | undefined): number | null {
  const id = Number(rawId);

  if (!Number.isInteger(id) || id <= 0) {
    return null;
  }

  return id;
}