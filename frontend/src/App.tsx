import { Navigate, Route, Routes } from "react-router";
import ClientesPage from "./pages/ClientesPage";
import { NotFoundPage } from "./pages/NotFoundPage";
import { ClienteDetallePage } from "./pages/ClienteDetallePage";
import ClienteAltaPage from "./pages/ClienteAltaPage";
import ClienteBajaPage from "./pages/ClienteBajaPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/clientes" replace />} />
      <Route path="/clientes" element={<ClientesPage />} />
      <Route path="/clientes/:id" element={<ClienteDetallePage />} />
      <Route path="/clientes/alta" element={<ClienteAltaPage />} />
      <Route path="/clientes/baja" element={<ClienteBajaPage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
