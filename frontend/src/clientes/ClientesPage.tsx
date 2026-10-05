import { useEffect, useState } from "react";
import { getClientes } from "./api/Clientesapi";
import type { ClienteDTO } from "./Clientedto";
import { ClienteFiltro } from "./ClienteFiltro";
import { ClienteTable } from "./ClienteTable";

export function ClientesPage() {
  const [filtro, setFiltro] = useState("");
  const [clientes, setClientes] = useState<ClienteDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Sincronización con la API al montar la pantalla.
  useEffect(() => {
    let cancelado = false;

    getClientes()
      .then((data) => {
        if (!cancelado) setClientes(data);
      })
      .catch((e: unknown) => {
        if (!cancelado) {
          setError(
            e instanceof Error ? e.message : "No se pudieron cargar los clientes."
          );
        }
      })
      .finally(() => {
        if (!cancelado) setLoading(false);
      });

    return () => {
      cancelado = true;
    };
  }, []);

  // Derivado de clientes + filtro: no se guarda en estado.
  const termino = filtro.trim().toLowerCase();
  const clientesFiltrados = clientes.filter((c) =>
    c.nombre.toLowerCase().includes(termino)
  );

  return (
    <main>
      <h1>Clientes</h1>
      <ClienteFiltro valor={filtro} onCambio={setFiltro} />

      {loading ? (
        <p>Cargando clientes...</p>
      ) : error !== null ? (
        <p role="alert">{error}</p>
      ) : (
        <ClienteTable clientes={clientesFiltrados} />
      )}
    </main>
  );
}