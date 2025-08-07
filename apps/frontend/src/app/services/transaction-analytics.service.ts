import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserTransactionData } from '../models/user-transactions.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TransactionAnalyticsService {
  constructor(private http: HttpClient) {}

  // Fetch user (with transactions) from the backend
  getUser(username: string): Observable<UserTransactionData> {
    return this.http.get<UserTransactionData>(`http://localhost:8080/api/users/${username}`);
  }
}

