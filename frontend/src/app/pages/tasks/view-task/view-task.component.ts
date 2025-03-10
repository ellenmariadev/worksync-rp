import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../../components/navbar/navbar.component';

@Component({
  selector: 'app-view-task',
  imports: [NavbarComponent],
  templateUrl: './view-task.component.html',
  styleUrl: './view-task.component.css'
})

export class ViewTaskComponent implements OnInit {
  taskId!: string;

  constructor(public router: ActivatedRoute) {}
  ngOnInit(): void {
    this.taskId = this.router.snapshot.paramMap.get('taskId')!;
  }
}
