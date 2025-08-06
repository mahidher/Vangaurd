import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserTransactionData } from '../models/user-transactions.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TransactionAnalyticsService {
  constructor(private http: HttpClient) {}

  getUserTransactions(): Observable<UserTransactionData[]> {
    return this.http.get<UserTransactionData[]>('assets/user-transactions.json');
  }
}

