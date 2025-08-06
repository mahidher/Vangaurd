import { Routes } from '@angular/router';
import { AuthService } from './services/auth.service';

export const routes: Routes = [

  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'admin', 
    loadComponent: () => import('./components/admin/admin.component').then(m => m.AdminComponent),
    canActivate: [AuthService]
  },
  {
    path: 'analytics', 
    loadComponent: () => import('./components/transaction-analytics/transaction-analytics.component').then(m => m.TransactionAnalyticsComponent),
    canActivate: [AuthService]
  },
  {
    path: 'transactions',
    loadComponent: () => import('./components/transactions/transactions.component').then(m => m.TransactionsComponent),
    canActivate: [AuthService]
  }

]; 