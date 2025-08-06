import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { ProductService, Product } from '../../services/product.service';
import { ExhibitorService, Exhibitor } from '../../services/exhibitor.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatIconModule
  ],
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.css']
})
export class AddProductComponent implements OnInit {
  productForm: FormGroup;
  isEditMode: boolean = false;
  productId?: number;
  exhibitors: Exhibitor[] = [];

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private exhibitorService: ExhibitorService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<AddProductComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      exhibitorId: ['', [Validators.required]],
      category: ['', [Validators.maxLength(50)]],
      imageUrl: ['']
    });
  }

  ngOnInit(): void {
    this.loadExhibitors();
    
    if (this.data && this.data.product) {
      this.isEditMode = true;
      this.productId = this.data.product.productId;
      this.productForm.patchValue({
        name: this.data.product.name,
        description: this.data.product.description || '',
        exhibitorId: this.data.product.exhibitorId,
        category: this.data.product.category || '',
        imageUrl: this.data.product.imageUrl || ''
      });
    }
  }

  loadExhibitors(): void {
    this.exhibitorService.getExhibitors().subscribe({
      next: (exhibitors) => {
        this.exhibitors = exhibitors;
      },
      error: (error) => {
        console.error('Error loading exhibitors:', error);
        this.snackBar.open('Error loading exhibitors', 'Close', { duration: 3000 });
      }
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      const productData = this.productForm.value;
      
      if (this.isEditMode && this.productId) {
        this.productService.updateProduct(this.productId, { ...productData, productId: this.productId }).subscribe({
          next: (response) => {
            this.snackBar.open('Product updated successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(response);
          },
          error: (error) => {
            console.error('Error updating product:', error);
            this.snackBar.open('Error updating product', 'Close', { duration: 3000 });
          }
        });
      } else {
        this.productService.createProduct(productData).subscribe({
          next: (response) => {
            this.snackBar.open('Product created successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(response);
          },
          error: (error) => {
            console.error('Error creating product:', error);
            this.snackBar.open('Error creating product', 'Close', { duration: 3000 });
          }
        });
      }
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
} 