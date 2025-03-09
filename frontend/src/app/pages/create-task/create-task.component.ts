import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component'; // Importação do Navbar

@Component({
  selector: 'app-create-task',
  standalone: true, // Define como um Standalone Component
  templateUrl: './create-task.component.html',
  styleUrls: ['./create-task.component.css'],
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent], // Importa os módulos necessários
})
export class CreateTaskComponent {
  taskForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.taskForm = this.fb.group({
      title: [''],
      description: [''],
      project: ['Team projects'],
      assignedUser: ['User 2'],
      status: ['Em andamento'],
    });
  }

  createTask() {
    console.log('Tarefa criada:', this.taskForm.value);
  }
}
