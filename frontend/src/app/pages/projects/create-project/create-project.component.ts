import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { ProjectsService } from '../../../services/project.service';
import { UserService } from '../../../services/user.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create-project',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, ReactiveFormsModule, FormsModule],
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css'],
})
export class CreateProjectComponent implements OnInit {
  projectForm: FormGroup;
  projectId: number | null = null;
  participants: any[] = [];
  users: any[] = [];
  selectedUserId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private projectService: ProjectsService,
    private userService: UserService,
    private router: Router
  ) {
    this.projectForm = this.fb.group({
      title: [''],
      description: [''],
    });
  }

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        console.log('Usuários recebidos:', this.users);
      },
      error: (err) => console.error('Erro ao buscar usuários:', err),
    });
  }

  createProject() {
    if (this.projectForm.invalid) {
      console.warn('Preencha todos os campos corretamente.');
      return;
    }

    const { title, description } = this.projectForm.value;
    this.projectService.createProject(title, description).subscribe({
      next: (project) => {
        this.projectId = project.id;
        console.log('Projeto criado:', project);

        // Atualiza os participantes do projeto
        this.participants = this.users.filter(user => project.participantIds.includes(user.id));
      },
      error: (err) => console.error('Erro ao criar projeto:', err),
    });
  }

  addParticipant() {
    if (this.selectedUserId === null || !this.projectId) {
      alert('Erro: Nenhum usuário selecionado!');
      return;
    }

    // Verifica se o usuário já está na lista de participantes
    const alreadyAdded = this.participants.some(user => user.id === this.selectedUserId);
    
    if (alreadyAdded) {
      alert('Erro: Este participante já foi adicionado!');
      return;
    }

    this.projectService.addParticipant(this.projectId, this.selectedUserId).subscribe({
      next: (updatedProject) => {
        console.log('Participante adicionado:', updatedProject);

        // Atualiza a lista de participantes após adição
        this.participants = this.users.filter(user => updatedProject.participantIds.includes(user.id));
      },
      error: (err) => {
        console.error('Erro ao adicionar participante:', err);
        alert('O participante já foi adicionado.');
      },
    });
  }

  finalizeProject() {
    if (!this.projectId) {
      alert('Erro: O projeto ainda não foi criado.');
      return;
    }

    alert('Projeto criado com sucesso!');
    this.router.navigate(['/projects']); // Redireciona para a listagem de projetos
  }
}
