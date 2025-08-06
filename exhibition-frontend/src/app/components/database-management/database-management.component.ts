import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';

import { DatabaseService, DatabaseHealth, TableSchema, DatabaseStats, SchemaUpdateRequest } from '../../services/database.service';

@Component({
  selector: 'app-database-management',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatChipsModule,
    MatDividerModule,
    MatSnackBarModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    ReactiveFormsModule
  ],
  templateUrl: './database-management.component.html',
  styleUrls: ['./database-management.component.css']
})
export class DatabaseManagementComponent implements OnInit {
  databaseHealth: DatabaseHealth | null = null;
  conferenceSchema: TableSchema | null = null;
  databaseStats: DatabaseStats | null = null;
  allTables: string[] = [];
  
  loading = {
    health: false,
    schema: false,
    stats: false,
    tables: false,
    update: false,
    sample: false
  };

  schemaUpdateForm: FormGroup;
  recentOperations: Array<{action: string, timestamp: number, success: boolean, message: string}> = [];

  constructor(
    private databaseService: DatabaseService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private fb: FormBuilder
  ) {
    this.schemaUpdateForm = this.fb.group({
      addLocation: [false],
      addTime: [false]
    });
  }

  ngOnInit(): void {
    this.loadDatabaseHealth();
    this.loadAllTables();
    this.loadDatabaseStats();
    this.loadConferenceSchema();
  }

  loadDatabaseHealth(): void {
    this.loading.health = true;
    this.databaseService.getDatabaseHealth().subscribe({
      next: (health) => {
        this.databaseHealth = health;
        this.loading.health = false;
      },
      error: (error) => {
        console.error('Failed to load database health:', error);
        this.loading.health = false;
        this.snackBar.open('Failed to load database health', 'Close', { duration: 3000 });
      }
    });
  }

  loadAllTables(): void {
    this.loading.tables = true;
    this.databaseService.getAllTables().subscribe({
      next: (response) => {
        this.allTables = response.tables;
        this.loading.tables = false;
      },
      error: (error) => {
        console.error('Failed to load tables:', error);
        this.loading.tables = false;
        this.snackBar.open('Failed to load database tables', 'Close', { duration: 3000 });
      }
    });
  }

  loadDatabaseStats(): void {
    this.loading.stats = true;
    this.databaseService.getDatabaseStats().subscribe({
      next: (stats) => {
        this.databaseStats = stats;
        this.loading.stats = false;
      },
      error: (error) => {
        console.error('Failed to load database stats:', error);
        this.loading.stats = false;
        this.snackBar.open('Failed to load database statistics', 'Close', { duration: 3000 });
      }
    });
  }

  loadConferenceSchema(): void {
    this.loading.schema = true;
    this.databaseService.getTableSchema('conference').subscribe({
      next: (schema) => {
        this.conferenceSchema = schema;
        this.loading.schema = false;
      },
      error: (error) => {
        console.error('Failed to load conference schema:', error);
        this.loading.schema = false;
        this.snackBar.open('Failed to load conference schema', 'Close', { duration: 3000 });
      }
    });
  }

  updateConferenceSchema(): void {
    const updates = this.schemaUpdateForm.value;
    
    // Check if any updates are selected
    if (!updates.addLocation && !updates.addTime) {
      this.snackBar.open('Please select at least one schema update', 'Close', { duration: 3000 });
      return;
    }

    this.loading.update = true;
    this.databaseService.updateConferenceSchema(updates).subscribe({
      next: (response) => {
        this.loading.update = false;
        if (response.success) {
          this.snackBar.open(response.message, 'Close', { duration: 5000 });
          this.addRecentOperation('Update Conference Schema', true, response.message);
          this.loadConferenceSchema(); // Reload schema
        } else {
          this.snackBar.open(response.message, 'Close', { duration: 5000 });
          this.addRecentOperation('Update Conference Schema', false, response.message);
        }
      },
      error: (error) => {
        console.error('Failed to update schema:', error);
        this.loading.update = false;
        const message = error.error?.message || 'Failed to update conference schema';
        this.snackBar.open(message, 'Close', { duration: 5000 });
        this.addRecentOperation('Update Conference Schema', false, message);
      }
    });
  }

  addSampleConferences(): void {
    this.loading.sample = true;
    this.databaseService.addSampleConferences().subscribe({
      next: (response) => {
        this.loading.sample = false;
        if (response.success) {
          this.snackBar.open(response.message, 'Close', { duration: 5000 });
          this.addRecentOperation('Add Sample Conferences', true, response.message);
          this.loadDatabaseStats(); // Reload stats
        } else {
          this.snackBar.open(response.message, 'Close', { duration: 5000 });
          this.addRecentOperation('Add Sample Conferences', false, response.message);
        }
      },
      error: (error) => {
        console.error('Failed to add sample conferences:', error);
        this.loading.sample = false;
        const message = error.error?.message || 'Failed to add sample conferences';
        this.snackBar.open(message, 'Close', { duration: 5000 });
        this.addRecentOperation('Add Sample Conferences', false, message);
      }
    });
  }

  addRecentOperation(action: string, success: boolean, message: string): void {
    this.recentOperations.unshift({
      action,
      timestamp: Date.now(),
      success,
      message
    });
    
    // Keep only last 10 operations
    if (this.recentOperations.length > 10) {
      this.recentOperations = this.recentOperations.slice(0, 10);
    }
  }

  getStatusColor(status: string): string {
    return status === 'healthy' ? 'accent' : 'warn';
  }

  getOperationIcon(success: boolean): string {
    return success ? 'check_circle' : 'error';
  }

  getOperationColor(success: boolean): string {
    return success ? 'accent' : 'warn';
  }

  formatTimestamp(timestamp: number): string {
    return new Date(timestamp).toLocaleString();
  }

  refreshAll(): void {
    this.loadDatabaseHealth();
    this.loadAllTables();
    this.loadDatabaseStats();
    this.loadConferenceSchema();
  }
} 