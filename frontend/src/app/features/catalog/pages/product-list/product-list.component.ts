import { Component, HostListener, OnInit, inject } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { forkJoin, map, of, switchMap, tap } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { Item, PageResponse } from '@shared/models/models';
import { ProductsService } from '@features/catalog/services/products.service';
import { VariantLookupService, VariantOption } from '@features/catalog/services/variant-lookup.service';

interface ProductCardView {
  item: Item;
  imageUrl: string | null;
}

@Component({
  standalone: true,
  imports: [CurrencyPipe, MatButtonModule, MatCardModule, RouterLink],
  template: `
    <section class="catalog-hero">
      <h1>SALE</h1>
      <div class="catalog-controls">
        <button mat-stroked-button>SIZE</button>
        <button mat-stroked-button>PRICE</button>
        <button mat-stroked-button>COLOUR</button>
      </div>
    </section>
    <section class="product-grid">
      @for (entry of cards; track entry.item.id) {
        <mat-card class="product-card" [routerLink]="['/products', entry.item.id]" tabindex="0">
          <div class="product-media" [style.background-image]="entry.imageUrl ? 'url(' + entry.imageUrl + ')' : null">
            @if (!entry.imageUrl) {
              <span>{{ entry.item.audience }}</span>
            }
          </div>
          <mat-card-title>{{ entry.item.name }}</mat-card-title>
          <mat-card-content>
            <p>{{ entry.item.description }}</p>
            <strong>{{ entry.item.price | currency }}</strong>
          </mat-card-content>
          <mat-card-actions>
            <span>View details</span>
          </mat-card-actions>
        </mat-card>
      } @empty {
        @if (!loading) {
          <p class="catalog-empty">{{ error || 'No products available.' }}</p>
        }
      }
    </section>
    @if (loading) {
      <div class="pager">Loading more pieces...</div>
    } @else if (page?.last && cards.length) {
      <div class="pager">End of collection</div>
    }
  `
})
export class ProductListComponent implements OnInit {
  private readonly products = inject(ProductsService);
  private readonly variants = inject(VariantLookupService);
  page: PageResponse<Item> | null = null;
  cards: ProductCardView[] = [];
  loading = false;
  error = '';
  private nextPage = 1;
  private readonly pageSize = 8;

  ngOnInit(): void {
    this.loadNext();
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    const threshold = 500;
    const position = window.innerHeight + window.scrollY;
    const height = document.documentElement.scrollHeight;
    if (height - position < threshold) {
      this.loadNext();
    }
  }

  loadNext(): void {
    if (this.loading || this.page?.last) {
      return;
    }
    this.loading = true;
    this.error = '';
    this.products.getPage(this.nextPage, this.pageSize).pipe(
      tap((res) => {
        this.page = res;
        this.nextPage = res.page + 1;
      }),
      switchMap((res) => this.cardsForPage(res)),
      finalize(() => (this.loading = false))
    ).subscribe({
      next: (cards) => {
        this.cards = [...this.cards, ...cards];
      },
      error: () => {
        this.error = 'Could not load products. Check that the backend is running and that you are logged in.';
      }
    });
  }

  private cardsForPage(page: PageResponse<Item>) {
    if (!page.content.length) {
      return of([] as ProductCardView[]);
    }
    const optionRequests = page.content.map((item) =>
      this.variants.options(item.variants).pipe(catchError(() => of([] as VariantOption[])))
    );
    return forkJoin(optionRequests).pipe(
      map((optionsByItem) =>
        page.content.map((item, index) => ({
          item,
          imageUrl: item.imageUrl ?? optionsByItem[index]?.[0]?.imageUrl ?? null
        }))
      )
    );
  }
}
