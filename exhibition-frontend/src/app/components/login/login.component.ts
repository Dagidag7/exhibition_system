import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    FormsModule,
    MatSnackBarModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginData = { email: '', password: '' };
  hidePassword = true;
  isSubmitting = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  onSubmit(): void {
    if (!this.loginData.email || !this.loginData.password) {
      this.snackBar.open('Please fill in all fields', 'Close', { duration: 3000 });
      return;
    }

    this.isSubmitting = true;
    this.authService.login(this.loginData.email, this.loginData.password).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        
        // Redirect based on user role
        if (response.user.role === 'admin') {
          this.router.navigate(['/admin']);
        } else if (response.user.role === 'exhibitor') {
          this.router.navigate(['/exhibitor']);
        } else {
          // For attendees, just show success message and stay on login page
          this.snackBar.open('Login successful! Attendees can view the exhibition on the main page.', 'Close', { duration: 5000 });
          this.router.navigate(['/']);
        }
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Login error:', error);
        this.snackBar.open('Login failed. Please check your credentials.', 'Close', { duration: 3000 });
      }
    });
  }

  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }
} 