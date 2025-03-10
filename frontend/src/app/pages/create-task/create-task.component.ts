import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TaskService } from '../../services/tasks.service';
import { Router } from '@angular/router';
import { translateError } from '../../utils/translateErrors';
import { ProjectsService } from '../../services/project.service';
import { ProjectDTO } from '../../services/types/project';
import { AuthService } from '../../services/auth/auth.service';
import { UserDTO } from '../../services/types/user';

@Component({
  selector: 'app-create-task',
  standalone: true,
  templateUrl: './create-task.component.html',
  styleUrls: ['./create-task.component.css'],
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent],
})
export class CreateTaskComponent {
  taskForm: FormGroup;
  errorMessage: string = '';
  projects: ProjectDTO[] = [];
  users: UserDTO[] = [];

  private tasksService = inject(TaskService);
  private projectsService = inject(ProjectsService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  constructor() {
    this.taskForm = this.fb.group({
      title: [''],
      description: [''],
      projectId: [''],
      status: ['NOT_STARTED'],
      startDate: [''],
      endDate: [''],
      deadline: [''],
      responsibleId: [''],
    });
  }

  get f() {
    return this.taskForm.controls;
  }

  ngOnInit(): void {
    this.projectsService.getAllProjects().subscribe(
      (data) => {
        this.projects = data;
      },
      (error) => {
        console.error('Erro ao carregar projetos', error);
      }
    );

    this.authService.getAllUsers().subscribe(
      (data) => {
        this.users = data;
      },
      (error) => {
        console.error('Erro ao carregar usuários', error);
      }
    );
  }

  async onSubmit(): Promise<void> {
    if (this.taskForm.invalid) {
      this.errorMessage = 'Preencha todos os campos corretamente.';
      return;
    }

    const {
      title,
      description,
      status,
      startDate,
      endDate,
      deadline,
      responsibleId,
      projectId,
    } = this.taskForm.value;

    const response = await this.tasksService.register(
      title,
      description,
      status,
      startDate,
      endDate,
      deadline,
      parseInt(projectId),
      parseInt(projectId)
    );

    this.errorMessage =
      'error' in response ? translateError(response.error.message) : '';
    if (!this.errorMessage) {
      alert('Tarefa criada com sucesso!');
      this.router.navigate(['/tasks']);
    }
  }
}
