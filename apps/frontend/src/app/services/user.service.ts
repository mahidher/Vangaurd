import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, catchError, delay, map, throwError } from 'rxjs';
import { User } from '../models';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  loggedInUser$ = new BehaviorSubject<User | null>(null);
  constructor(private http: HttpClient, private router: Router) { }

  login(userName: string): Observable<User> {
    return this.http.get<User>(`http://localhost:8080/api/users/${userName}`).pipe(
      map((user: User) => {
        if (!user) {
          throw new Error('User not found');
        }
        this.loggedInUser$.next(user);
        return user;
      }),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    );
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>('http://localhost:8080/api/users').pipe(
      map((users: User[]) => {
        return users;
      }),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    )

    /* return this.http.get<User[]>(`http://localhost:8080/api/users`).pipe(
      map((response: User[]) => {
        if (!response) {
          throw new Error('Users list error');
        }
        return response;
      }),
      catchError((error) => {
        console.error('Users list error:', error);
        return throwError(() => error);
      })
    ); */
  }

  getLoggedInUserValue(): User | null {
    return this.loggedInUser$.value;
  }

  logout(): void {
    this.loggedInUser$.next(null);
    this.router.navigate(['/login']);
  }
}
