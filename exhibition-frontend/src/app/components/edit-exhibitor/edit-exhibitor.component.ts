import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ExhibitorService, Exhibitor } from '../../services/exhibitor.service';

@Component({
  selector: 'app-edit-exhibitor',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule
  ],
  templateUrl: './edit-exhibitor.component.html',
  styleUrls: ['./edit-exhibitor.component.css']
})
export class EditExhibitorComponent implements OnInit {
  exhibitorForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private exhibitorService: ExhibitorService,
    private dialogRef: MatDialogRef<EditExhibitorComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { exhibitor: Exhibitor }
  ) {
    this.exhibitorForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      contactPerson: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      boothNumber: ['', [Validators.required]],
      productIds: [''],
      logoUrl: [''],
      floorNumber: [''],
      status: ['active', [Validators.required]]
    });
  }

  ngOnInit(): void {
    if (this.data?.exhibitor) {
      this.exhibitorForm.patchValue({
        name: this.data.exhibitor.name,
        contactPerson: this.data.exhibitor.contactPerson,
        email: this.data.exhibitor.email,
        boothNumber: this.data.exhibitor.boothNumber,
        productIds: this.data.exhibitor.productIds || '',
        logoUrl: this.data.exhibitor.logoUrl || '',
        floorNumber: this.data.exhibitor.floorNumber || '',
        status: this.data.exhibitor.status || 'active'
      });
    }
  }

  onSubmit(): void {
    if (this.exhibitorForm.valid) {
      this.loading = true;
      const formData = this.exhibitorForm.value;
      
      const updateData: Exhibitor = {
        exhibitorId: this.data.exhibitor.exhibitorId,
        name: formData.name,
        contactPerson: formData.contactPerson,
        email: formData.email,
        boothNumber: formData.boothNumber,
        productIds: formData.productIds,
        logoUrl: formData.logoUrl,
        floorNumber: formData.floorNumber,
        status: formData.status
      };

      this.exhibitorService.updateExhibitor(this.data.exhibitor.exhibitorId, updateData).subscribe({
        next: (response) => {
          this.snackBar.open('Exhibitor updated successfully!', 'Close', { duration: 3000 });
          this.dialogRef.close(response);
        },
        error: (error) => {
          this.snackBar.open('Error updating exhibitor: ' + error.message, 'Close', { duration: 5000 });
          this.loading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
} 