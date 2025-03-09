import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { AuthGuard } from './services/auth/auth.guard';
import { HomeComponent } from './pages/home/home.component';
import { RegisterComponent } from './pages/register/register.component';
import { ProjectsComponent } from './pages/projects/projects.component';
import { TasksComponent } from './pages/tasks/tasks.component';
import { CreateTaskComponent } from './pages/create-task/create-task.component';
import { ViewTaskComponent } from './pages/tasks/view-task/view-task.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    title: 'Login - Worksync',
  },
  { path: 'projects', component: ProjectsComponent, canActivate: [AuthGuard], title: 'Projetos - Worksync' },
  { path: 'tasks', component: TasksComponent, canActivate: [AuthGuard], title: 'Tarefas - Worksync',
    children: [
      {
        path: ':taskId',
        component: ViewTaskComponent,
      },
    ],
   },
  { path: 'create-task', component: CreateTaskComponent, canActivate: [AuthGuard], title: 'Criar Tarefa - Worksync' },
  { path: 'register', component: RegisterComponent, title: 'Cadastrar - Worksync' },
  // { path: '', component: HomeComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/login' },
];
