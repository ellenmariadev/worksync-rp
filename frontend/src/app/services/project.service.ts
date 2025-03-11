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

<<<<<<< HEAD
  updateProject(
    id: number,
    title: string,
    description: string
  ): Observable<ProjectDTO> {
=======
  updateProject(id: number, title: string, description: string): Observable<ProjectDTO> {
>>>>>>> d82e49520d45db3045232361e96e5c049a1bb9a7
    const updatedProject = { title, description };
    return this.http.patch<ProjectDTO>(`${this.apiUrl}/${id}`, updatedProject);
  }

<<<<<<< HEAD
  deleteProject(id: number) {
    const headers = new HttpHeaders().set(
      'Authorization',
      `Bearer ${localStorage.getItem('token')}`
    );
    return this.http.delete(`http://localhost:8080/projects/${id}`, {
      headers,
    });
=======
  deleteProject(id: number): Observable<void> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('token')}`);
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
>>>>>>> d82e49520d45db3045232361e96e5c049a1bb9a7
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
<<<<<<< HEAD

=======
>>>>>>> d82e49520d45db3045232361e96e5c049a1bb9a7
}
