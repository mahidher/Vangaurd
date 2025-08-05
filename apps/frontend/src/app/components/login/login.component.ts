import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from 'src/app/services/user.service';
import { User } from '../../models';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatInput } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, MatButtonModule, MatInput, MatFormFieldModule],
  template: `
    <div class="login-container">
      <form class="login-form" (submit)="login()">
        <h2>Sign In</h2>
        <mat-form-field appearance="fill">
          <mat-label>Username</mat-label>
          <input type="text" matInput [(ngModel)]="username" required autocomplete="off" name="username">
        </mat-form-field>
        <button mat-button type="submit" [disabled]="!username">Login</button>
      </form>
    </div>
  `,
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit, OnDestroy {

  username!: string;
  loginSubscription!: Subscription;

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log('Login')
  }

  login(): void {
    this.loginSubscription = this.userService.login(this.username).subscribe({
      next: (user: User) => {
        if (!user) {
          alert('Login failed. Please check your username.');
          return;
        }
        console.log('Login successful:', user);
        this.router.navigate([user.isAdmin ? '/admin' : `/transactions`]);
      },
      error: error => {
        console.error('Login failed:', error);
        alert('Login failed. Please try again later.');
      }
    });
  }

  ngOnDestroy(): void {
    this.loginSubscription?.unsubscribe();
  }
}
