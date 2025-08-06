import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register-conference',
  templateUrl: './register-conference.component.html',
  styleUrls: ['./register-conference.component.css'],
  imports: [
      CommonModule,
      ReactiveFormsModule,
      MatIconModule,
      MatFormFieldModule,
      MatInputModule,
      MatCheckboxModule,
      MatButtonModule,
      MatDialogModule,
      MatSnackBarModule
    ]
})
export class RegisterConferenceComponent implements OnInit {
  registrationForm!: FormGroup;
  isSubmitting = false;
  conference: any;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<RegisterConferenceComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private snackBar: MatSnackBar,
    private http: HttpClient
  ) {
    this.conference = data.conference;
  }

  ngOnInit() {
    this.registrationForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[\+]?[1-9][\d]{0,15}$/)]],
      company: ['', [Validators.required, Validators.minLength(2)]],
      jobTitle: ['', [Validators.required, Validators.minLength(2)]],
      dietaryRestrictions: [''],
      specialRequirements: [''],
      termsAccepted: [false, [Validators.requiredTrue]],
      marketingUpdates: [false]
    });
  }

  onSubmit() {
    if (this.registrationForm.valid) {
      this.isSubmitting = true;
      
      const registrationData = {
        conferenceId: this.conference.conferenceId,
        firstName: this.registrationForm.get('firstName')?.value,
        lastName: this.registrationForm.get('lastName')?.value,
        email: this.registrationForm.get('email')?.value,
        phone: this.registrationForm.get('phone')?.value,
        company: this.registrationForm.get('company')?.value,
        jobTitle: this.registrationForm.get('jobTitle')?.value,
        dietaryRestrictions: this.registrationForm.get('dietaryRestrictions')?.value || '',
        specialRequirements: this.registrationForm.get('specialRequirements')?.value || ''
      };

      // Call the real API
      this.http.post(`${environment.apiUrl}/conference-registrations`, registrationData)
        .subscribe({
          next: (response: any) => {
            this.isSubmitting = false;
            if (response.success) {
              this.snackBar.open('Registration successful! You will receive a confirmation email shortly.', 'Close', { 
                duration: 5000, 
                panelClass: ['success-snackbar'] 
              });
              this.dialogRef.close(true);
            } else {
              this.snackBar.open('Registration failed: ' + response.message, 'Close', { 
                duration: 5000, 
                panelClass: ['error-snackbar'] 
              });
            }
          },
          error: (error) => {
            this.isSubmitting = false;
            this.snackBar.open('Registration failed: ' + (error.error?.message || 'Network error'), 'Close', { 
              duration: 5000, 
              panelClass: ['error-snackbar'] 
            });
          }
        });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

  getFormattedDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }

  getFormattedTime(timeString: string): string {
    if (!timeString) return '';
    return timeString;
  }
} 