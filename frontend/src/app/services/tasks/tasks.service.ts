import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ErrorMessage } from '../types/error';
import { start } from 'repl';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/tasks';

  constructor(private http: HttpClient) {}

  register(
    title: string,
    description: string,
    status: string,
    startDate: string,
    endDate: string,
    deadline: string,
    responsibleId: number,
    projectId: number
  ): Promise<{ error: ErrorMessage } | Response> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return new Promise((resolve, _) => {
      this.http
        .post(
          `${this.apiUrl}`,
          {
            title,
            description,
            status,
            startDate,
            endDate,
            deadline,
            responsibleId,
            projectId,
          },
          { headers }
        )
        .subscribe(
          (response) => {
            console.log(response);
            resolve(response as Response);
          },
          (error) => {
            console.log(error);
            const errorMessage: ErrorMessage = error.error;
            resolve({ error: errorMessage });
          }
        );
    });
  }

  // async login(
  //   email: string,
  //   password: string
  // ): Promise<{ error: ErrorMessage } | Response> {
  //   const response = await fetch(`${this.apiUrl}/login`, {
  //     method: 'POST',
  //     headers: { 'Content-Type': 'application/json' },
  //     body: JSON.stringify({ email, password }),
  //   });

  //   if (!response.ok) {
  //     const errorMessage = await response.json();
  //     return { error: errorMessage as ErrorMessage };
  //   }

  //   const data = await response.json();
  //   const decodedToken = jwtDecode(data.token);
  //   localStorage.setItem('token', data.token);
  //   localStorage.setItem('user', JSON.stringify(decodedToken));
  //   return response;
  // }

  // logout() {
  //   localStorage.removeItem('token');
  //   localStorage.removeItem('user');
  // }

  // isAuthenticated(): boolean {
  //   if (typeof localStorage === 'undefined') {
  //     return false;
  //   }
  //   return !!localStorage.getItem('token');
  // }

  // getUser(): User | null {
  //   const user = localStorage.getItem('user');
  //   return user ? (JSON.parse(user) as User) : null;
  // }
}
