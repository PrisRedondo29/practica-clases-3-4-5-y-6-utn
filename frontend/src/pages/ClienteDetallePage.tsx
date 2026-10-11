import { useEffect, useState } from "react";
import { Link, useParams } from "react-router";
import { getCliente } from "../api/clientesapi"; // ajustar a la ruta real
import type { ClienteDetalleDTO } from "../types/ClienteDetalleDTO"; // ajustar a la ruta real

type Estado =
  | { tipo: "loading" }
  | { tipo: "exito"; cliente: ClienteDetalleDTO }
  | { tipo: "no-encontrado" }
  | { tipo: "error" };

export function ClienteDetallePage() {
  const { id } = useParams();
  const clienteId = parseClienteId(id);
  const [estado, setEstado] = useState<Estado>({ tipo: "loading" });

  useEffect(() => {
    if (clienteId === null) {
      return;
    }

    let cancelado = false;
    setEstado({ tipo: "loading" });

    getCliente(clienteId)
      .then((cliente) => {
        if (cancelado) return;
        setEstado(
          cliente === null
            ? { tipo: "no-encontrado" }
            : { tipo: "exito", cliente },
        );
      })
      .catch(() => {
        if (!cancelado) setEstado({ tipo: "error" });
      });

    return () => {
      cancelado = true;
    };
  }, [clienteId]);

  return (
    <main>
      <h2>Detalle del cliente</h2>

      {clienteId === null ? (
        <p>El id del cliente no es válido.</p>
      ) : (
        <>
          {estado.tipo === "loading" && <p>Cargando cliente...</p>}
          {estado.tipo === "no-encontrado" && <p>Cliente no encontrado.</p>}
          {estado.tipo === "error" && (
            <p>No se pudo cargar el cliente. Intentá nuevamente.</p>
          )}
          {estado.tipo === "exito" && (
            <dl>
              {Object.entries(estado.cliente).map(([campo, valor]) => (
                <div key={campo}>
                  <dt>{campo}</dt>
                  <dd>{formatearValor(valor)}</dd>
                </div>
              ))}
            </dl>
          )}
        </>
      )}

      <Link to="/clientes">Volver a clientes</Link>
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