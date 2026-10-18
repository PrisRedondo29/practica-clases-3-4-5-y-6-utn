import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Link, useParams } from "react-router";
import { getCliente, updateCliente } from "../api/clientesapi"; // ajustar a la ruta real
import type { ClienteDetalleDTO } from "../types/ClienteDetalleDTO"; // ajustar a la ruta real
import { ClienteForm } from "../components/ClienteForm";
import { ClienteUpdateRequest } from "../types/ClienteUpdateRequest";

export function ClienteDetallePage() {
  const { id } = useParams();
  const clienteId = parseClienteId(id);
  const [editando, setEditando] = useState(false);
  const queryClient = useQueryClient();

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
  
  const actualizar = useMutation({
    mutationFn: (datos: ClienteUpdateRequest) => {
      if (clienteId === null) {
        return Promise.reject(new Error('ID de cliente inválido'));
      }
      return updateCliente(clienteId, datos);
    },
    onSuccess: async () => {
      if (clienteId !== null) {
        await queryClient.invalidateQueries({ queryKey: ['cliente', clienteId] });
      }
      await queryClient.invalidateQueries({ queryKey: ['clientes'] });
      setEditando(false);
    },
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

          {isSuccess && data && !editando && (
            <>
              <dl>
                {Object.entries(data).map(([campo, valor]) => (
                  <div key={campo}>
                    <dt>{campo}</dt>
                    <dd>{formatearValor(valor)}</dd>
                  </div>
                ))}
              </dl>
              <button type="button" onClick={() => setEditando(true)}>
                Editar
              </button>
            </>
          )}

          {/* Se renderiza según "data" (no según isSuccess) para que el
              formulario no se desmonte ni pierda lo escrito ante un refetch. */}
          {data && editando && (
            <ClienteForm
              defaultValues={{
                nombre: data.nombre,
                email: data.email,
                telefono: data.telefono,
              }}
              onSubmit={(datos) => {actualizar.mutate(datos);}}
              onCancel={() => setEditando(false)}
              isSaving={actualizar.isPending}
              submitError={actualizar.isError ? actualizar.error.message : null}
            />
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