import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { AttendeeService } from '../../services/attendee.service';

interface Attendee {
  name: string;
  email: string;
  phone: string;
  password: string;
}

@Component({
  selector: 'app-register-attendee',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
    MatSnackBarModule,
    MatIconModule,
    FormsModule
  ],
  templateUrl: './register-attendee.component.html',
  styleUrls: ['./register-attendee.component.css']
})
export class RegisterAttendeeComponent {
  attendee: Attendee = {
    name: '',
    email: '',
    phone: '',
    password: ''
  };
  
  confirmPassword: string = '';
  isSubmitting: boolean = false;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<RegisterAttendeeComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private attendeeService: AttendeeService,
    private snackBar: MatSnackBar
  ) {}

  onSubmit() {
    if (this.validateForm()) {
      this.isSubmitting = true;
      
      this.attendeeService.registerAttendee(this.attendee).subscribe({
        next: (response) => {
          this.snackBar.open('Registration successful!', 'Close', {
            duration: 3000,
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
          this.dialogRef.close(response);
        },
        error: (error) => {
          this.snackBar.open('Registration failed. Please try again.', 'Close', {
            duration: 3000,
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
          console.error('Registration error:', error);
        },
        complete: () => {
          this.isSubmitting = false;
        }
      });
    }
  }

  validateForm(): boolean {
    // Check if all fields are filled
    if (!this.attendee.name || !this.attendee.email || !this.attendee.phone || !this.attendee.password) {
      this.snackBar.open('Please fill in all fields!', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top'
      });
      return false;
    }

    // Enhanced email validation
    if (!this.attendeeService.isValidEmailFormat(this.attendee.email)) {
      this.snackBar.open('Please enter a valid email address!', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top'
      });
      return false;
    }

    // Check if passwords match
    if (this.attendee.password !== this.confirmPassword) {
      this.snackBar.open('Passwords do not match!', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top'
      });
      return false;
    }

    // Check password strength (minimum 6 characters)
    if (this.attendee.password.length < 6) {
      this.snackBar.open('Password must be at least 6 characters long!', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top'
      });
      return false;
    }

    return true;
  }

  onEmailBlur(): void {
    if (this.attendee.email && this.attendeeService.isValidEmailFormat(this.attendee.email)) {
      this.checkEmailExists();
    }
  }

  private checkEmailExists(): void {
    this.attendeeService.checkEmailExists(this.attendee.email).subscribe({
      next: (response) => {
        if (response.exists) {
          this.snackBar.open('This email is already registered!', 'Close', {
            duration: 4000,
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
        }
      },
      error: (error) => {
        console.error('Error checking email:', error);
      }
    });
  }

  onCancel() {
    this.dialogRef.close();
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  getPasswordStrength(): string {
    const password = this.attendee.password;
    if (password.length === 0) return '';
    if (password.length < 6) return 'weak';
    if (password.length < 10) return 'medium';
    return 'strong';
  }

  getPasswordStrengthColor(): string {
    const strength = this.getPasswordStrength();
    switch (strength) {
      case 'weak': return '#f44336';
      case 'medium': return '#ff9800';
      case 'strong': return '#4caf50';
      default: return '#757575';
    }
  }
}
