// Dependencias: npm install react-hook-form zod @hookform/resolvers
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { clienteSchema } from "../schemas/clienteSchema";


export type ClienteFormData = z.infer<typeof clienteSchema>;

type ClienteFormProps = {
  defaultValues: ClienteFormData;
  onSubmit: (datos: ClienteFormData) => void;
  onCancel: () => void;
  isSaving?: boolean;
  submitError?: string | null;
};

export function ClienteForm({defaultValues, onSubmit, onCancel, isSaving, submitError}: ClienteFormProps) {
  const { register, handleSubmit, formState: { errors }} = useForm<ClienteFormData>({
    resolver: zodResolver(clienteSchema),
    defaultValues,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <div>
        <label htmlFor="nombre">Nombre</label>
        <input id="nombre" disabled={isSaving} {...register("nombre")} />
        {errors.nombre && <p role="alert">{errors.nombre.message}</p>}
      </div>

      <div>
        <label htmlFor="email">Email</label>
        <input id="email" type="email" disabled={isSaving} {...register("email")} />
        {errors.email && <p role="alert">{errors.email.message}</p>}
      </div>

      <div>
        <label htmlFor="telefono">Teléfono</label>
        <input id="telefono" disabled={isSaving} {...register("telefono")} />
        {errors.telefono && <p role="alert">{errors.telefono.message}</p>}
      </div>

      {submitError && <p role="alert">{submitError}</p>}

      <button type="submit" disabled={isSaving}>
        {isSaving ? "Guardando..." : "Guardar"}
      </button>
      <button type="button" onClick={onCancel} disabled={isSaving}>
        Cancelar
      </button>
    </form>
  );
}