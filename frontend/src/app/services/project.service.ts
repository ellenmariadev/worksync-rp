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

  updateProject(id: number, title: string, description: string): Observable<ProjectDTO> {
    const updatedProject = { title, description };
    return this.http.put<ProjectDTO>(`${this.apiUrl}/${id}`, updatedProject);
  }

  deleteProject(id: number) {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    return this.http.delete(`http://localhost:8080/projects/${id}`, { headers });
  }
}
