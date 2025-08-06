import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

export interface UserDialogData {
  user: { id?: number; name: string; email: string };
  isEdit: boolean;
}

@Component({
  standalone: true,
  selector: 'app-user-dialog',
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  template: `<div class="user-dialog">
    <h2 mat-dialog-title>{{ data.isEdit ? 'Edit User' : 'Add User' }}</h2>
    <div mat-dialog-content>
      <form #userForm="ngForm">
        <mat-form-field appearance="fill" class="w-100">
          <mat-label>Name</mat-label>
          <input matInput [(ngModel)]="data.user.name" name="name" required />
        </mat-form-field>
        <mat-form-field appearance="fill" class="w-100">
          <mat-label>Email</mat-label>
          <input matInput [(ngModel)]="data.user.email" name="email" required />
        </mat-form-field>
      </form>
    </div>
    <div mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancel</button>
      <button mat-flat-button color="primary" [disabled]="!data.user.name || !data.user.email" (click)="onSave()">
        {{ data.isEdit ? 'Update' : 'Add' }}
      </button>
    </div>
  </div>`,
  styleUrls: ['./user-dialog.component.scss']
})
export class UserDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserDialogData
  ) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    this.dialogRef.close(this.data.user);
  }
}