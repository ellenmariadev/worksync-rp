import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { ProjectsService } from '../../services/project.service';
import { ProjectDTO } from '../../services/types/project';
import { Router } from '@angular/router';  // Importar para navegação

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css'],
})
export class ProjectsComponent implements OnInit {
  projects: ProjectDTO[] = [];  // Lista de projetos

  constructor(
    private projectsService: ProjectsService,
    private router: Router  // Injetar o serviço Router para navegação
  ) {}

  ngOnInit(): void {
    this.loadProjects();  // Carregar projetos ao iniciar o componente
  }

  // Método para carregar projetos (opcionalmente com filtro por título)
  loadProjects(title: string = ''): void {
    this.projectsService.getAllProjects(title).subscribe(
      (data) => {
        this.projects = data;  // Atualiza a lista de projetos com os dados retornados
      },
      (error) => {
        console.error('Erro ao carregar projetos', error);  // Loga erro no console
      }
    );
  }

  // Método para editar o projeto
  editProject(id: number): void {
    // Redireciona para uma página de edição de projeto
    this.router.navigate([`/projects/edit/${id}`]);
  }

  // Método para excluir o projeto
  deleteProject(id: number): void {
    if (confirm('Tem certeza que deseja excluir este projeto?')) {
      this.projectsService.deleteProject(id).subscribe(
        () => {
          // Remove o projeto excluído da lista localmente
          this.projects = this.projects.filter(projeto => projeto.id !== id);
        },
        (error) => {
          console.error('Erro ao excluir projeto', error);
        }
      );
    }
  }



  // Método que vai ser chamado no evento input
  onSearchChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.loadProjects(inputElement.value);  // Chama o método de carregar projetos com o valor do input
  }
}
