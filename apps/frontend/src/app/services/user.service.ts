import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, catchError, map, throwError } from 'rxjs';
import { User } from '../models';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  loggedInUser$ = new BehaviorSubject<User | null>(null);
  constructor(private http: HttpClient) { }

  login(userName: string) : Observable<User> {
    return this.http.get<User[]>('/assets/data/users.json').pipe(
      map((users:User[]) =>  {
        const user = users?.find(u => u.userName === userName);
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
    )
    // -- Uncomment the following lines if you have a backend API to handle login -- //
    // return this.http.post<User>('/api/login', { userName }).pipe(
    //   map((user: User) => {
    //     if (!user) {
    //       throw new Error('User not found');
    //     }
    //     this.loggedInUser$.next(user);
    //     return user;
    //   }),
    //   catchError((error) => {
    //     console.error('Login error:', error);
    //     return throwError(() => error);
    //   })
    // );
  }
}
