import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';

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
    private snackBar: MatSnackBar
  ) {}

  onSubmit() {
    if (this.exhibitor) {
      this.dialogRef.close(this.exhibitor);
      this.snackBar.open('Exhibitor created successfully! Default password: Welcome123', 'Close', {
        duration: 8000,
        panelClass: ['success-snackbar']
      });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}