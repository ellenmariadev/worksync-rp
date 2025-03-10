import { Component, inject } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-tasks',
  imports: [NavbarComponent, RouterModule],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.css'
})
export class TasksComponent {
  private router = inject(Router);

  goToCreateTask(): void {
    this.router.navigate(['/create-task']);
  }

}
