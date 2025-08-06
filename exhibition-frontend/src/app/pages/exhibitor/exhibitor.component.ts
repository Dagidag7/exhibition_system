import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';

// Services
import { ProductService } from '../../services/product.service';
import { ConferenceService } from '../../services/conference.service';
import { FloorService } from '../../services/floor.service';
import { AuthService, User } from '../../services/auth.service';

// Components
import { AddProductComponent } from '../../components/add-product/add-product.component';
import { ChangePasswordComponent } from '../../components/change-password/change-password.component';

// Interfaces
import { Product } from '../../services/product.service';
import { Conference } from '../../services/conference.service';
import { Floor } from '../../services/floor.service';

@Component({
  selector: 'app-exhibitor',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatChipsModule,
    MatDialogModule,
    MatTabsModule,
    MatProgressBarModule,
    MatListModule,
    MatDividerModule,
    MatBadgeModule,
    MatTooltipModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './exhibitor.component.html',
  styleUrls: ['./exhibitor.component.css']
})
export class ExhibitorComponent implements OnInit {
  // Dashboard Statistics
  dashboardStats = {
    totalProducts: 0,
    activeProducts: 0,
    totalConferences: 0,
    boothNumber: '',
    floorNumber: '',
    profileCompletion: 0
  };

  // Data Arrays
  products: Product[] = [];
  conferences: Conference[] = [];
  floorInfo: Floor | null = null;

  // Table configurations
  displayedColumnsProducts = ['productId', 'name', 'description', 'category', 'imageUrl', 'actions'];
  displayedColumnsConferences = ['conferenceId', 'title', 'description', 'date', 'time', 'location', 'speaker'];

  // Active tab
  activeTab = 'products';

  // Current exhibitor data from authentication
  currentExhibitor: any = null;

  constructor(
    private productService: ProductService,
    private conferenceService: ConferenceService,
    private floorService: FloorService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get current user from auth service
    const currentUser = this.authService.getCurrentUser();
    console.log('Current user from auth service:', currentUser);
    
    if (!currentUser || currentUser.role !== 'exhibitor') {
      console.log('User is not an exhibitor, redirecting...');
      this.router.navigate(['/login']);
      return;
    }

    // Set current exhibitor data
    this.currentExhibitor = {
      exhibitorId: currentUser.id,
      name: currentUser.name || 'Welcome to Exhibition Dashboard',
      contactPerson: currentUser.name || 'Exhibitor',
      email: currentUser.email,
      boothNumber: currentUser['boothNumber'] || currentUser['booth_number'] || 'A-15',
      floorNumber: currentUser['floorNumber'] || currentUser['floor_number'] || '1',
      logoUrl: currentUser['logoUrl'] || currentUser['logo_url'] || 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUwIiBoZWlnaHQ9IjgwIiB2aWV3Qm94PSIwIDAgMTUwIDgwIiBmaWxsPSJub25lIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPgo8cmVjdCB3aWR0aD0iMTUwIiBoZWlnaHQ9IjgwIiBmaWxsPSIjNjY3ZWVhIi8+Cjx0ZXh0IHg9Ijc1IiB5PSI0NSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjE0IiBmaWxsPSJ3aGl0ZSIgdGV4dC1hbmNob3I9Im1pZGRsZSI+RXhoaWJpdG9yPC90ZXh0Pgo8L3N2Zz4K',
      status: currentUser['status'] || 'active'
    };
    
    console.log('Current exhibitor data set:', this.currentExhibitor);
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    if (!this.currentExhibitor) return;

    // Load products for this exhibitor
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products = products.filter(p => p.exhibitorId === this.currentExhibitor.exhibitorId);
        this.dashboardStats.totalProducts = this.products.length;
        this.dashboardStats.activeProducts = this.products.filter(p => p.status === 'active').length;
        console.log('Products loaded:', this.products);
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.snackBar.open('Error loading products', 'Close', { duration: 3000 });
        // Set empty array to avoid template errors
        this.products = [];
      }
    });

    // Load conferences with fallback
    this.conferenceService.getConferences().subscribe({
      next: (conferences) => {
        console.log('Conferences loaded from API:', conferences);
        this.conferences = conferences;
        this.dashboardStats.totalConferences = conferences.length;
        if (this.conferences.length === 0) {
          console.log('No conferences from API, using mock data');
          this.conferences = [
            {
              conferenceId: 1,
              title: 'Future of Technology',
              description: 'Exploring emerging technologies and their impact on business',
              date: '2024-12-15',
              time: '10:00 AM',
              location: 'Main Hall A',
              speaker: 'Dr. Sarah Johnson'
            },
            {
              conferenceId: 2,
              title: 'Digital Transformation',
              description: 'How companies are adapting to the digital age',
              date: '2024-12-16',
              time: '2:00 PM',
              location: 'Conference Room B',
              speaker: 'Michael Chen'
            }
          ];
        }
      },
      error: (error) => {
        console.error('Error loading conferences:', error);
        // Add mock conferences
        this.conferences = [
          {
            conferenceId: 1,
            title: 'Future of Technology',
            description: 'Exploring emerging technologies and their impact on business',
            date: '2024-12-15',
            time: '10:00 AM',
            location: 'Main Hall A',
            speaker: 'Dr. Sarah Johnson'
          },
          {
            conferenceId: 2,
            title: 'Digital Transformation',
            description: 'How companies are adapting to the digital age',
            date: '2024-12-16',
            time: '2:00 PM',
            location: 'Conference Room B',
            speaker: 'Michael Chen'
          }
        ];
        console.log('Using mock conferences:', this.conferences);
      }
    });
    
    // Update dashboard stats
    this.dashboardStats.totalConferences = this.conferences.length;

    // Load floor information with fallback
    this.floorService.getFloors().subscribe({
      next: (floors) => {
        console.log('All floors from API:', floors);
        // Try to find floor by number, convert to string for comparison
        this.floorInfo = floors.find(f => f.floorNumber.toString() === this.currentExhibitor.floorNumber.toString()) || null;
        console.log('Floor info loaded:', this.floorInfo);
        
        // If no floor found, create mock floor info
        if (!this.floorInfo) {
          this.floorInfo = {
            floorId: 1,
            floorNumber: parseInt(this.currentExhibitor.floorNumber) || 1,
            layoutImage: 'https://via.placeholder.com/400x300/667eea/ffffff?text=Floor+Layout',
            exhibitorIds: '1,2,3,4,5',
            conferenceIds: '1,2,3'
          };
          console.log('Using fallback floor info:', this.floorInfo);
        }
      },
      error: (error) => {
        console.error('Error loading floor info:', error);
        // Add mock floor info
        this.floorInfo = {
          floorId: 1,
          floorNumber: parseInt(this.currentExhibitor.floorNumber) || 1,
          layoutImage: 'https://via.placeholder.com/400x300/667eea/ffffff?text=Floor+Layout',
          exhibitorIds: '1,2,3,4,5',
          conferenceIds: '1,2,3'
        };
        console.log('Using mock floor info:', this.floorInfo);
      }
    });

    // Set booth and floor info
    this.dashboardStats.boothNumber = this.currentExhibitor.boothNumber;
    this.dashboardStats.floorNumber = this.currentExhibitor.floorNumber;

    // Calculate profile completion
    this.calculateProfileCompletion();
  }

  calculateProfileCompletion(): void {
    const fields = [
      this.currentExhibitor.contactPerson,
      this.currentExhibitor.email,
      this.currentExhibitor.boothNumber,
      this.currentExhibitor.logoUrl
    ];
    
    const completedFields = fields.filter(field => field && field.trim() !== '').length;
    this.dashboardStats.profileCompletion = Math.round((completedFields / fields.length) * 100);
  }

  openAddProductDialog(): void {
    const dialogRef = this.dialog.open(AddProductComponent, {
      width: '600px',
      data: { exhibitorId: this.currentExhibitor.exhibitorId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Product added successfully', 'Close', { duration: 3000 });
      }
    });
  }

  editProduct(product: Product): void {
    const dialogRef = this.dialog.open(AddProductComponent, {
      width: '600px',
      data: { product, isEditMode: true }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadDashboardData();
        this.snackBar.open('Product updated successfully', 'Close', { duration: 3000 });
      }
    });
  }

  deleteProduct(product: Product): void {
    if (confirm(`Are you sure you want to delete "${product.name}"?`)) {
      this.productService.deleteProduct(product.productId).subscribe({
        next: () => {
          this.loadDashboardData();
          this.snackBar.open('Product deleted successfully', 'Close', { duration: 3000 });
        },
        error: (error) => {
          console.error('Error deleting product:', error);
          this.snackBar.open('Error deleting product', 'Close', { duration: 3000 });
        }
      });
    }
  }

  getStatusColor(status: string): string {
    return status === 'active' ? 'primary' : 'warn';
  }

  getFormattedDate(dateString: string): string {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString();
    } catch {
      return dateString;
    }
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  getExhibitorCount(): number {
    if (!this.floorInfo?.exhibitorIds) return 0;
    return this.floorInfo.exhibitorIds.split(',').filter(id => id.trim()).length;
  }

  getConferenceCount(): number {
    if (!this.floorInfo?.conferenceIds) return 0;
    return this.floorInfo.conferenceIds.split(',').filter(id => id.trim()).length;
  }

  openChangePasswordDialog(): void {
    const dialogRef = this.dialog.open(ChangePasswordComponent, {
      width: '450px',
      data: { exhibitorId: this.currentExhibitor.exhibitorId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.snackBar.open('Password changed successfully', 'Close', { duration: 3000 });
      }
    });
  }
} 