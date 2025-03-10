import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/tasks';

  tasks: any[] = [];

  constructor(private http: HttpClient) {}

  createTask(taskData: any): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(`${this.apiUrl}`, taskData, { headers });
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
