const mapErrors: Record<string, string> = {
  'Invalid username or password': 'Usuário ou senha inválidos.',
  'Email already taken': 'E-mail já cadastrado.',
  'Responsible person not found!': 'Pessoa responsável não encontrada!',
  'Project not found!': 'Projeto não encontrado!',
  'The given id must not be null': 'O id fornecido não pode ser nulo!',
  'Task not found!': 'Tarefa não encontrada!',
};


export function translateError(errorMessage: string): string {
  return mapErrors[errorMessage] || "Erro! Por favor, tente novamente.";
}
