import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { TaskService } from '../../services/tasks.service';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-tasks',
  standalone: true,
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.css'],
  imports: [CommonModule, RouterModule, NavbarComponent],
})
export class TasksComponent implements OnInit {
  tasks: any[] = [];
  private router = inject(Router);
  constructor(private taskService: TaskService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasksByProject(1).subscribe({
      next: (data) => {
        this.tasks = data;
        this.cdr.detectChanges(); // Força a atualização do Angular
      },
      error: (err) => console.error('Erro ao buscar tarefas', err),
    });
  }

  deleteTask(id: number): void {
    if (confirm('Tem certeza que deseja excluir esta tarefa?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.tasks = this.tasks.filter((task) => task.id !== id);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Erro ao excluir tarefa', err);
        },
      });
    }
  }

  goToCreateTask(): void {
    this.router.navigate(['/create-task']);
  }
}
