import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectsService } from '../../../services/project.service';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-project',
  standalone: true,
  imports: [CommonModule, NavbarComponent, ReactiveFormsModule, FormsModule, RouterModule],
  templateUrl: './edit-project.component.html',
  styleUrls: ['./edit-project.component.css']
})
export class EditProjectComponent implements OnInit {
  projectForm: FormGroup;
  projectId: number | null = null;
  participants: any[] = [];
  users: any[] = [];
  selectedUserId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private projectService: ProjectsService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.projectForm = this.fb.group({
      title: ['', [Validators.required]], 
      description: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.projectId = +params['id'];
      if (this.projectId) {
        this.loadProjectData(this.projectId);
      }
    });

    this.projectService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (err) => console.error('Erro ao buscar usuários:', err),
    });
  }

  loadProjectData(projectId: number) {
    this.projectService.getProjectById(projectId).subscribe({
      next: (project) => {
        this.projectForm.patchValue({
          title: project.title,
          description: project.description,
        });

        this.participants = this.users.filter(user => project.participantIds?.includes(user.id));
      },
      error: (err) => console.error('Erro ao carregar dados do projeto:', err),
    });
  }

  updateProject() {
    if (this.projectForm.invalid) {
      console.warn('Preencha todos os campos corretamente.');
      return;
    }

    const { title, description } = this.projectForm.value;
    this.projectService.updateProject(this.projectId!, title, description).subscribe({
      next: (updatedProject) => {
        console.log('Projeto atualizado:', updatedProject);
        this.router.navigate(['/projects']);
      },
      error: (err) => console.error('Erro ao atualizar projeto:', err),
    });
  }

  addParticipant() {
    if (this.selectedUserId === null || !this.projectId) {
      alert('Erro: Nenhum usuário selecionado!');
      return;
    }

    if (this.participants.some(user => user.id === this.selectedUserId)) {
      alert('Este participante já foi adicionado!');
      return;
    }

    this.projectService.addParticipant(this.projectId!, this.selectedUserId).subscribe({
      next: (updatedProject) => {
        console.log('Participante adicionado:', updatedProject);
        this.participants = this.users.filter(user => updatedProject.participantIds.includes(user.id));
      },
      error: (err) => {
        console.error('Erro ao adicionar participante:', err);
        alert('Erro ao adicionar participante. Verifique se ele já foi adicionado.');
      },
    });
  }

  salvarProject() {
    if (this.projectForm.valid && this.projectId !== null) {
      const { title, description } = this.projectForm.value; 
      this.projectService.updateProject(this.projectId, title, description).subscribe({
        next: () => {
          alert('Projeto atualizado com sucesso!');
          this.router.navigate(['/projects']);
        },
        error: (err) => alert('Erro ao salvar o projeto: ' + err.message),
      });
    } else {
      alert('Erro ao cadastrar! Revise todos os campos.');
    }
  }
}
