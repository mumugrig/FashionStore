import { NgComponentOutlet } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { map, switchMap } from 'rxjs';
import { ProductsService } from '@features/catalog/services/products.service';
import { VariantLookupService } from '@features/catalog/services/variant-lookup.service';
import { Item } from '@shared/models/models';
import { ProductActionsWidgetComponent } from './product-actions-widget.component';
import { ProductReviewsWidgetComponent } from './product-reviews-widget.component';
import { ProductSummaryWidgetComponent } from './product-summary-widget.component';

@Component({
  standalone: true,
  imports: [MatCardModule, NgComponentOutlet],
  template: `
    @if (item) {
      @if (heroImage) {
        <div class="detail-hero" [style.background-image]="'url(' + heroImage + ')'"></div>
      }
      <section class="detail-layout">
        <mat-card class="panel">
          <ng-container *ngComponentOutlet="summaryWidget; inputs: { item: item }" />
        </mat-card>
        <mat-card class="panel">
          <ng-container *ngComponentOutlet="actionsWidget; inputs: { item: item, variantImageSelected: onVariantImageSelected }" />
        </mat-card>
        <mat-card class="panel">
          <ng-container *ngComponentOutlet="reviewsWidget; inputs: { item: item }" />
        </mat-card>
      </section>
    }
  `
})
export class ProductDetailComponent implements OnInit {
  private readonly products = inject(ProductsService);
  private readonly route = inject(ActivatedRoute);
  private readonly lookup = inject(VariantLookupService);
  item: Item | null = null;
  heroImage: string | null = null;
  readonly summaryWidget = ProductSummaryWidgetComponent;
  readonly actionsWidget = ProductActionsWidgetComponent;
  readonly reviewsWidget = ProductReviewsWidgetComponent;
  readonly onVariantImageSelected = (imageUrl: string | null): void => {
    this.heroImage = imageUrl;
  };

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.products.getById(id).pipe(
      switchMap((item) =>
        this.lookup.options(item.variants).pipe(
          map((options) => ({
            item,
            heroImage: item.imageUrl ?? options[0]?.imageUrl ?? null
          }))
        )
      )
    ).subscribe(({ item, heroImage }) => {
      this.item = item;
      this.heroImage = heroImage;
    });
  }
}
