import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskService } from '../../../services/tasks.service';
import { ProjectsService } from '../../../services/project.service';  // Importe o serviço de Projetos
import { UserService } from '../../../services/user.service';  // Importe o serviço de Usuários (caso exista)
import { NavbarComponent } from '../../../components/navbar/navbar.component';

@Component({
  selector: 'app-edit-task',
  standalone: true,
  templateUrl: './edit-task.component.html',
  styleUrls: ['./edit-task.component.css'],
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent],
})
export class EditTaskComponent implements OnInit {
  taskForm: FormGroup;
  taskId!: number;
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  projects: any[] = [];  // Lista de projetos
  users: any[] = [];  // Lista de usuários

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router, // Injete o Router
    private taskService: TaskService,
    private projectsService: ProjectsService,  // Injete o serviço de Projetos
    private userService: UserService  // Injete o serviço de Usuários
  ) {
    this.taskForm = this.fb.group({
      title: [''],
      description: [''],
      projectId: [null],
      responsibleId: [null],
      status: [''],
      startDate: [''],
      endDate: [''],
      deadline: [''],
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('taskId');
      if (id) {
        this.taskId = +id;
        this.loadTask();
      }
    });

    this.loadProjects();  // Carregar projetos
    this.loadUsers();  // Carregar usuários
  }

  loadTask() {
    this.loading = true;
    this.taskService.getTaskById(this.taskId.toString()).subscribe({
      next: (task) => {
        this.taskForm.patchValue(task);
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar a tarefa:', err);
        this.errorMessage = 'Erro ao carregar a tarefa.';
        this.loading = false;
      },
    });
  }

  loadProjects() {
    this.projectsService.getAllProjects().subscribe({
      next: (projects) => {
        this.projects = projects;  // Preencher a lista de projetos
      },
      error: (err) => {
        console.error('Erro ao carregar projetos:', err);
      },
    });
  }

  loadUsers() {
    // Se você tiver um serviço de usuários (UserService), faça a chamada aqui
    // Caso contrário, você precisará ajustar o código conforme a sua implementação.
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;  // Preencher a lista de usuários
      },
      error: (err) => {
        console.error('Erro ao carregar usuários:', err);
      },
    });
  }

  async editTask() {
    if (!this.taskId) {
      this.errorMessage = 'ID da tarefa inválido.';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    try {
      await this.taskService.updateTask(this.taskId, this.taskForm.value);
      this.successMessage = 'Tarefa atualizada com sucesso!';
      this.loadTask();  // Carrega novamente a tarefa após a atualização
      // Navegue para a página de tarefas após salvar
      this.router.navigate(['/tasks']);  // Este é o caminho para a página de tarefas
    } catch (error) {
      this.errorMessage = 'Erro inesperado ao atualizar a tarefa.';
      console.error('Erro ao atualizar tarefa:', error);
    } finally {
      this.loading = false;
    }
  }
}
