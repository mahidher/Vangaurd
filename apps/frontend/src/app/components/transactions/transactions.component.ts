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
import { Transaction, User, UserTransactionSummary } from 'src/app/models';
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
  currentBalance: number | undefined;
  displayedColumns: string[] = ['transactionId', 'timestamp', 'description', 'amount'];
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
    this.currentBalance = this.userService.getLoggedInUserValue()?.balance;
    this.getTransactionHistory();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  getTransactionHistory() {
    this.userService.userTransactions()
      .subscribe({
        next: (response: UserTransactionSummary) => {
          this.dataSource.data = response.transactions;
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
      'fromUserName': [this.userService.getLoggedInUserValue()?.userName],
      'toUserName': ['', [Validators.required]],
      'amount': [null, [Validators.required, Validators.pattern(/^\d+$/), Validators.min(1)]],
      'description': ['']
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
          //To remove current user from user list
          this.userList = response.filter((user: User) => user.userName != this.userService.getLoggedInUserValue()?.userName);
        },
        error: (err) => {
          this.showError(err);
        }
      });
  }

  transferFunds() {
    this.transferFundsForm.markAllAsTouched();
    if (this.transferFundsForm.valid) {
      this.loading = true;

      this.transactionService.transferFunds(this.transferFundsForm.value)
        .subscribe({
          next: (response: Transaction) => {
            response.type = 'SENT';
            this.dataSource.data = [response, ...this.dataSource.data];
            this.loading = false;
            this.transferFundDialogRef.close();

            if (this.currentBalance)
              this.currentBalance = this.currentBalance - this.transferFundsForm.value.amount;
          },
          error: (err) => {
            this.showError(err.message);
            this.loading = false;
          }
        });
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
