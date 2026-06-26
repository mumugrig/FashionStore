import { Component, Input, OnInit, inject } from '@angular/core';
import { map, switchMap } from 'rxjs';
import { ProductsService } from '@features/catalog/services/products.service';
import { VariantLookupService } from '@features/catalog/services/variant-lookup.service';
import { Item, Review } from '@shared/models/models';

interface ReviewView {
  review: Review;
  optionLabel: string;
}

@Component({
  standalone: true,
  template: `
    <h2>Reviews</h2>
    @if (loading) {
      <p>Loading reviews...</p>
    } @else if (reviews.length) {
      <div class="review-list">
        @for (entry of reviews; track entry.review.id) {
          <article class="review-item">
            <p>{{ entry.review.body }}</p>
            <div class="review-meta">
              <span>{{ entry.optionLabel }}</span>
              <span>{{ label(entry.review.sizeFit) }}</span>
              <span>{{ label(entry.review.quality) }}</span>
              <span>{{ label(entry.review.comfort) }}</span>
            </div>
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
  private readonly lookup = inject(VariantLookupService);
  @Input({ required: true }) item!: Item;
  reviews: ReviewView[] = [];
  loading = true;

  ngOnInit(): void {
    this.lookup.options(this.item.variants).pipe(
      switchMap((options) => {
        const optionByVariant = new Map(options.map((option) => [option.variant.id, option.label]));
        return this.products.getReviews(this.item.id, 1, 20).pipe(
          map((page) =>
            page.content.map((review) => ({
              review,
              optionLabel: optionByVariant.get(review.itemVariantId) ?? 'Product option'
            }))
          )
        );
      })
    ).subscribe({
      next: (reviews) => {
        this.reviews = reviews;
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
}
