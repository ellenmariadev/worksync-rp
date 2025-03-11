import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> d82e49520d45db3045232361e96e5c049a1bb9a7
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { ProjectsService } from '../../../services/project.service';
import { UserService } from '../../../services/user.service';
import { FormsModule } from '@angular/forms';
import { ProjectDTO } from '../../../services/types/project';
<<<<<<< HEAD
=======
import { NavbarComponent } from '../../../components/navbar/navbar.component';
>>>>>>> 730ae57638f89dc82c564d99ab6b4aa2aae3aad2
=======
>>>>>>> d82e49520d45db3045232361e96e5c049a1bb9a7

@Component({
  selector: 'app-view-project',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, ReactiveFormsModule, FormsModule],
  templateUrl: './view-project.component.html',
  styleUrl: './view-project.component.css',
  imports: [NavbarComponent],
})

export class ViewProjectComponent implements OnInit {
  projectId!: string;
  project: ProjectDTO | null = null;
  participantNames: { [key: number]: string } = {}; // Mapeia ID -> Nome

  constructor(
    private router: ActivatedRoute,
    private projectsService: ProjectsService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.projectId = this.router.snapshot.paramMap.get('projectId')!;
    this.getProject();
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
}
