import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DatabaseHealth {
  status: string;
  database: string;
  version: string;
  timestamp: number;
}

export interface TableSchema {
  tableName: string;
  columns: Column[];
  error?: boolean;
  message?: string;
}

export interface Column {
  name: string;
  type: string;
  nullable: boolean;
  default: string | null;
  maxLength: number | null;
}

export interface DatabaseStats {
  attendeeCount: number;
  exhibitorCount: number;
  sponsorCount: number;
  partnerCount: number;
  floorCount: number;
  productCount: number;
  speakerCount: number;
  conferenceCount: number;
  timestamp: number;
  error?: boolean;
  message?: string;
}

export interface SchemaUpdateRequest {
  addLocation?: boolean;
  addTime?: boolean;
}

export interface SchemaUpdateResponse {
  success: boolean;
  message: string;
  operations?: string[];
  timestamp: number;
}

export interface SampleDataResponse {
  success: boolean;
  message: string;
  count?: number;
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class DatabaseService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Get database health status
  getDatabaseHealth(): Observable<DatabaseHealth> {
    return this.http.get<DatabaseHealth>(`${this.apiUrl}/database/health`);
  }

  // Get all tables in the database
  getAllTables(): Observable<{ tables: string[] }> {
    return this.http.get<{ tables: string[] }>(`${this.apiUrl}/database/tables`);
  }

  // Get schema for a specific table
  getTableSchema(tableName: string): Observable<TableSchema> {
    return this.http.get<TableSchema>(`${this.apiUrl}/database/schema/${tableName}`);
  }

  // Update conference table schema
  updateConferenceSchema(updates: SchemaUpdateRequest): Observable<SchemaUpdateResponse> {
    return this.http.post<SchemaUpdateResponse>(`${this.apiUrl}/database/update-conference-schema`, updates);
  }

  // Add sample conference data
  addSampleConferences(): Observable<SampleDataResponse> {
    return this.http.post<SampleDataResponse>(`${this.apiUrl}/database/add-sample-conferences`, {});
  }

  // Get database statistics
  getDatabaseStats(): Observable<DatabaseStats> {
    return this.http.get<DatabaseStats>(`${this.apiUrl}/database/stats`);
  }
} 