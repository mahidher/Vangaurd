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
  user?: UserTransactionData;
  redFlag: boolean = false;
  analyticsRun: boolean = false; // <-- Add this

  constructor(
    private service: TransactionAnalyticsService,
    @Inject(MAT_DIALOG_DATA) public data: { username: string }
  ) {}

  ngOnInit(): void {
    if (this.data?.username) {
      this.service.getUser(this.data.username).subscribe(userData => {
        this.user = userData;
      });
    }
  }

  runAnalytics(): void {
    if (this.user) {
      const transactions = this.user.transaction_history || [];
      this.redFlag = this.hasRedFlag(transactions);
      this.analyticsRun = true;
      console.log('Analytics run:', { redFlag: this.redFlag, transactions });
    }
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
