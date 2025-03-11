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
  filteredTasks: any[] = [];
  searchQuery: string = '';
  startDate: string = '';
  endDate: string = '';
  private router = inject(Router);

  constructor(private taskService: TaskService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasksByProject(1).subscribe({
      next: (data) => {
        console.log('Tarefas carregadas:', data);
        this.tasks = data;
        this.applyFilters();
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao buscar tarefas', err),
    });
  }

  onSearchChange(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value.toLowerCase();
    this.applyFilters();
  }

  onStartDateChange(event: Event): void {
    this.startDate = (event.target as HTMLInputElement).value;
    this.applyFilters();
  }

  onEndDateChange(event: Event): void {
    this.endDate = (event.target as HTMLInputElement).value;
    this.applyFilters();
  }

  applyFilters(): void {
    this.filteredTasks = this.tasks.filter(task => {
      const titleMatch = task.title.toLowerCase().includes(this.searchQuery);
      const startDateMatch = this.startDate ? new Date(task.startDate) >= new Date(this.startDate) : true;
      const endDateMatch = this.endDate ? new Date(task.startDate) <= new Date(this.endDate) : true;
      return titleMatch && startDateMatch && endDateMatch;
    });
  }

  deleteTask(id: number): void {
    if (confirm('Tem certeza que deseja excluir esta tarefa?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.tasks = this.tasks.filter((task) => task.id !== id);
          this.applyFilters();
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

  goToEditTask(taskId: number): void {
    this.router.navigate([`/tasks/edit/${taskId}`]);
  }

  goToViewTask(taskId: number): void {
    this.router.navigate([`/tasks/${taskId}`]);
  }
}

