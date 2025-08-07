import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminUser } from '../models/admin-user.model';

@Injectable({ providedIn: 'root' })
export class AdminUserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(this.apiUrl);
  }

  createUser(user: Partial<AdminUser>): Observable<AdminUser> {
    return this.http.post<AdminUser>(this.apiUrl, user);
  }

  updateUser(userName: string, user: Partial<AdminUser>): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${this.apiUrl}/${userName}`, user);
  }

  deleteUser(userName: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${userName}`, { responseType: 'text' });
  }

  runAnalytics(): Observable<any> {
    return this.http.get('http://localhost:8080/api/analyse', { responseType: 'text' });
  }
}