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

  checkEmailExists(email: string): Observable<{email: string, exists: boolean}> {
    const encodedEmail = encodeURIComponent(email);
    return this.http.get<{email: string, exists: boolean}>(`http://localhost:8888/exhibitors/check-email/${encodedEmail}`);
  }

  /**
   * Validates email format using a regex pattern
   * @param email The email to validate
   * @returns true if email format is valid
   */
  isValidEmailFormat(email: string): boolean {
    if (!email) return false;
    const emailPattern = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/;
    return emailPattern.test(email.trim());
  }
}