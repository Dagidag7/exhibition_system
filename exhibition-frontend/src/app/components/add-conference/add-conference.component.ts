import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Conference, ConferenceService } from '../../services/conference.service';

@Component({
  selector: 'app-add-conference',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './add-conference.component.html',
  styleUrls: ['./add-conference.component.css']
})
export class AddConferenceComponent implements OnInit {
  conferenceForm: FormGroup;
  isEditMode: boolean = false;
  conferenceId?: number;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddConferenceComponent>,
    private conferenceService: ConferenceService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.conferenceForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(200)]],
      description: ['', Validators.required],
      date: ['', Validators.required],
      time: ['', Validators.required],
      location: ['', Validators.required],
      speaker: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.data && this.data.conference) {
      this.isEditMode = true;
      this.conferenceId = this.data.conference.conferenceId;
      this.conferenceForm.patchValue({
        title: this.data.conference.title,
        description: this.data.conference.description,
        date: this.data.conference.date,
        time: this.data.conference.time,
        location: this.data.conference.location,
        speaker: this.data.conference.speaker
      });
    }
  }

  onSubmit() {
    if (this.conferenceForm.valid) {
      const formValue = this.conferenceForm.value;
      
      if (this.isEditMode && this.conferenceId) {
        const conference: Conference = {
          conferenceId: this.conferenceId,
          title: formValue.title,
          description: formValue.description,
          date: formValue.date,
          time: formValue.time,
          location: formValue.location,
          speaker: formValue.speaker
        };
        this.conferenceService.updateConference(conference).subscribe({
          next: () => {
            this.snackBar.open('Conference updated successfully!', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
            this.dialogRef.close(conference);
          },
          error: (error) => {
            this.snackBar.open('Failed to update conference: ' + (error.error?.message || error.message), 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
          }
        });
      } else {
        const conference: Conference = {
          conferenceId: 0,
          title: formValue.title,
          description: formValue.description,
          date: formValue.date,
          time: formValue.time,
          location: formValue.location,
          speaker: formValue.speaker
        };
        this.conferenceService.addConference(conference).subscribe({
          next: () => {
            this.snackBar.open('Conference added successfully!', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
            this.dialogRef.close(conference);
          },
          error: (error) => {
            this.snackBar.open('Failed to add conference: ' + (error.error?.message || error.message), 'Close', {
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