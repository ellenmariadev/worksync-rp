import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TaskService } from '../../services/tasks/tasks.service';
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
  private authService = inject(TaskService);
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
      responsibleId: ['']
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

      const response = await this.authService.register(
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
