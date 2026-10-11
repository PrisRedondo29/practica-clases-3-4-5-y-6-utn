import { Navigate, Route, Routes } from "react-router";
import ClientesPage from "./pages/ClientesPage";
import { NotFoundPage } from "./pages/NotFoundPage";
import { ClienteDetallePage } from "./pages/ClienteDetallePage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/clientes" replace />} />
      <Route path="/clientes" element={<ClientesPage />} />
      <Route path="/clientes/:id" element={<ClienteDetallePage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
