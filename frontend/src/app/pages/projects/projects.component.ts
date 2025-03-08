import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-projects',
  imports: [NavbarComponent],
  templateUrl: './projects.component.html',
  styleUrl: './projects.component.css'
})
export class ProjectsComponent { }
