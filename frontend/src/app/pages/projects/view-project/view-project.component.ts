import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { ProjectsService } from '../../../services/project.service';
import { UserService } from '../../../services/user.service';
import { TaskService } from '../../../services/tasks.service';
import { FormsModule } from '@angular/forms';
import { ProjectDTO } from '../../../services/types/project';

@Component({
  selector: 'app-view-project',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, ReactiveFormsModule, FormsModule],
  templateUrl: './view-project.component.html',
  styleUrl: './view-project.component.css',
})

export class ViewProjectComponent implements OnInit {
  projectId!: string;
  project: ProjectDTO | null = null;
  tasks: any[] = [];
  participantNames: { [key: number]: string } = {}; // Mapeia ID -> Nome
  taskResponsibleNames: { [key: number]: string } = {}; // Mapeia ID -> Nome do responsável

  constructor(
    private router: ActivatedRoute,
    private projectsService: ProjectsService,
    private userService: UserService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.projectId = this.router.snapshot.paramMap.get('projectId')!;
    this.getProject();
    this.getTasks();
  }

  getProject(): void {
    this.projectsService.getProjectById(Number(this.projectId)).subscribe(
      (project) => {
        this.project = project;
        if (project.participantIds) {
          project.participantIds.forEach((id) => {
            this.userService.getUserById(id).subscribe(
              (user) => {
                this.participantNames[id] = user.name;
              },
              (error) => {
                console.error(`Erro ao buscar usuário ${id}:`, error);
              }
            );
          });
        }
      },
      (error) => {
        console.error('Erro ao carregar o projeto:', error);
      }
    );
  }

  getTasks(): void {
    this.taskService.getTasksByProject(Number(this.projectId)).subscribe(
      (tasks) => {
        this.tasks = tasks;
        tasks.forEach((task) => {
          this.userService.getUserById(task.responsibleId).subscribe(
            (user) => {
              this.taskResponsibleNames[task.responsibleId] = user.name;
            },
            (error) => {
              console.error(`Erro ao buscar responsável ${task.responsibleId}:`, error);
            }
          );
        });
      },
      (error) => {
        console.error('Erro ao carregar tarefas:', error);
      }
    );
  }
}
