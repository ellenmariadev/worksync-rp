import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent {
  private authService = inject(AuthService);
  private notificationService = inject(NotificationService);
  notifications: any[] = [];
  hasUnreadNotifications = false;
  username: string = '';
  userRole: string | null = null;

  dropdownOpen = false;
  dropdownOpenNotification = false;

  constructor(private router: Router) {}

  ngOnInit() {
    const user = this.authService.getUser();
    if (user && user.sub) {
      this.username = user.sub;
      this.userRole = user.role;
    }

    this.notificationService.getAllNotifications().subscribe({
      next: (notification) => {
        console.log('Notification:', notification);
        this.notifications = notification;
        this.hasUnreadNotifications = this.notifications.some(notification => !notification.read);
      },
      error: (err) => {
        console.error('Erro ao visualizar notificações:', err);
        alert('Erro ao visualizar notificações.');
      },
    });
  }

  formatDateTime(dateInput: string | Date): string {
    const date = typeof dateInput === 'string' ? new Date(dateInput) : dateInput;

    if (isNaN(date.getTime())) {
      return 'Data inválida';
    }

    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    const hour = date.getHours().toString().padStart(2, '0');
    const minute = date.getMinutes().toString().padStart(2, '0');

    return `${day}/${month}/${year} às ${hour}:${minute}`;
  }

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  toggleNotifications() {
    this.dropdownOpen = false;
    this.dropdownOpenNotification = !this.dropdownOpenNotification;
  }

  markAsReadNotification(notificationId: string) {
    this.notificationService.markAsRead(notificationId).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(notification =>
          notification.id !== notificationId
        );

        this.hasUnreadNotifications = this.notifications.some(notification => !notification.read);

        if (this.notifications.length === 0) {
          this.dropdownOpenNotification = false;
        }
      },
      error: (err) => {
        console.error('Erro ao marcar notificação como lida:', err);
        alert('Erro ao marcar notificação como lida.');
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }


}
