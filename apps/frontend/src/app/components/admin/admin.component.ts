import { Component, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { UserDialogComponent, UserDialogData } from '../user-dialog/user-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { TransactionAnalyticsComponent } from '../transaction-analytics/transaction-analytics.component';
import { AdminUser } from '../../models/admin-user.model';
import { AdminUserService } from '../../services/admin-user.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatSort } from '@angular/material/sort';
import { MatProgressBarModule } from '@angular/material/progress-bar';

interface User {
  id: number;
  name: string;
  username: string; // <-- Add this line
  email: string;
  selected?: boolean;
}

@Component({
  standalone: true,
  selector: 'app-admin',
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatToolbarModule,
    MatDialogModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressBarModule
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements AfterViewInit {
  displayedColumns: string[] = ['userName', 'balance', 'createdAt', 'actions'];
  dataSource = new MatTableDataSource<AdminUser>([]);
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading = false;

  constructor(
    private dialog: MatDialog,
    private apiUserService: AdminUserService
  ) {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.apiUserService.getUsers().subscribe(users => {
      this.dataSource.data = users;
      setTimeout(() => {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        if (this.paginator) {
          this.paginator.firstPage();
        }
        this.loading = false;
      });
    }, () => {
      this.loading = false;
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  createUser(): void {
    const dialogRef = this.dialog.open<UserDialogComponent, UserDialogData, AdminUser>(UserDialogComponent, {
      data: {
        user: { userName: '', balance: 0, createdAt: null },
        isEdit: false
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.apiUserService.createUser(result).subscribe(() => this.loadUsers());
      }
    });
  }

  editUser(user: AdminUser): void {
    const dialogRef = this.dialog.open<UserDialogComponent, UserDialogData, AdminUser>(UserDialogComponent, {
      data: {
        user: { ...user },
        isEdit: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.apiUserService.updateUser(user.userName, result).subscribe(() => this.loadUsers());
      }
    });
  }

  deleteUser(user: AdminUser): void {
    this.apiUserService.deleteUser(user.userName).subscribe(() => {
      this.loadUsers();
    });
  }

  viewAnalytics(user: AdminUser): void {
    this.dialog.open(TransactionAnalyticsComponent, {
      width: '600px',
      maxWidth: '95vw',
      data: { username: user.userName }
    });
  }

  runAnalytics(): void {
    this.apiUserService.runAnalytics().subscribe();
  }
}

