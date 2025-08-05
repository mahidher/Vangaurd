import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { UserService } from './services/user.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, RouterLink, MatButtonModule, MatIconModule],
    template: `
    <div class="app-container">
      <header class="header">
        <h1>Vanguard Monorepo</h1>
        <nav>
          <a routerLink="/home" class="nav-link">Home</a>
          @if ((userService.getLoggedInUserValue()) === null) {
            <a routerLink="/login" class="nav-link">Login</a>
          }
          @else {
            <button mat-flat-button (click)="logout()">Logout <mat-icon>logout</mat-icon></button>
          }
        </nav>
      </header>
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'vanguard-frontend';

  constructor(public userService: UserService) {}

  logout() {
    this.userService.logout();
  }
} 