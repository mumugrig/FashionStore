import { Component, Input, OnInit, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { finalize } from 'rxjs';
import { AuthService } from '@core/auth/auth.service';
import { ProductsService } from '@features/catalog/services/products.service';
import { Item, Review } from '@shared/models/models';

@Component({
  standalone: true,
  imports: [MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, ReactiveFormsModule],
  template: `
    <h2>Reviews</h2>
    <form class="review-form" [formGroup]="reviewForm" (ngSubmit)="submitReview()">
      <mat-form-field appearance="outline">
        <mat-label>Size fit</mat-label>
        <mat-select formControlName="sizeFit">
          @for (value of sizeFits; track value) {
            <mat-option [value]="value">{{ label(value) }}</mat-option>
          }
        </mat-select>
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Quality</mat-label>
        <mat-select formControlName="quality">
          @for (value of qualities; track value) {
            <mat-option [value]="value">{{ label(value) }}</mat-option>
          }
        </mat-select>
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Comfort</mat-label>
        <mat-select formControlName="comfort">
          @for (value of comforts; track value) {
            <mat-option [value]="value">{{ label(value) }}</mat-option>
          }
        </mat-select>
      </mat-form-field>

      <mat-form-field appearance="outline" class="review-body-field">
        <mat-label>Your review</mat-label>
        <textarea matInput formControlName="body" rows="4"></textarea>
      </mat-form-field>

      <div class="inline-actions">
        <button mat-flat-button type="submit" [disabled]="reviewForm.invalid || submitting">
          {{ submitLabel }}
        </button>
        @if (editingReviewId) {
          <button mat-stroked-button type="button" (click)="cancelEdit()" [disabled]="submitting">Cancel</button>
        }
      </div>
    </form>
    @if (message) {
      <p>{{ message }}</p>
    }
    @if (loading) {
      <p>Loading reviews...</p>
    } @else if (reviews.length) {
      <div class="review-list">
        @for (entry of reviews; track entry.review.id) {
          <article class="review-item">
            <p>{{ entry.review.body }}</p>
            <div class="review-meta">
              <span>{{ label(entry.review.sizeFit) }}</span>
              <span>{{ label(entry.review.quality) }}</span>
              <span>{{ label(entry.review.comfort) }}</span>
            </div>
            @if (canManage(entry.review)) {
              <div class="inline-actions">
                <button mat-stroked-button type="button" (click)="startEdit(entry.review)" [disabled]="submitting || deletingReviewId === entry.review.id">
                  Edit
                </button>
                <button mat-stroked-button type="button" (click)="deleteReview(entry.review)" [disabled]="submitting || deletingReviewId === entry.review.id">
                  {{ deletingReviewId === entry.review.id ? 'Deleting...' : 'Delete' }}
                </button>
              </div>
            }
          </article>
        }
      </div>
    } @else {
      <p>No reviews yet.</p>
    }
  `
})
export class ProductReviewsWidgetComponent implements OnInit {
  private readonly products = inject(ProductsService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  @Input({ required: true }) item!: Item;
  readonly sizeFits = ['RUNS_SMALL', 'TRUE_TO_SIZE', 'RUNS_LARGE'];
  readonly qualities = ['POOR', 'AVERAGE', 'EXCELLENT'];
  readonly comforts = ['UNCOMFORTABLE', 'COMFORTABLE', 'VERY_COMFORTABLE'];
  readonly reviewForm = this.fb.nonNullable.group({
    sizeFit: ['TRUE_TO_SIZE', Validators.required],
    quality: ['EXCELLENT', Validators.required],
    comfort: ['VERY_COMFORTABLE', Validators.required],
    body: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(5000)]]
  });
  reviews: { review: Review }[] = [];
  loading = true;
  submitting = false;
  editingReviewId: number | null = null;
  deletingReviewId: number | null = null;
  message = '';

  ngOnInit(): void {
    this.loadReviews();
  }

  submitReview(): void {
    if (this.reviewForm.invalid || this.submitting) {
      this.reviewForm.markAllAsTouched();
      return;
    }
    this.submitting = true;
    this.message = '';
    const request = { ...this.reviewForm.getRawValue(), itemId: this.item.id };
    const saveRequest = this.editingReviewId
      ? this.products.updateReview(this.item.id, this.editingReviewId, request)
      : this.products.createReview(this.item.id, request);

    saveRequest.pipe(
      finalize(() => (this.submitting = false))
    ).subscribe({
      next: () => {
        const wasEditing = !!this.editingReviewId;
        this.resetForm();
        this.message = wasEditing ? 'Review updated.' : 'Review posted.';
        this.loadReviews();
      },
      error: (err) => {
        this.message = err.error?.message ?? 'Could not save review.';
      }
    });
  }

  get submitLabel(): string {
    if (this.submitting) {
      return this.editingReviewId ? 'Saving...' : 'Posting...';
    }
    return this.editingReviewId ? 'Save changes' : 'Post review';
  }

  canManage(review: Review): boolean {
    return this.auth.currentUser()?.id === review.userId;
  }

  startEdit(review: Review): void {
    this.editingReviewId = review.id;
    this.message = '';
    this.reviewForm.setValue({
      sizeFit: review.sizeFit,
      quality: review.quality,
      comfort: review.comfort,
      body: review.body
    });
  }

  cancelEdit(): void {
    this.resetForm();
    this.message = '';
  }

  deleteReview(review: Review): void {
    if (!this.canManage(review) || this.deletingReviewId) {
      return;
    }
    this.deletingReviewId = review.id;
    this.message = '';
    this.products.deleteReview(this.item.id, review.id).pipe(
      finalize(() => (this.deletingReviewId = null))
    ).subscribe({
      next: () => {
        if (this.editingReviewId === review.id) {
          this.resetForm();
        }
        this.message = 'Review deleted.';
        this.loadReviews();
      },
      error: (err) => {
        this.message = err.error?.message ?? 'Could not delete review.';
      }
    });
  }

  private loadReviews(): void {
    this.loading = true;
    this.products.getReviews(this.item.id, 1, 20).subscribe({
      next: (page) => {
        this.reviews = page.content.map((review) => ({ review }));
        this.loading = false;
      },
      error: () => {
        this.reviews = [];
        this.loading = false;
      }
    });
  }

  label(value: string): string {
    return value
      .toLowerCase()
      .split('_')
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }

  private resetForm(): void {
    this.editingReviewId = null;
    this.reviewForm.reset({
      sizeFit: 'TRUE_TO_SIZE',
      quality: 'EXCELLENT',
      comfort: 'VERY_COMFORTABLE',
      body: ''
    });
  }
}
