import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Attendee {
  attendeeId?: number;
  name: string;
  email: string;
  phone: string;
  password: string;
  registrationDate?: string;
  status?: string;
}

export interface EmailCheckResponse {
  email: string;
  exists: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AttendeeService {
  private apiUrl = 'http://localhost:8888/attendees';

  constructor(private http: HttpClient) {}

  registerAttendee(attendee: Attendee): Observable<Attendee> {
    return this.http.post<Attendee>(this.apiUrl, attendee);
  }

  getAttendees(): Observable<Attendee[]> {
    return this.http.get<Attendee[]>(this.apiUrl);
  }

  getAttendeeById(id: number): Observable<Attendee> {
    return this.http.get<Attendee>(`${this.apiUrl}/${id}`);
  }

  updateAttendee(id: number, attendee: Attendee): Observable<Attendee> {
    return this.http.put<Attendee>(`${this.apiUrl}/${id}`, attendee);
  }

  deleteAttendee(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  checkEmailExists(email: string): Observable<EmailCheckResponse> {
    const encodedEmail = encodeURIComponent(email);
    return this.http.get<EmailCheckResponse>(`http://localhost:8888/attendees/check-email/${encodedEmail}`);
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