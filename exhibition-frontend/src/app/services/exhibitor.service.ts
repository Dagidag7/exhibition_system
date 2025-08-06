import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Exhibitor {
  exhibitorId: number;
  name: string;
  contactPerson: string;
  email: string;
  boothNumber: string;
  productIds: string;
  logoUrl: string;
  floorNumber?: string;
  status?: string;
  passwordChanged?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ExhibitorService {
  private apiUrl = 'http://localhost:8888/exhibitors';

  constructor(private http: HttpClient) {}

  addExhibitor(exhibitor: Exhibitor): Observable<any> {
    return this.http.post(this.apiUrl, exhibitor);
  }

  getExhibitors(): Observable<Exhibitor[]> {
    return this.http.get<Exhibitor[]>(this.apiUrl);
  }

  getExhibitorById(id: number): Observable<Exhibitor> {
    return this.http.get<Exhibitor>(`${this.apiUrl}/${id}`);
  }

  updateExhibitor(id: number, exhibitor: Exhibitor): Observable<Exhibitor> {
    return this.http.put<Exhibitor>(`${this.apiUrl}/${id}`, exhibitor);
  }

  deleteExhibitor(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  changePassword(id: number, password: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/password`, { password });
  }
}