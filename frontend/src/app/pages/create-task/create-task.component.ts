import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TaskService } from '../../services/auth/tasks/tasks.service';
import { Router } from '@angular/router';
import { translateError } from '../../utils/translateErrors';

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
  private taskService = inject(TaskService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  constructor() {
    this.taskForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      projectId: ['', Validators.required],
      status: ['NOT_STARTED', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      deadline: ['', Validators.required],
      responsibleId: ['', Validators.required]
    });
  }

  get f() {
    return this.taskForm.controls;
  }

  async onSubmit(): Promise<void> {
    if (this.taskForm.invalid) {
      this.errorMessage = 'Preencha todos os campos corretamente.';
      return;
    }

    const { title, description, status, startDate, endDate, deadline, responsibleId, projectId } = this.taskForm.value;

    try {
      const response = await this.taskService.createTask({
        title,
        description,
        status,
        startDate,
        endDate,
        deadline,
        responsibleId: parseInt(responsibleId),
        projectId: parseInt(projectId),
      }).toPromise(); 

      this.errorMessage = response?.error ? translateError(response.error.message) : '';

      if (!this.errorMessage) {
        alert('Tarefa criada com sucesso!');
        this.router.navigate(['/tasks']);
      }
    } catch (error) {
      this.errorMessage = 'Erro ao criar tarefa!';
    }
  }
}
