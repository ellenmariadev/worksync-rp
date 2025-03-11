import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectDTO } from '../services/types/project';

@Injectable({
  providedIn: 'root',
})
export class ProjectsService {
  private apiUrl = 'http://localhost:8080/projects';

  constructor(private http: HttpClient) {}

  getAllProjects(title?: string): Observable<ProjectDTO[]> {
    const params = title ? { title } : undefined;
    return this.http.get<ProjectDTO[]>(this.apiUrl, { params });
  }

  getProjectById(id: number): Observable<ProjectDTO> {
    return this.http.get<ProjectDTO>(`${this.apiUrl}/${id}`);
  }

  updateProject(id: number, title: string, description: string): Observable<ProjectDTO> {
    const updatedProject = { title, description };
    return this.http.patch<ProjectDTO>(`${this.apiUrl}/${id}`, updatedProject);
  }

  deleteProject(id: number): Observable<void> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
  }

  createProject(title: string, description: string): Observable<ProjectDTO> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    const newProject = { title, description };
    return this.http.post<ProjectDTO>(this.apiUrl, newProject, { headers });
  }

  addParticipant(projectId: number, userId: number): Observable<ProjectDTO> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    return this.http.post<ProjectDTO>(`${this.apiUrl}/${projectId}/participants/${userId}`, {}, { headers });
  }
}
