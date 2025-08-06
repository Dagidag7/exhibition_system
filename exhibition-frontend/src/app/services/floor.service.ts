import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Floor {
  floorId: number;
  floorNumber: number;
  layoutImage?: string;
  exhibitorIds?: string;
  conferenceIds?: string;
}

@Injectable({
  providedIn: 'root'
})
export class FloorService {
  private apiUrl = 'http://localhost:8888/floors';

  constructor(private http: HttpClient) {}

  getFloors(): Observable<Floor[]> {
    return this.http.get<Floor[]>(this.apiUrl);
  }

  getFloorById(id: number): Observable<Floor> {
    return this.http.get<Floor>(`${this.apiUrl}/${id}`);
  }

  createFloor(floor: Omit<Floor, 'floorId'>): Observable<Floor> {
    return this.http.post<Floor>(this.apiUrl, floor);
  }

  updateFloor(id: number, floor: Floor): Observable<Floor> {
    return this.http.put<Floor>(`${this.apiUrl}/${id}`, floor);
  }

  deleteFloor(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
} 