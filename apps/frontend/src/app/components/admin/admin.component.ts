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

interface User {
  id: number;
  name: string;
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

  users: User[] = [
    { id: 1, name: 'Karthik J', email: 'karthik@example.com' },
    { id: 2, name: 'Arjun R', email: 'arjun@example.com' },
    { id: 3, name: 'Sneha V', email: 'sneha@example.com' }
  ];

  constructor(private dialog: MatDialog) {}

  toggleSelectAll(checked: boolean): void {
    this.users.forEach(user => user.selected = checked);
  }

  deleteUser(user: User): void {
    this.users = this.users.filter(u => u.id !== user.id);
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
        // Assign a new array to trigger change detection
        this.users = [...this.users, { id, ...result }];
      }
    });
  }

  editUser(user: User): void {
    const dialogRef = this.dialog.open<UserDialogComponent, UserDialogData, any>(UserDialogComponent, {
      data: {
        user: { ...user },
        isEdit: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        Object.assign(user, result);
      }
    });
  }

  deleteSelected(): void {
    this.users = this.users.filter(u => !u.selected);
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
        Object.assign(user, result);
      }
    }
  }

  hasSelectedUsers(): boolean {
    return Array.isArray(this.users) && this.users.some(u => u.selected);
  }
}
