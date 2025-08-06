import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Floor, FloorService } from '../../services/floor.service';

@Component({
  selector: 'app-add-floor',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule
  ],
  templateUrl: './add-floor.component.html',
  styleUrls: ['./add-floor.component.css']
})
export class AddFloorComponent implements OnInit {
  floorForm: FormGroup;
  isEditMode = false;
  floorId?: number;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddFloorComponent>,
    private floorService: FloorService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { floor?: Floor }
  ) {
    this.floorForm = this.fb.group({
      floorNumber: ['', [Validators.required, Validators.min(1)]],
      layoutImage: [''],
      exhibitorIds: [''],
      conferenceIds: ['']
    });
  }

  ngOnInit(): void {
    if (this.data?.floor) {
      this.isEditMode = true;
      this.floorId = this.data.floor.floorId;
      this.floorForm.patchValue({
        floorNumber: this.data.floor.floorNumber,
        layoutImage: this.data.floor.layoutImage || '',
        exhibitorIds: this.data.floor.exhibitorIds || '',
        conferenceIds: this.data.floor.conferenceIds || ''
      });
    }
  }

  onSubmit() {
    if (this.floorForm.valid) {
      const formValue = this.floorForm.value;
      
      if (this.isEditMode && this.floorId) {
        // Update existing floor
        const updateData: Floor = {
          floorId: this.floorId,
          floorNumber: formValue.floorNumber,
          layoutImage: formValue.layoutImage || undefined,
          exhibitorIds: formValue.exhibitorIds || undefined,
          conferenceIds: formValue.conferenceIds || undefined
        };
        
        this.floorService.updateFloor(this.floorId, updateData).subscribe({
          next: (response) => {
            this.snackBar.open('Floor updated successfully!', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
            this.dialogRef.close(response);
          },
          error: (error) => {
            this.snackBar.open('Failed to update floor: ' + (error.error?.message || error.message), 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
          }
        });
      } else {
        // Create new floor
        const floor: Omit<Floor, 'floorId'> = {
          floorNumber: formValue.floorNumber,
          layoutImage: formValue.layoutImage || undefined,
          exhibitorIds: formValue.exhibitorIds || undefined,
          conferenceIds: formValue.conferenceIds || undefined
        };
        
        this.floorService.createFloor(floor).subscribe({
          next: (response) => {
            this.snackBar.open('Floor added successfully!', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
            this.dialogRef.close(response);
          },
          error: (error) => {
            this.snackBar.open('Failed to add floor: ' + (error.error?.message || error.message), 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
          }
        });
      }
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
} 