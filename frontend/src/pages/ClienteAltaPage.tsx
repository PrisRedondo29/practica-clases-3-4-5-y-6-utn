import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createCliente } from '../api/clientesapi';
import { ClienteForm, ClienteFormData } from "../components/ClienteForm";

const valoresVacios: ClienteFormData = { nombre: '', email: '', telefono: '' };

export function ClienteAltaPage() {
  const queryClient = useQueryClient();
  // Cambiar la key vuelve a montar el formulario y lo deja vacío
  const [formKey, setFormKey] = useState(0);

  const mutation = useMutation({
    mutationFn: createCliente,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['clientes'] });
      setFormKey((k) => k + 1);
    },
  });

  function handleCancelar() {
    mutation.reset();
    setFormKey((k) => k + 1);
  }

  return (
    <section>
      <h2>Alta de cliente</h2>

      <ClienteForm
        key={formKey}
        defaultValues={valoresVacios}
        onSubmit={(datos) => mutation.mutate(datos)}
        onCancel={handleCancelar}
        isSaving={mutation.isPending}
        submitError={mutation.error?.message}
      />

      {mutation.isSuccess && <p role="status">Cliente creado correctamente.</p>}
    </section>
  );
}

export default ClienteAltaPage;