type ClienteFiltroProps = {
  valor: string;
  onCambio: (valor: string) => void;
};

export function ClienteFiltro({ valor, onCambio }: ClienteFiltroProps) {
  return (
    <label>
      Buscar
      <input
        value={valor}
        onChange={e => onCambio(e.target.value)}
      />
    </label>
  );
}
export default ClienteFiltro;