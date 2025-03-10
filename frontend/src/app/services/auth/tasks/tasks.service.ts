import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ErrorMessage } from '../../types/error';
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
            resolve(response as Response);
          },
          (error) => {
            const errorMessage: ErrorMessage = error.error;
            resolve({ error: errorMessage });
          }
        );
    });
  }
}
