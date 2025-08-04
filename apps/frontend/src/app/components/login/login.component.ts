import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from 'src/app/services/user.service';
import { User } from '../../models';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  template: `
    <div class="login-container">
      <form class="login-form" (submit)="login()">
        <h2>Sign In</h2>
        <input
          type="text"
          placeholder="Username"
          [(ngModel)]="username"
          name="username"
          required
          autocomplete="off"
        />
        <button type="submit">Login</button>
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
        this.router.navigate([user.isAdmin ? '/admin' : `/userhome/${user.userId}`]);
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
