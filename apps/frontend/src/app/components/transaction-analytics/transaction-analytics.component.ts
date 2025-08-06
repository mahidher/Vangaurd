import { Component, Inject, OnInit } from '@angular/core';
import { TransactionAnalyticsService } from '../../services/transaction-analytics.service';
import { UserTransactionData, Transaction } from '../../models/user-transactions.model';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-transaction-analytics',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatDialogModule
  ],
  styleUrls: ['./transaction-analytics.component.scss'],
  templateUrl: './transaction-analytics.component.html'
})
export class TransactionAnalyticsComponent implements OnInit {
  users: UserTransactionData[] = [];
  redFlags: { [username: string]: boolean } = {};

  constructor(
    private service: TransactionAnalyticsService,
    @Inject(MAT_DIALOG_DATA) public data: { username: string }
  ) {}

  ngOnInit(): void {
    this.service.getUserTransactions().subscribe((data) => {
      if (this.data?.username) {
        this.users = data.filter(u => u.username === this.data.username);
      } else {
        this.users = data;
      }
    });

    this.service.getUserTransactions().subscribe(analyticsUsers => {
      const usernames = analyticsUsers.map(u => u.username);
      this.users = this.users.filter(u => usernames.includes(u.username));
    });
  }

  runAnalytics(user: UserTransactionData): void {
    this.redFlags[user.username] = this.hasRedFlag(user.transaction_history);
  }

  private hasRedFlag(transactions: Transaction[]): boolean {
    const grouped = {
      credit: [] as number[],
      debit: [] as number[]
    };

    transactions.forEach(tx => {
      if (tx.amount >= 10000) {
        const time = new Date(tx.timestamp).getTime();
        grouped[tx.transaction_type].push(time);
      }
    });

    for (const type of ['credit', 'debit']) {
      const times = grouped[type as 'credit' | 'debit'].sort();
      for (let i = 0; i < times.length - 1; i++) {
        if (Math.abs(times[i + 1] - times[i]) <= 60 * 1000) {
          return true;
        }
      }
    }

    return false;
  }
}
