import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../services/auth/tasks/tasks.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-tasks',
  standalone: true,
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.css'], // Corrigido para styleUrls (em vez de styleUrl)
  imports: [CommonModule, RouterModule, NavbarComponent], // Importando NavbarComponent
})
export class TasksComponent implements OnInit {
  tasks: any[] = []; // Usando `any[]` para as tarefas

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  // Carregar as tarefas do projeto (com ID 1 neste exemplo)
  loadTasks(): void {
    this.taskService.getTasksByProject(1).subscribe({
      next: (data) => (this.tasks = data),
      error: (err) => console.error('Erro ao buscar tarefas', err),
    });
  }

  deleteTask(id: number): void {
    if (confirm('Tem certeza que deseja excluir esta tarefa?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.tasks = this.tasks.filter((task) => task.id !== id);
        },
        error: (err) => {
          console.error('Erro ao excluir tarefa', err);
        },
      });
    } else {
      console.log('Exclusão de tarefa cancelada');
    }
  }
}
