import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root',
})
export class NotificationService {
    private apiUrl = `${environment.apiUrl}/notifications`;

    constructor(private http: HttpClient) { }

    private getHeaders(): HttpHeaders {
        return new HttpHeaders().set(
            'Authorization',
            `Bearer ${localStorage.getItem('token')}`
        );
    }

    getAllNotifications(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}`, {
            headers: this.getHeaders(),
        });
    }

    markAsRead(notificationId: string): Observable<any> {
        return this.http.post<any>(
            `${this.apiUrl}/${notificationId}/read`,
            {},
            { headers: this.getHeaders() }
        );
    }

    markAllAsRead(): Observable<any> {
        return this.http.post<any>(
            `${this.apiUrl}/read-all`,
            {},
            { headers: this.getHeaders() }
        );
    }
}
