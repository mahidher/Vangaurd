import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],
  template: `
    <div class="app-container">
      <header class="header">
        <h1>Vanguard Monorepo</h1>
        <nav>
          <a routerLink="/" routerLinkActive="active" class="nav-link">Home</a>
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