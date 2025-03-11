export type User = {
    id: number;
    email: string;
    password: string;
    role: string;
    sub?: string;
}

export type Authority = {
  authority: string;
}

export type UserDTO = {
  id: number;
  role: string;
  authorities: Authority[];
  username: string;
  enabled: boolean;
  accountNonExpired: boolean;
  credentialsNonExpired: boolean;
  accountNonLocked: boolean;
}
