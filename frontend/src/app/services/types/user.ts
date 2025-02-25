export type User = {
    id: number;
    email: string;
    password: string;
    role: string;
}

export type ErrorMessage = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
}