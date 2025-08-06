import { Component } from '@angular/core';
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
    MatDialogModule
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent {
  displayedColumns: string[] = ['select', 'name', 'email', 'actions'];
  users: AdminUser[] = [];

  constructor(
    private dialog: MatDialog,
    private adminUserService: AdminUserService
  ) {
    this.adminUserService.getUsers().subscribe(users => {
      this.users = users;
    });
  }

  private updateUsers(users: AdminUser[]) {
    this.adminUserService.setUsers(users);
  }

  toggleSelectAll(checked: boolean): void {
    this.updateUsers(this.users.map(user => ({ ...user, selected: checked })));
  }

  deleteUser(user: AdminUser): void {
    this.updateUsers(this.users.filter(u => u.id !== user.id));
  }

  createUser(): void {
    const dialogRef = this.dialog.open<UserDialogComponent, UserDialogData, any>(UserDialogComponent, {
      data: {
        user: { name: '', email: '' },
        isEdit: false
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const id = this.users.length ? Math.max(...this.users.map(u => u.id)) + 1 : 1;
        this.updateUsers([...this.users, { id, ...result }]);
      }
    });
  }

  editUser(user: AdminUser): void {
    const dialogRef = this.dialog.open<UserDialogComponent, UserDialogData, any>(UserDialogComponent, {
      data: {
        user: { ...user },
        isEdit: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.updateUsers(this.users.map(u => u.id === user.id ? { ...u, ...result } : u));
      }
    });
  }

  deleteSelected(): void {
    this.updateUsers(this.users.filter(u => !u.selected));
  }

  async editSelected(): Promise<void> {
    const selected = this.users.filter(u => u.selected);
    for (const user of selected) {
      // eslint-disable-next-line no-await-in-loop
      const result = await this.dialog.open<UserDialogComponent, UserDialogData, any>(UserDialogComponent, {
        data: {
          user: { ...user },
          isEdit: true
        }
      }).afterClosed().toPromise();

      if (result) {
        this.updateUsers(this.users.map(u => u.id === user.id ? { ...u, ...result } : u));
      }
    }
  }

  hasSelectedUsers(): boolean {
    return Array.isArray(this.users) && this.users.some(u => u.selected);
  }

  viewAnalytics(user: AdminUser): void {
    this.dialog.open(TransactionAnalyticsComponent, {
      width: '600px',
      maxWidth: '95vw',
      data: { username: user.username }
    });
  }
}
