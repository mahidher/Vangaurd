import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AdminUser } from '../models/admin-user.model';

@Injectable({ providedIn: 'root' })
export class AdminUserService {
  private usersSubject = new BehaviorSubject<AdminUser[]>( [
    { id: 1, name: 'Ben Smith', username: 'ben', email: 'ben@example.com' },
    { id: 2, name: 'Alice Johnson', username: 'alice', email: 'alice@example.com' }
    // Add more users only if they exist in user-transactions.json
  ]);

  getUsers(): Observable<AdminUser[]> {
    return this.usersSubject.asObservable();
  }

  setUsers(users: AdminUser[]): void {
    this.usersSubject.next(users);
  }
}