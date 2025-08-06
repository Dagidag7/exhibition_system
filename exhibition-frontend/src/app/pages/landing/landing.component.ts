import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { SponsorService } from '../../services/sponsor.service';
import { ExhibitorService } from '../../services/exhibitor.service';
import { ProductService } from '../../services/product.service';
import { ConferenceService } from '../../services/conference.service';
import { RegisterAttendeeComponent } from '../../components/register-attendee/register-attendee.component';
import { RegisterConferenceComponent } from '../../components/register-conference/register-conference.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {
  sponsors: any[] = [];
  exhibitors: any[] = [];
  products: any[] = [];
  conferences: any[] = [];
  currentYear = new Date().getFullYear();

  constructor(
    private sponsorService: SponsorService,
    private exhibitorService: ExhibitorService,
    private productService: ProductService,
    private conferenceService: ConferenceService,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSponsors();
    this.loadExhibitors();
    this.loadProducts();
    this.loadConferences();
  }

  loadSponsors(): void {
    this.sponsorService.getSponsors().subscribe({
      next: (sponsors) => {
        this.sponsors = sponsors;
      },
      error: (error) => {
        console.error('Error loading sponsors:', error);
      }
    });
  }

  loadExhibitors(): void {
    this.exhibitorService.getExhibitors().subscribe({
      next: (exhibitors) => {
        this.exhibitors = exhibitors;
      },
      error: (error) => {
        console.error('Error loading exhibitors:', error);
      }
    });
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products = products;
      },
      error: (error) => {
        console.error('Error loading products:', error);
      }
    });
  }

  loadConferences(): void {
    this.conferenceService.getConferences().subscribe({
      next: (conferences) => {
        this.conferences = conferences;
      },
      error: (error) => {
        console.error('Error loading conferences:', error);
      }
    });
  }

  getFormattedDate(dateString: string): string {
    if (!dateString) return '';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
      });
    } catch (error) {
      return dateString;
    }
  }

  getFormattedTime(timeString: string): string {
    if (!timeString) return '';
    try {
      const [hours, minutes] = timeString.split(':');
      const hour = parseInt(hours);
      const ampm = hour >= 12 ? 'PM' : 'AM';
      const displayHour = hour % 12 || 12;
      return `${displayHour}:${minutes} ${ampm}`;
    } catch (error) {
      return timeString;
    }
  }

  openRegisterDialog(): void {
    const dialogRef = this.dialog.open(RegisterAttendeeComponent, {
      width: '450px',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Registration successful');
      }
    });
  }

  openConferenceRegistrationDialog(conference: any): void {
    const dialogRef = this.dialog.open(RegisterConferenceComponent, {
      width: '600px',
      maxHeight: '90vh',
      disableClose: true,
      data: { conference: conference }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Conference registration successful:', result);
        // You can add additional logic here, such as:
        // - Sending confirmation email
        // - Updating conference attendance count
        // - Redirecting to payment page
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
