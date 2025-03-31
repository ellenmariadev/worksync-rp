import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, lastValueFrom } from 'rxjs';
import { ErrorMessage } from './types/error';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('Token não encontrado. Usuário não autenticado.');
    }
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  async createTask(
    title: string,
    description: string,
    status: string,
    startDate: string,
    completionDate: string,
    deadline: string,
    responsibleId: number,
    projectId: number
  ): Promise<{ error?: ErrorMessage } | Response> {
    try {
      const response = await lastValueFrom(
        this.http.post(
          `${this.apiUrl}`,
          {
            title,
            description,
            status,
            startDate,
            completionDate,
            deadline,
            responsibleId,
            projectId,
          },
          { headers: this.getAuthHeaders() }
        )
      );
      console.log('Tarefa criada com sucesso:', response);
      return response as Response;
    } catch (error: any) {
      console.error(
        'Erro ao criar tarefa:',
        error.status,
        error.message,
        error.error
      );
      return { error: error.error };
    }
  }

  getTasksByProject(
    projectId: number,
    status?: string,
    startDate?: string,
    endDate?: string
  ): Observable<any[]> {
    let url = `${this.apiUrl}/projects/${projectId}`;
    const params: any = {};

    if (status) {
      params.status = status;
    }
    if (startDate) {
      params.startDate = startDate;
    }
    if (endDate) {
      params.endDate = endDate;
    }

    const queryParams = new URLSearchParams(params).toString();

    if (queryParams) {
      url = `${url}?${queryParams}`;
    }

    return this.http.get<any[]>(url, {
      headers: this.getAuthHeaders(),
    });
  }

  getSearchTasks(
    creatorId: string,
    assignedPersonId: string,
    statusFilter: string,
    startDate: string,
    endDate: string,
    searchQuery: string,
  ): Observable<any[]> {
    let url = `${this.apiUrl}/search`;
    const params: any = {};

    if (creatorId) {
      params.creatorId = creatorId;
    }

    if (assignedPersonId) {
      params.assignedPersonId = assignedPersonId;
    }

    if (statusFilter) {
      params.statusFilter = statusFilter;
    }
    
    if (startDate) {
      params.startDate = startDate;
    }
    
    if (endDate) {
      params.endDate = endDate;
    }

    if (searchQuery) {
      params.searchQuery = searchQuery;
    }

    const queryParams = new URLSearchParams(params).toString();

    if (queryParams) {
      url = `${url}?${queryParams}`;
    }

    return this.http.get<any[]>(url, {
      headers: this.getAuthHeaders(),
    });
  }

  getTaskById(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }

  async updateTask(
    id: number,
    updatedTask: any
  ): Promise<{ error?: ErrorMessage } | Response> {
    try {
      console.log('Atualizando tarefa...', updatedTask); // Log para depuração
      const response = await lastValueFrom(
        this.http.patch(`${this.apiUrl}/${id}`, updatedTask, {
          headers: this.getAuthHeaders(),
        })
      );
      console.log(`Tarefa ${id} atualizada com sucesso.`, response); // Log para verificar o sucesso da resposta
      return response as Response;
    } catch (error: any) {
      console.error(
        `Erro ao atualizar tarefa ${id}:`,
        error.status,
        error.message,
        error.error
      );
      return { error: error.error };
    }
  }
  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }
}
