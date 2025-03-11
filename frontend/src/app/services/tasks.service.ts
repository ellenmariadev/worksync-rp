import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ErrorMessage } from './types/error';
import { start } from 'repl';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/tasks';

  tasks: any[] = [];

  constructor(private http: HttpClient) {}

  createTask(
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
            console.log(errorMessage);
            resolve({ error: errorMessage });
          }
        );
    });
  }

  getTasksByProject(projectId: number): Observable<any[]> {
    const token = localStorage.getItem('token');

    if (!token) {
      console.log('Token não encontrado');
      return new Observable();
    }

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any[]>(`${this.apiUrl}/projects/${projectId}`, { headers });
  }

  deleteTask(id: number): Observable<void> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
  }
}
