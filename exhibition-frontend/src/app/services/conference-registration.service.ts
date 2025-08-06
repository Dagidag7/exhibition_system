import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ConferenceRegistration {
  registrationId?: number;
  conferenceId: number;
  attendeeId?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  company: string;
  jobTitle: string;
  dietaryRestrictions?: string;
  specialRequirements?: string;
  registrationDate?: string;
  status?: string;
}

export interface RegistrationResponse {
  success: boolean;
  message: string;
  registrationId?: number;
  registration?: ConferenceRegistration;
}

@Injectable({
  providedIn: 'root'
})
export class ConferenceRegistrationService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  registerForConference(registration: ConferenceRegistration): Observable<RegistrationResponse> {
    return this.http.post<RegistrationResponse>(`${this.apiUrl}/conference-registrations`, registration);
  }

  getRegistrationsByConference(conferenceId: number): Observable<{success: boolean, registrations: ConferenceRegistration[]}> {
    return this.http.get<{success: boolean, registrations: ConferenceRegistration[]}>(`${this.apiUrl}/conference-registrations/conference/${conferenceId}`);
  }

  getRegistrationsByEmail(email: string): Observable<{success: boolean, registrations: ConferenceRegistration[]}> {
    return this.http.get<{success: boolean, registrations: ConferenceRegistration[]}>(`${this.apiUrl}/conference-registrations/email/${email}`);
  }

  getAllRegistrations(): Observable<{success: boolean, registrations: ConferenceRegistration[]}> {
    return this.http.get<{success: boolean, registrations: ConferenceRegistration[]}>(`${this.apiUrl}/conference-registrations`);
  }

  getRegistrationById(registrationId: number): Observable<{success: boolean, registration: ConferenceRegistration}> {
    return this.http.get<{success: boolean, registration: ConferenceRegistration}>(`${this.apiUrl}/conference-registrations/${registrationId}`);
  }

  deleteRegistration(registrationId: number): Observable<{success: boolean, message: string}> {
    return this.http.delete<{success: boolean, message: string}>(`${this.apiUrl}/conference-registrations/${registrationId}`);
  }
} 