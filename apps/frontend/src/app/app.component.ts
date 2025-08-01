import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="app-container">
      <header class="header">
        <h1>Vanguard Monorepo</h1>
        <nav>
          <a routerLink="/home" class="nav-link">Home</a>
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