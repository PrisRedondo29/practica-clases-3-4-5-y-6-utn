import { Link } from "react-router";

export function NotFoundPage() {
  return (
    <main>
      <h2>Página no encontrada</h2>
      <p>La dirección que buscás no existe en esta aplicación.</p>
      <Link to="/clientes">Volver a clientes</Link>
    </main>
  );
}