import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Transaction } from '../models';

@Injectable({
  providedIn: 'root'
})
export class TransactionsService {

  constructor(private http: HttpClient) { }

  transactionHistory(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>('http://localhost:8080/api/transfers').pipe(
      map((response: Transaction[]) => {
        return response;
      }),
      catchError((error) => {
        console.error('Transaction History error:', error);
        return throwError(() => error);
      })
    );
  }

  transferFunds(transactionObj: Transaction): Observable<Transaction> {
    return this.http.post<Transaction>('http://localhost:8080/api/transfers', transactionObj).pipe(
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
