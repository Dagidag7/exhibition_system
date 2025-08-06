import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { ExhibitorService } from '../../services/exhibitor.service';

@Component({
  selector: 'app-add-exhibitor',
  templateUrl: './add-exhibitor.component.html',
  styleUrls: ['./add-exhibitor.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ]
})
export class AddExhibitorComponent {
  exhibitor: any = {};

  constructor(
    private dialogRef: MatDialogRef<AddExhibitorComponent>,
    private snackBar: MatSnackBar,
    private exhibitorService: ExhibitorService
  ) {}

  onSubmit() {
    if (this.validateForm()) {
      this.dialogRef.close(this.exhibitor);
      this.snackBar.open('Exhibitor created successfully! Default password: Welcome123', 'Close', {
        duration: 8000,
        panelClass: ['success-snackbar']
      });
    }
  }

  validateForm(): boolean {
    // Check required fields
    if (!this.exhibitor.name || !this.exhibitor.email || !this.exhibitor.contactPerson || !this.exhibitor.boothNumber) {
      this.snackBar.open('Please fill in all required fields!', 'Close', {
        duration: 3000
      });
      return false;
    }

    // Validate email format
    if (!this.exhibitorService.isValidEmailFormat(this.exhibitor.email)) {
      this.snackBar.open('Please enter a valid email address!', 'Close', {
        duration: 3000
      });
      return false;
    }

    return true;
  }

  onEmailBlur(): void {
    if (this.exhibitor.email && this.exhibitorService.isValidEmailFormat(this.exhibitor.email)) {
      this.checkEmailExists();
    }
  }

  private checkEmailExists(): void {
    this.exhibitorService.checkEmailExists(this.exhibitor.email).subscribe({
      next: (response) => {
        if (response.exists) {
          this.snackBar.open('This email is already registered!', 'Close', {
            duration: 4000
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
}