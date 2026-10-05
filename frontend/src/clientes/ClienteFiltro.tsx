interface ClienteFiltroProps {
  valor: string;
  onCambio: (valor: string) => void;
}

export function ClienteFiltro({ valor, onCambio }: ClienteFiltroProps) {
  return (
    <input
      type="text"
      value={valor}
      onChange={(e) => onCambio(e.target.value)}
      placeholder="Buscar por nombre"
    />
  );
}