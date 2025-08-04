import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { inject } from '@angular/core';

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
    MatToolbarModule
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent {
  router = inject(Router);

  goHome() {
    this.router.navigate(['/']);
  }
  displayedColumns: string[] = ['select', 'name', 'email', 'actions'];

  users: User[] = [
    { id: 1, name: 'Karthik J', email: 'karthik@example.com' },
    { id: 2, name: 'Arjun R', email: 'arjun@example.com' },
    { id: 3, name: 'Sneha V', email: 'sneha@example.com' }
  ];

  toggleSelectAll(checked: boolean): void {
    this.users.forEach(user => user.selected = checked);
  }

  deleteUser(user: User): void {
    this.users = this.users.filter(u => u.id !== user.id);
  }

  editUser(user: User): void {
    alert(`Editing user: ${user.name}`);
  }

  deleteSelected(): void {
    this.users = this.users.filter(u => !u.selected);
  }

  editSelected(): void {
    const selected = this.users.filter(u => u.selected);
    alert(`Editing users: ${selected.map(u => u.name).join(', ')}`);
  }

  createUser(): void {
    const id = this.users.length + 1;
    this.users.push({
      id,
      name: `New User ${id}`,
      email: `newuser${id}@example.com`
    });
  }

  hasSelectedUsers(): boolean {
    return Array.isArray(this.users) && this.users.some(u => u.selected);
  }
}
