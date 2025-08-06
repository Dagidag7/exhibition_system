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
import { SponsorService, Sponsor } from '../../services/sponsor.service';

@Component({
  selector: 'app-add-sponsor',
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
  templateUrl: './add-sponsor.component.html',
  styleUrls: ['./add-sponsor.component.css']
})
export class AddSponsorComponent implements OnInit {
  sponsorForm: FormGroup;
  isEditMode = false;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private sponsorService: SponsorService,
    private dialogRef: MatDialogRef<AddSponsorComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { sponsor?: Sponsor }
  ) {
    this.sponsorForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      contactPerson: ['', [Validators.required, Validators.maxLength(100)]],
      contributionAmount: ['', [Validators.required, Validators.min(0)]],
      benefits: [''],
      logoUrl: ['']
    });
  }

  ngOnInit(): void {
    if (this.data?.sponsor) {
      this.isEditMode = true;
      this.sponsorForm.patchValue({
        name: this.data.sponsor.name,
        contactPerson: this.data.sponsor.contactPerson,
        contributionAmount: this.data.sponsor.contributionAmount,
        benefits: this.data.sponsor.benefits || '',
        logoUrl: this.data.sponsor.logoUrl || ''
      });
    }
  }

  onSubmit(): void {
    if (this.sponsorForm.valid) {
      this.loading = true;
      const sponsorData = this.sponsorForm.value;

      if (this.isEditMode && this.data.sponsor) {
        // Update existing sponsor
        const updatedSponsor: Sponsor = {
          ...this.data.sponsor,
          ...sponsorData
        };

        this.sponsorService.updateSponsor(this.data.sponsor.sponsorId, updatedSponsor).subscribe({
          next: (response) => {
            this.snackBar.open('Sponsor updated successfully!', 'Close', { duration: 3000 });
            this.dialogRef.close(response);
          },
          error: (error) => {
            this.snackBar.open('Error updating sponsor: ' + error.message, 'Close', { duration: 5000 });
            this.loading = false;
          }
        });
      } else {
        // Create new sponsor
        this.sponsorService.createSponsor(sponsorData).subscribe({
          next: (response) => {
            this.snackBar.open('Sponsor created successfully!', 'Close', { duration: 3000 });
            this.dialogRef.close(response);
          },
          error: (error) => {
            this.snackBar.open('Error creating sponsor: ' + error.message, 'Close', { duration: 5000 });
            this.loading = false;
          }
        });
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
} 