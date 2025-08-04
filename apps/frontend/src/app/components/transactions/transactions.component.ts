import { CommonModule } from '@angular/common';
import { Component, TemplateRef, ViewChild } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MatDialogModule,
} from '@angular/material/dialog';
import {
  MatTableModule,
  MatTableDataSource,
} from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Transaction, TransactionListResponse, User } from 'src/app/models';
import { TransactionsService } from 'src/app/services/transactions.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-transactions',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.scss'
})
export class TransactionsComponent {
  loading: boolean = false;
  currentBalance: string | number = '';
  displayedColumns: string[] = ['transaction_id', 'timestamp', 'username', 'transaction_type', 'amount'];
  dataSource = new MatTableDataSource<Transaction>([]);

  userList: User[] = [];
  transferFundsForm!: FormGroup;
  transferFundDialogRef!: MatDialogRef<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild('dialogTemplate') dialogTemplate!: TemplateRef<any>;

  constructor(
    private dialog: MatDialog,
    private fb: FormBuilder,
    private userService: UserService,
    private transactionService: TransactionsService,
    private _snackBar: MatSnackBar
  ) { }

  ngOnInit() {
    this.getTransactionHistory();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  getTransactionHistory() {
    this.transactionService.transactionHistory(this.userService.getLoggedInUserValue()?.userId)
      .subscribe({
        next: (response: TransactionListResponse) => {
          const transactionListResponse = response;
          this.currentBalance = response.current_balance;
          this.dataSource.data = transactionListResponse.transaction_history;
          this.loading = false;
        },
        error: (err) => {
          this.showError(err);
          this.loading = false;
        }
      });
  }

  openDialog() {
    this.transferFundsForm = this.fb.group({
      'fromUsername': [this.userService.getLoggedInUserValue()?.userName],
      'toUsername': ['', [Validators.required]],
      'amount': [null, [Validators.required, Validators.pattern(/^\d+$/)]]
    });

    this.getUserList();

    this.transferFundDialogRef = this.dialog.open(this.dialogTemplate, {
      width: '600px',
    });
  }

  getUserList() {
    this.userService.getUsers()
      .subscribe({
        next: (response: User[]) => {
          this.userList = response;
        },
        error: (err) => {
          this.showError(err);
        }
      });
  }

  transferFunds() {
    this.transferFundsForm.markAllAsTouched();
    if (this.transferFundsForm.valid) {
      if (+this.currentBalance < +this.transferFundsForm.value.amount) {
        this.showError("Insufficient Funds");
        return;
      }

      this.loading = true;
      let newData: Transaction = {
        ...this.transferFundsForm.value,
        transaction_id: 'T5913500045',
        transaction_type: 'debit',
        username: this.transferFundsForm.value.toUsername,
        timestamp: new Date()
      };
      this.dataSource.data = [newData, ...this.dataSource.data];
      this.currentBalance = +this.currentBalance - +newData.amount;
      this.loading = false;
      this.transferFundDialogRef.close();

      /* this.transactionService.transferFunds(this.transferFundsForm.value)
        .subscribe({
          next: (response: Transaction) => {
            this.dataSource.data = [...this.dataSource.data, response];
            this.loading = false;
            this.transferFundDialogRef.close();
          },
          error: (err) => {
            this.showError(err);
            this.loading = false;
          }
        }); */
    }
  }

  showError(message: string) {
    this._snackBar.open(message, 'Close', {
      duration: 4000,              // auto close after 4 seconds
      panelClass: ['error-snackbar'],
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
  }
}
