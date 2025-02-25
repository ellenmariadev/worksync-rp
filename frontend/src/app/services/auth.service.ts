import { Injectable } from '@angular/core';
import { User } from './user';
import { jwtDecode } from 'jwt-decode';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  register(
    name: string,
    email: string,
    password: string,
    role: string
  ): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.http
        .post(`${this.apiUrl}/register`, { name, email, password, role })
        .subscribe(
          (response) => {
            resolve(true);
          },
          (error) => {
            reject(false);
          }
        );
    });
  }

  async login(email: string, password: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.apiUrl}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error('Login failed');
      }

      const data = await response.json();
      const decodedToken = jwtDecode(data.token);
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(decodedToken));
      return true;
    } catch (error) {
      console.error('Error during login:', error);
      return false;
    }
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  isAuthenticated(): boolean {
    if (typeof localStorage === 'undefined') {
      return false;
    }
    return !!localStorage.getItem('token');
  }

  getUser(): User | null {
    const user = localStorage.getItem('user');
    return user ? (JSON.parse(user) as User) : null;
  }
}
