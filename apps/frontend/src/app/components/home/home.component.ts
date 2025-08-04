import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpClient } from '@angular/common/http';

interface ApiResponse {
  message: string;
  timestamp: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  template: `
    <div class="home-container">
      <h2>Welcome to Vanguard</h2>
      <p>This is a modern monorepo with Angular frontend and Spring Boot backend.</p>
      
      <div class="api-section">
        <h3>Backend Connection Test</h3>
        <button (click)="testBackendConnection()" class="test-button" [disabled]="loading">
          {{ loading ? 'Testing...' : 'Test Backend Connection' }}
        </button>
        
        <div class="result" *ngIf="result">
          <h4>Response from Backend:</h4>
          <div class="response-box">
            <p><strong>Message:</strong> {{ result.message }}</p>
            <p><strong>Timestamp:</strong> {{ result.timestamp }}</p>
          </div>
        </div>
        
        <div class="error" *ngIf="error">
          <h4>Error:</h4>
          <p>{{ error }}</p>
          <small>Make sure the Spring Boot backend is running on port 8080</small>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  result: ApiResponse | null = null;
  error: string | null = null;
  loading = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    // Component initialization
  }

  testBackendConnection(): void {
    this.loading = true;
    this.result = null;
    this.error = null;

    this.http.get<ApiResponse>('http://localhost:8080/api/hello')
      .subscribe({
        next: (response) => {
          this.result = response;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to connect to backend: ' + err.message;
          this.loading = false;
        }
      });
  }
} 