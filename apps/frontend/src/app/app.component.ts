import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, RouterOutlet],
  template: `
    <div class="app-container">
      <header class="header">
        <h1>Vanguard Monorepo</h1>
        <nav>
          <a routerLink="/home" routerLinkActive="active" class="nav-link">Home</a>
          <a routerLink="/login" routerLinkActive="active" class="nav-link">Login</a>
          <a routerLink="/admin" routerLinkActive="active" class="nav-link">Admin</a>
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
} 