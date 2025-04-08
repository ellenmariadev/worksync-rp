import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { ProjectsService } from '../../services/project.service';
import { ProjectDTO } from '../../services/types/project';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterModule],
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {
  projects: ProjectDTO[] = [];
  userRole: string | null = null; // ⬅️ VARIÁVEL PRA ROLE

  constructor(
    private projectsService: ProjectsService,
    private router: Router,
    private authService: AuthService // ⬅️ INJETA O SERVIÇO
  ) {}

  ngOnInit(): void {
    this.loadProjects();

    const user = this.authService.getUser(); // ⬅️ PEGA O USUÁRIO
    this.userRole = user?.role || null;
    console.log('Role do usuário:', this.userRole);
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
    this.router.navigate([`/edit-project/${id}`]);
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

  onSearchChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.loadProjects(inputElement.value);
  }
}
