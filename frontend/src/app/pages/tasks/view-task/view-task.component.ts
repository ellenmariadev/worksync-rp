import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { NavbarComponent } from '../../../components/navbar/navbar.component';
import { TaskService } from '../../../services/tasks.service';
import { TaskResponse } from '../../../services/types/tasks';
import { translateStatus } from '../../../utils/translateStatus';
import { UserService } from '../../../services/user.service';
import { formatDate } from '../../../utils/formatDate';
import { CommentResponse } from '../../../services/types/comment';
import { CommentService } from '../../../services/comment.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-view-task',
  imports: [NavbarComponent, CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './view-task.component.html',
  styleUrl: './view-task.component.css',
})
export class ViewTaskComponent implements OnInit {
  taskId!: string;
  task: TaskResponse | null = null;
  responsibleName!: string;
  comments: CommentResponse[] = [];
  username: string = '';
  email!: string;
  userId!: number;
  commentForm: FormGroup;

  private taskService = inject(TaskService);
  private userService = inject(UserService);
  private commentService = inject(CommentService);

  constructor(public router: ActivatedRoute, private fb: FormBuilder) {
    this.commentForm = this.fb.group({
      description: [''],
    });
  }

  ngOnInit(): void {
    this.taskId = this.router.snapshot.paramMap.get('taskId')!;
    const user = localStorage.getItem('user');
    if (user) {
      this.email = JSON.parse(user).sub;
    }

    this.taskService.getTaskById(this.taskId).subscribe({
      next: (task: TaskResponse) => {
        this.task = task;
        this.userService.getUserById(this.task.responsibleId).subscribe({
          next: (user) => {
            this.responsibleName = user.name;
          },
          error: (err) => {
            console.error('Erro ao buscar o responsável:', err);
          },
        });
      },
      error: (err: unknown) => {
        console.error('Erro ao carregar a tarefa:', err);
      },
    });

    this.commentService.getCommentsByTask(this.taskId).subscribe({
      next: (data: CommentResponse[]) => {
        this.comments = data;
        this.comments.forEach((comment) => {
          this.userService.getUserById(comment.userId).subscribe({
            next: (user) => {
              this.username = user.name;
            },
            error: (err) => {
              console.error(`Erro ao buscar o usuário ${comment.userId}:`, err);
            },
          });
        });
      },
      error: (err) => {
        console.error('Erro ao buscar os comentários:', err);
      },
    });

    this.loadComments();
  }

  loadComments(): void {
    this.commentService.getCommentsByTask(this.taskId).subscribe({
      next: (data: CommentResponse[]) => {
        this.comments = data;
        this.comments.forEach((comment) => {
          this.userService.getUserById(comment.userId).subscribe({
            next: (user) => {
              this.username = user.name;
            },
            error: (err) => {
              console.error(`Erro ao buscar o usuário ${comment.userId}:`, err);
            },
          });
        });
      },
      error: (err) => {
        console.error('Erro ao buscar os comentários:', err);
      },
    });


    this.userService.getUserByEmail(this.email).subscribe({
      next: (user) => {
        this.userId = user.id;
      },
      error: (err) => {
        console.error('Erro ao buscar o usuário:', err);
      },
    });

  }

  async onSubmit(): Promise<void> {
    const description = this.commentForm.get('description')?.value;

    if (description && this.userId) {
      try {
        const newComment = await this.commentService.createComment(
          description,
          parseInt(this.taskId),
          this.userId
        );
        this.comments.push(newComment);
        this.commentForm.reset();
        this.loadComments();
      } catch (error) {
        console.error('Erro ao criar comentário:', error);
      }
    }
  }

  deleteComment(id: number): void {
    if (confirm('Tem certeza que deseja excluir este comentário?')) {
      this.commentService.deleteComment(id).subscribe({
        next: () => {
          this.comments = this.comments.filter(comment => comment.id !== id);
        },
        error: (error) => {
          console.error('Erro ao excluir comentário', error);
        }
      });
    }
  }



  translateStatus(status: string): string {
    return translateStatus(status);
  }

  formatDate(date: string): string {
    return formatDate(date);
  }
}
