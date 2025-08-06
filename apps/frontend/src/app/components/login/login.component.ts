import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from 'src/app/services/user.service';
import { User } from '../../models';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatInput } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-login',
  imports: [FormsModule, MatButtonModule, MatInput, MatFormFieldModule, MatProgressSpinnerModule],
  template: `
    <div class="login-container">
      <form class="login-form" (submit)="login()">
        <h2>Sign In</h2>
        <mat-form-field appearance="fill">
          <mat-label>Username</mat-label>
          <input type="text" matInput [(ngModel)]="username" required autocomplete="off" name="username">
        </mat-form-field>
        <button mat-button type="submit" [disabled]="!username">
          @if (showLoader) {
            <div class="spinner">
              <mat-spinner [diameter]="30"></mat-spinner>
            </div>
          }
          @else {
            Login
          }
        </button>
      </form>
    </div>
  `,
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit, OnDestroy {

  username!: string;
  loginSubscription!: Subscription;
  showLoader: boolean = false;

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log('Login')
  }

  login(): void {
    this.showLoader = true;
    if (this.username.trim().toLowerCase() === 'admin') {
      const adminUser: User = {
        userName: 'admin',
        isAdmin: true
      }
      this.router.navigate(['/admin']);
      this.userService.loggedInUser$.next(adminUser);
      this.showLoader = false;
      return;
    }
    this.loginSubscription = this.userService.login(this.username).subscribe({
      next: (user: User) => {
        if (!user) {
          alert('Login failed. Please check your username.');
          return;
        }
        console.log('Login successful:', user);
        user.isAdmin = false;
        this.showLoader = false;
        this.router.navigate([user.isAdmin ? '/admin' : `/transactions`]);
      },
      error: error => {
        console.error('Login failed:', error);
        this.showLoader = false;
        alert('Login failed. Please try again later.');
      }
    });
  }

  ngOnDestroy(): void {
    this.loginSubscription?.unsubscribe();
  }
}
