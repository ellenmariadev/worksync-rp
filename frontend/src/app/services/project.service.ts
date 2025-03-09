import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectDTO } from '../services/types/project';  // Importar os tipos de projeto

@Injectable({
  providedIn: 'root',
})
export class ProjectsService {
  private apiUrl = 'http://localhost:8080/projects'; // URL da API de projetos

  constructor(private http: HttpClient) {}

  // Função para obter todos os projetos
  getAllProjects(title?: string): Observable<ProjectDTO[]> {
    const params = title ? { title } : undefined;
    return this.http.get<ProjectDTO[]>(this.apiUrl, { params });
  }

  // Função para editar um projeto
  updateProject(id: number, title: string, description: string): Observable<ProjectDTO> {
    const updatedProject = { title, description };
    return this.http.put<ProjectDTO>(`${this.apiUrl}/${id}`, updatedProject);
  }

  // Função para excluir um projeto
  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
