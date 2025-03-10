import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { ProjectsService } from '../../services/project.service';
import { ProjectDTO } from '../../services/types/project';
import { RouterModule, Router } from '@angular/router';  // Importando Router

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterModule],  // Importando RouterModule aqui
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {
  projects: ProjectDTO[] = [];

  constructor(
    private projectsService: ProjectsService,
    private router: Router  // Agora Router é reconhecido
  ) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(title: string = ''): void {
    this.projectsService.getAllProjects(title).subscribe(
      (data) => {
        this.projects = data;
      },
      (error) => {
        console.error('Erro ao carregar projetos', error);
      }
    );
  }

  editProject(id: number): void {
    this.router.navigate([`/projects/edit/${id}`]);  // Usando o Router para navegação
  }

  deleteProject(id: number): void {
    if (confirm('Tem certeza que deseja excluir este projeto?')) {
      this.projectsService.deleteProject(id).subscribe({
        next: () => {
          this.projects = this.projects.filter(projeto => projeto.id !== id);
        },
        error: (error) => {
          console.error('Erro ao excluir projeto', error);
        }
      });
    }
  }

  // Método que vai ser chamado no evento input
  onSearchChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.loadProjects(inputElement.value);
  }
}
