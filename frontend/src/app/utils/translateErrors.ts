const mapErrors: Record<string, string> = {
  'Invalid username or password': 'Usuário ou senha inválidos.',
  'Email already taken': 'E-mail já cadastrado.',
};


export function translateError(errorMessage: string): string {
  return mapErrors[errorMessage] || "Erro! Por favor, tente novamente.";
}