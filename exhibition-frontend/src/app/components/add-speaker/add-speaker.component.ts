import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Speaker, SpeakerService } from '../../services/speaker.service';

@Component({
  selector: 'app-add-speaker',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './add-speaker.component.html',
  styleUrls: ['./add-speaker.component.css']
})
export class AddSpeakerComponent implements OnInit {
  speakerForm: FormGroup;
  isEditMode: boolean = false;
  speakerId?: number;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddSpeakerComponent>,
    private speakerService: SpeakerService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.speakerForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      bio: [''],
      expertise: ['', Validators.maxLength(200)],
      email: ['', [Validators.email, Validators.maxLength(100)]],
      phone: ['', Validators.maxLength(20)],
      organization: ['', Validators.maxLength(100)]
    });
  }

  ngOnInit(): void {
    if (this.data && this.data.speaker) {
      this.isEditMode = true;
      this.speakerId = this.data.speaker.speakerId;
      this.speakerForm.patchValue({
        name: this.data.speaker.name,
        bio: this.data.speaker.bio || '',
        expertise: this.data.speaker.expertise || '',
        email: this.data.speaker.email || '',
        phone: this.data.speaker.phone || '',
        organization: this.data.speaker.organization || ''
      });
    }
  }

  onSubmit() {
    if (this.speakerForm.valid) {
      const formValue = this.speakerForm.value;
      
      if (this.isEditMode && this.speakerId) {
        const speaker: Speaker = {
          speakerId: this.speakerId,
          name: formValue.name,
          bio: formValue.bio,
          expertise: formValue.expertise,
          email: formValue.email,
          phone: formValue.phone,
          organization: formValue.organization
        };
        this.speakerService.updateSpeaker(speaker).subscribe({
          next: () => {
            this.snackBar.open('Speaker updated successfully!', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
            this.dialogRef.close(speaker);
          },
          error: (error) => {
            this.snackBar.open('Failed to update speaker: ' + (error.error?.message || error.message), 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
          }
        });
      } else {
        const speaker: Speaker = {
          speakerId: 0,
          name: formValue.name,
          bio: formValue.bio,
          expertise: formValue.expertise,
          email: formValue.email,
          phone: formValue.phone,
          organization: formValue.organization
        };
        this.speakerService.addSpeaker(speaker).subscribe({
          next: () => {
            this.snackBar.open('Speaker added successfully!', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
            this.dialogRef.close(speaker);
          },
          error: (error) => {
            this.snackBar.open('Failed to add speaker: ' + (error.error?.message || error.message), 'Close', {
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