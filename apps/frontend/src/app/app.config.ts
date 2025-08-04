// src/app/app.config.ts
import { provideRouter } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { AdminComponent } from './components/admin/admin.component';

export const appConfig = {
  providers: [
    provideRouter([
      { path: 'home', component: HomeComponent },
      { path: 'admin', component: AdminComponent },
      { path: '**', redirectTo: 'home' } // Optional wildcard
    ])
  ]
};
