import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AttendeeService } from '../../services/attendee.service';
import { ExhibitorService, Exhibitor } from '../../services/exhibitor.service';
import { ProductService } from '../../services/product.service';
import { ConferenceService, Conference } from '../../services/conference.service';
import { AddExhibitorComponent } from '../../components/add-exhibitor/add-exhibitor.component';
import { AddConferenceComponent } from '../../components/add-conference/add-conference.component';
import { SpeakerService, Speaker } from '../../services/speaker.service';
import { AddSpeakerComponent } from '../../components/add-speaker/add-speaker.component';
import { PartnerService, Partner } from '../../services/partner.service';
import { AddPartnerComponent } from '../../components/add-partner/add-partner.component';
import { FloorService, Floor } from '../../services/floor.service';
import { AddFloorComponent } from '../../components/add-floor/add-floor.component';
import { SponsorService, Sponsor } from '../../services/sponsor.service';
import { AddSponsorComponent } from '../../components/add-sponsor/add-sponsor.component';
import { EditExhibitorComponent } from '../../components/edit-exhibitor/edit-exhibitor.component';
import { AddProductComponent } from '../../components/add-product/add-product.component';
import { DatabaseManagementComponent } from '../../components/database-management/database-management.component';


interface UserWithStatus {
  id: number;
  name: string;
  email: string;
  contactPerson?: string;
  status: string;
  [key: string]: any;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTabsModule,
    MatChipsModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    DatabaseManagementComponent
    // AddExhibitorComponent
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  currentUser: any;
  activeTab: string = 'dashboard';
  dashboardStats = {
    totalAttendees: 0,
    totalExhibitors: 0,
    totalProducts: 0,
    totalConferences: 0,
    totalSpeakers: 0,
    totalSponsors: 0,
    totalPartners: 0,
    totalFloors: 0,
    activeUsers: 0
  };
  
  attendees: UserWithStatus[] = [];
  exhibitors: UserWithStatus[] = [];
  products: any[] = [];
  conferences: Conference[] = [];
  speakers: Speaker[] = [];
  partners: Partner[] = [];
  floors: Floor[] = [];
  sponsors: Sponsor[] = [];
  
  // Update displayed columns to match HTML template
  attendeeDisplayedColumns = ['name', 'email', 'phone', 'registrationDate', 'status', 'actions'];
  exhibitorDisplayedColumns = ['name', 'email', 'boothNumber', 'floorNumber', 'status', 'actions'];
  productDisplayedColumns = ['name', 'description', 'category', 'exhibitorId', 'actions'];
  speakerDisplayedColumns = ['name', 'email', 'phone', 'specialization', 'actions'];
  conferenceDisplayedColumns = ['title', 'description', 'date', 'time', 'location', 'speaker', 'actions'];
  partnerDisplayedColumns = ['name', 'contactPerson', 'partnershipType', 'benefits', 'actions'];
  floorDisplayedColumns = ['floorNumber', 'layoutImage', 'exhibitorIds', 'conferenceIds', 'actions'];
  sponsorDisplayedColumns = ['name', 'contactPerson', 'contributionAmount', 'benefits', 'actions'];

  // Database Management
  databaseOperations = [
    { id: 'update_conference_schema', name: 'Update Conference Schema', description: 'Update conference table to new structure', status: 'pending' },
    { id: 'insert_sample_conferences', name: 'Insert Sample Conferences', description: 'Add sample conference data', status: 'pending' },
    { id: 'check_database_status', name: 'Check Database Status', description: 'Verify database connectivity and tables', status: 'pending' }
  ];

  displayedColumnsDatabase = ['operation', 'description', 'status', 'actions'];

  constructor(
    private authService: AuthService,
    private attendeeService: AttendeeService,
    private exhibitorService: ExhibitorService,
    private productService: ProductService,
    private conferenceService: ConferenceService,
    private speakerService: SpeakerService,
    private partnerService: PartnerService,
    private floorService: FloorService,
    private sponsorService: SponsorService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadDashboardData();
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  openAddAttendeeDialog(): void {
    this.snackBar.open('Add attendee functionality coming soon', 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  openAddConferenceDialog() {
    const dialogRef = this.dialog.open(AddConferenceComponent, {
      width: '600px'
    });

    dialogRef.afterClosed().subscribe((result: Conference) => {
      if (result) {
        console.log('Conference added, reloading data...');
        this.loadDashboardData(); // Reload conferences and stats after add
        this.snackBar.open('Conference added successfully!', 'Close', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      }
    });
  }

  openAddProductDialog(): void {
    const dialogRef = this.dialog.open(AddProductComponent, {
      width: '500px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadDashboardData();
      }
    });
  }

  editProduct(product: any): void {
    const dialogRef = this.dialog.open(AddProductComponent, {
      width: '500px',
      data: { product: product }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadDashboardData();
      }
    });
  }

  deleteProduct(product: any): void {
    if (confirm(`Are you sure you want to delete the product "${product.name}"?`)) {
      this.productService.deleteProduct(product.productId).subscribe({
        next: () => {
          this.snackBar.open('Product deleted successfully', 'Close', { duration: 3000 });
          this.loadDashboardData();
        },
        error: (error) => {
          console.error('Error deleting product:', error);
          this.snackBar.open('Error deleting product', 'Close', { duration: 3000 });
        }
      });
    }
  }

  loadDashboardData(): void {
    // Load attendees
    this.attendeeService.getAttendees().subscribe({
      next: (attendees: any[]) => {
        this.attendees = attendees.map(attendee => ({
          ...attendee,
          id: attendee.attendeeId, // Map attendeeId to id for the interface
          status: (attendee as any).status || 'active' // Add default status
        }));
        this.dashboardStats.totalAttendees = attendees.length;
        this.dashboardStats.activeUsers = this.attendees.filter(a => a.status === 'active').length;
      },
      error: (error) => {
        console.error('Error loading attendees:', error);
      }
    });

    // Load exhibitors
    this.exhibitorService.getExhibitors().subscribe({
      next: (exhibitors: any[]) => {
        this.exhibitors = exhibitors.map(exhibitor => ({
          ...exhibitor,
          id: exhibitor.exhibitorId, // Map exhibitorId to id for the interface
          status: (exhibitor as any).status || 'active' // Add default status
        }));
        this.dashboardStats.totalExhibitors = exhibitors.length;
        console.log('Exhibitors loaded:', this.exhibitors);
      },
      error: (error) => {
        console.error('Error loading exhibitors:', error);
      }
    });

    // Load products
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.dashboardStats.totalProducts = products.length;
      },
      error: (error) => {
        console.error('Error loading products:', error);
      }
    });

    // Load conferences
    this.conferenceService.getConferences().subscribe({
      next: (conferences) => {
        this.conferences = conferences;
        this.dashboardStats.totalConferences = conferences.length;
        console.log('Conferences loaded:', this.conferences);
      },
      error: (error) => {
        console.error('Error loading conferences:', error);
      }
    });

    this.speakerService.getSpeakers().subscribe({
      next: (speakers) => {
        this.speakers = speakers;
        this.dashboardStats.totalSpeakers = speakers.length;
        console.log('Speakers loaded:', this.speakers);
      },
      error: (error) => { console.error('Error loading speakers:', error); }
    });

          this.partnerService.getPartners().subscribe({
        next: (partners) => {
          this.partners = partners;
          this.dashboardStats.totalPartners = partners.length;
          console.log('Partners loaded:', this.partners);
        },
        error: (error) => { console.error('Error loading partners:', error); }
      });

      this.floorService.getFloors().subscribe({
        next: (floors) => {
          this.floors = floors;
          this.dashboardStats.totalFloors = floors.length;
          console.log('Floors loaded:', this.floors);
        },
        error: (error) => { console.error('Error loading floors:', error); }
      });

    // Load sponsors
    this.sponsorService.getSponsors().subscribe({
      next: (sponsors) => {
        this.sponsors = sponsors;
        this.dashboardStats.totalSponsors = sponsors.length;
        console.log('Sponsors loaded:', this.sponsors);
      },
      error: (error) => { console.error('Error loading sponsors:', error); }
    });
  }

  // Update method signatures to match HTML template
  toggleUserStatus(user: UserWithStatus): void {
    const newStatus = user.status === 'active' ? 'inactive' : 'active';
    
    if (this.attendees.includes(user)) {
      // This is an attendee
      const attendeeData = {
        name: user.name,
        email: user.email,
        phone: user['phone'] || '',
        password: user['password'] || '',
        status: newStatus
      };
      
      this.attendeeService.updateAttendee(user.id, attendeeData).subscribe({
        next: (response) => {
          user.status = newStatus;
          this.snackBar.open(`Attendee ${newStatus} successfully`, 'Close', { duration: 3000 });
        },
        error: (error) => {
          console.error('Error updating attendee status:', error);
          this.snackBar.open('Error updating attendee status', 'Close', { duration: 3000 });
        }
      });
    } else if (this.exhibitors.includes(user)) {
      // This is an exhibitor
      const exhibitorData = {
        exhibitorId: user['exhibitorId'] || user.id,
        name: user.name,
        contactPerson: user.contactPerson || '',
        email: user.email,
        boothNumber: user['boothNumber'] || '',
        productIds: user['productIds'] || '',
        logoUrl: user['logoUrl'] || '',
        status: newStatus
      };
      
      this.exhibitorService.updateExhibitor(user.id, exhibitorData).subscribe({
        next: (response) => {
          user.status = newStatus;
          this.snackBar.open(`Exhibitor ${newStatus} successfully`, 'Close', { duration: 3000 });
        },
        error: (error) => {
          console.error('Error updating exhibitor status:', error);
          this.snackBar.open('Error updating exhibitor status', 'Close', { duration: 3000 });
        }
      });
    }
  }

  deleteUser(user: UserWithStatus): void {
    if (this.attendees.includes(user)) {
      // This is an attendee
      this.attendeeService.deleteAttendee(user.id).subscribe({
        next: (response) => {
          this.attendees = this.attendees.filter(a => a.id !== user.id);
          this.snackBar.open('Attendee deleted successfully', 'Close', { duration: 3000 });
        },
        error: (error) => {
          console.error('Error deleting attendee:', error);
          this.snackBar.open('Error deleting attendee', 'Close', { duration: 3000 });
        }
      });
    } else if (this.exhibitors.includes(user)) {
      // This is an exhibitor
      this.exhibitorService.deleteExhibitor(user.id).subscribe({
        next: (response) => {
          this.exhibitors = this.exhibitors.filter(e => e.id !== user.id);
          this.snackBar.open('Exhibitor deleted successfully', 'Close', { duration: 3000 });
        },
        error: (error) => {
          console.error('Error deleting exhibitor:', error);
          this.snackBar.open('Error deleting exhibitor', 'Close', { duration: 3000 });
        }
      });
    }
  }

  editUser(user: UserWithStatus, type: 'attendee' | 'exhibitor'): void {
    if (type === 'attendee') {
      // Attendee editing is not allowed
      this.snackBar.open('Attendee editing is not available', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top'
      });
    } else {
      this.editExhibitor(user);
    }
  }

  editExhibitor(exhibitor: UserWithStatus): void {
    const dialogRef = this.dialog.open(EditExhibitorComponent, { 
      width: '600px',
      data: { exhibitor: exhibitor }
    });
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Exhibitor updated successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  addExhibitor(): void {
    // In a real app, you would open an add exhibitor dialog here
    this.snackBar.open('Add exhibitor functionality coming soon', 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  assignBooth(exhibitor: UserWithStatus): void {
    // In a real app, you would open a booth assignment dialog here
    this.snackBar.open('Booth assignment functionality coming soon', 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getStatusColor(status: string): string {
    return status === 'active' ? 'accent' : 'warn';
  }

  getFormattedDate(dateString: string): string {
    if (!dateString) return 'N/A';
    
    try {
      // Handle different date formats
      if (dateString.includes(',')) {
        // Handle array format like "2025,7,20,9,0"
        const parts = dateString.split(',').map(p => parseInt(p.trim()));
        if (parts.length >= 3) {
          const date = new Date(parts[0], parts[1] - 1, parts[2]); // month is 0-indexed
          return date.toLocaleDateString();
        }
      } else {
        // Handle standard date format
        const date = new Date(dateString);
        if (!isNaN(date.getTime())) {
          return date.toLocaleDateString();
        }
      }
    } catch (error) {
      console.error('Error parsing date:', dateString, error);
    }
    
    return dateString || 'N/A';
  }

  openAddExhibitorDialog() {
    const dialogRef = this.dialog.open(AddExhibitorComponent, {
      width: '400px'
    });
  
    dialogRef.afterClosed().subscribe((result: Exhibitor) => {
      if (result) {
        this.exhibitorService.addExhibitor(result).subscribe({
          next: () => {
            console.log('Exhibitor added, reloading data...');
            this.loadDashboardData(); // Reload exhibitors and stats after add
            this.snackBar.open('Exhibitor added successfully!', 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
          },
          error: (err) => {
            this.snackBar.open('Failed to add exhibitor: ' + (err.error?.message || err.message), 'Close', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
          }
        });
      }
    });
  }

  openAddSpeakerDialog() {
    const dialogRef = this.dialog.open(AddSpeakerComponent, { width: '600px' });
    dialogRef.afterClosed().subscribe((result: Speaker) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Speaker added successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  openAddPartnerDialog() {
    const dialogRef = this.dialog.open(AddPartnerComponent, { width: '600px' });
    dialogRef.afterClosed().subscribe((result: Partner) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Partner added successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  editPartner(partner: Partner) {
    const dialogRef = this.dialog.open(AddPartnerComponent, { 
      width: '600px',
      data: { partner: partner }
    });
    dialogRef.afterClosed().subscribe((result: Partner) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Partner updated successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  deletePartner(partner: Partner) {
    if (confirm(`Are you sure you want to delete partner "${partner.name}"?`)) {
      this.partnerService.deletePartner(partner.partnerId).subscribe({
        next: () => {
          this.loadDashboardData();
          this.snackBar.open('Partner deleted successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
        },
        error: (error) => {
          this.snackBar.open('Error deleting partner: ' + error.message, 'Close', { duration: 5000, horizontalPosition: 'center', verticalPosition: 'top' });
        }
      });
    }
  }

  openAddFloorDialog() {
    const dialogRef = this.dialog.open(AddFloorComponent, { width: '600px' });
    dialogRef.afterClosed().subscribe((result: Floor) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Floor added successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  editFloor(floor: Floor) {
    const dialogRef = this.dialog.open(AddFloorComponent, { 
      width: '600px',
      data: { floor: floor }
    });
    dialogRef.afterClosed().subscribe((result: Floor) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Floor updated successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  deleteFloor(floor: Floor) {
    if (confirm(`Are you sure you want to delete floor ${floor.floorNumber}?`)) {
      this.floorService.deleteFloor(floor.floorId).subscribe({
        next: () => {
          this.loadDashboardData();
          this.snackBar.open('Floor deleted successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
        },
        error: (error) => {
          this.snackBar.open('Error deleting floor: ' + error.message, 'Close', { duration: 5000, horizontalPosition: 'center', verticalPosition: 'top' });
        }
      });
    }
  }

  openAddSponsorDialog() {
    const dialogRef = this.dialog.open(AddSponsorComponent, { width: '600px' });
    dialogRef.afterClosed().subscribe((result: Sponsor) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Sponsor added successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  editSponsor(sponsor: Sponsor): void {
    const dialogRef = this.dialog.open(AddSponsorComponent, { 
      width: '600px',
      data: { sponsor: sponsor }
    });
    dialogRef.afterClosed().subscribe((result: Sponsor) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Sponsor updated successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  deleteSponsor(sponsor: Sponsor): void {
    if (confirm(`Are you sure you want to delete sponsor "${sponsor.name}"?`)) {
      this.sponsorService.deleteSponsor(sponsor.sponsorId).subscribe({
        next: () => {
          this.sponsors = this.sponsors.filter(s => s.sponsorId !== sponsor.sponsorId);
          this.dashboardStats.totalSponsors--;
          this.snackBar.open('Sponsor deleted successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
        },
        error: (error) => {
          this.snackBar.open('Error deleting sponsor: ' + error.message, 'Close', { duration: 5000, horizontalPosition: 'center', verticalPosition: 'top' });
        }
      });
    }
  }

  editSpeaker(speaker: Speaker): void {
    const dialogRef = this.dialog.open(AddSpeakerComponent, { 
      width: '600px',
      data: { speaker: speaker }
    });
    dialogRef.afterClosed().subscribe((result: Speaker) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Speaker updated successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  deleteSpeaker(speaker: Speaker): void {
    if (confirm(`Are you sure you want to delete speaker "${speaker.name}"?`)) {
      this.speakerService.deleteSpeaker(speaker.speakerId).subscribe({
        next: () => {
          this.speakers = this.speakers.filter(s => s.speakerId !== speaker.speakerId);
          this.dashboardStats.totalSpeakers--;
          this.snackBar.open('Speaker deleted successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
        },
        error: (error) => {
          this.snackBar.open('Error deleting speaker: ' + error.message, 'Close', { duration: 5000, horizontalPosition: 'center', verticalPosition: 'top' });
        }
      });
    }
  }

  editConference(conference: Conference): void {
    const dialogRef = this.dialog.open(AddConferenceComponent, { 
      width: '600px',
      data: { conference: conference }
    });
    dialogRef.afterClosed().subscribe((result: Conference) => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Conference updated successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
      }
    });
  }

  deleteConference(conference: Conference): void {
    if (confirm(`Are you sure you want to delete conference "${conference.title}"?`)) {
      this.conferenceService.deleteConference(conference.conferenceId).subscribe({
        next: () => {
          this.conferences = this.conferences.filter(c => c.conferenceId !== conference.conferenceId);
          this.dashboardStats.totalConferences--;
          this.snackBar.open('Conference deleted successfully!', 'Close', { duration: 3000, horizontalPosition: 'center', verticalPosition: 'top' });
        },
        error: (error) => {
          this.snackBar.open('Error deleting conference: ' + error.message, 'Close', { duration: 5000, horizontalPosition: 'center', verticalPosition: 'top' });
        }
      });
    }
  }
}