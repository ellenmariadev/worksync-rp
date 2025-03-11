export type User = {
    id: number;
    email: string;
    password: string;
    name: string;
    role: string;
    sub?: string;
}

export type ErrorMessage = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
}