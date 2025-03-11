export function translateStatus(status: string): string {
  const statusMap: { [key: string]: string } = {
    'NOT_STARTED': 'Não Iniciada',
    'IN_PROGRESS': 'Em Progresso',
    'DONE': 'Concluída',
  };
  return statusMap[status] || status;
}
