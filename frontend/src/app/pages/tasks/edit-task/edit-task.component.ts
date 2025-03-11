import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component'; 

@Component({
  selector: 'app-edit-task',
  standalone: true, 
  templateUrl: './edit-task.component.html',
  styleUrls: ['./edit-task.component.css'],
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent], 
})
export class EditTaskComponent {
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

  editTask() {
    console.log('Tarefa salva:', this.taskForm.value);
  }
}
