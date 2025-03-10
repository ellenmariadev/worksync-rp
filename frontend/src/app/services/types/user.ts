export type User = {
    id: number;
    email: string;
    password: string;
    role: string;
    sub?: string;
}

export type ErrorMessage = {
  message: string;
};
