import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Transaction, TransactionListResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class TransactionsService {

  constructor(private http: HttpClient) { }

  transactionHistory(userId: string | undefined): Observable<TransactionListResponse> {
    return this.http.get<TransactionListResponse>('/assets/data/transactions.json').pipe(
      map((transactions: TransactionListResponse) => {
        return transactions;
      }),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    )


    /* return this.http.get<TransactionListResponse>(`/history/${userId}`).pipe(
      map((response: TransactionListResponse) => {
        if (!response) {
          throw new Error('Transaction History error');
        }
        return response;
      }),
      catchError((error) => {
        console.error('Transaction History error:', error);
        return throwError(() => error);
      })
    ); */
  }

  transferFunds(transactionObj: { fromUsername: string, toUsername: string, amount: string }): Observable<Transaction> {
    return this.http.post<Transaction>('/transfer', transactionObj).pipe(
      map((response: Transaction) => {
        if (!response) {
          throw new Error('Transfer Funds error');
        }
        return response;
      }),
      catchError((error) => {
        console.error('Transfer Funds error:', error);
        return throwError(() => error);
      })
    );
  }
}
