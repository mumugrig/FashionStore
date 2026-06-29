import { Component, HostListener, OnInit, inject } from '@angular/core';
import { AsyncPipe, CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { forkJoin, map, of, switchMap, tap } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { Item, PageResponse } from '@shared/models/models';
import { ProductFilters, ProductsService } from '@features/catalog/services/products.service';
import { CatalogFilterOptionsService } from '@features/catalog/services/catalog-filter-options.service';
import { VariantLookupService, VariantOption } from '@features/catalog/services/variant-lookup.service';

interface ProductCardView {
  item: Item;
  imageUrl: string | null;
}

@Component({
  standalone: true,
  imports: [
    CurrencyPipe,
    AsyncPipe,
    MatButtonModule,
    MatCardModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    RouterLink
  ],
  template: `
    <section class="catalog-hero">
      <h1>SALE</h1>
      <mat-expansion-panel class="catalog-filter-panel">
        <mat-expansion-panel-header>
          <mat-panel-title>Search catalog</mat-panel-title>
          @if (activeFilterCount) {
            <mat-panel-description>{{ activeFilterCount }} active</mat-panel-description>
          }
        </mat-expansion-panel-header>
        <form class="catalog-filter-form" [formGroup]="filtersForm" (ngSubmit)="applyFilters()">
          <mat-form-field appearance="outline">
            <mat-label>Search catalog</mat-label>
            <input matInput formControlName="search" type="search">
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Category</mat-label>
            <mat-select formControlName="category">
              <mat-option value="">Any category</mat-option>
              @for (category of categories$ | async; track category.id) {
                <mat-option [value]="category.name">{{ category.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Size</mat-label>
            <mat-select formControlName="itemSize">
              <mat-option value="">Any size</mat-option>
              @for (size of sizes$ | async; track size.id) {
                <mat-option [value]="size.label">{{ size.label }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Colour</mat-label>
            <mat-select formControlName="color">
              <mat-option value="">Any colour</mat-option>
              @for (color of colors$ | async; track color.id) {
                <mat-option [value]="color.name">{{ color.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Audience</mat-label>
            <mat-select formControlName="audience">
              <mat-option value="">Any audience</mat-option>
              @for (audience of audiences; track audience) {
                <mat-option [value]="audience">{{ audience }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Min price</mat-label>
            <input matInput formControlName="pricemin" min="0" type="number">
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Max price</mat-label>
            <input matInput formControlName="pricemax" min="0" type="number">
          </mat-form-field>

          <div class="catalog-filter-actions">
            <button mat-flat-button type="submit">Apply</button>
            <button mat-stroked-button type="button" (click)="clearFilters()">Clear</button>
          </div>
        </form>
      </mat-expansion-panel>
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
  private readonly options = inject(CatalogFilterOptionsService);
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  readonly audiences = ['MEN', 'WOMEN', 'KIDS', 'UNISEX'];
  readonly categories$ = this.options.categories$;
  readonly colors$ = this.options.colors$;
  readonly sizes$ = this.options.sizes$;
  readonly filtersForm = this.fb.nonNullable.group({
    search: [''],
    category: [''],
    itemSize: [''],
    color: [''],
    audience: [''],
    pricemin: [''],
    pricemax: ['']
  });
  page: PageResponse<Item> | null = null;
  cards: ProductCardView[] = [];
  loading = false;
  error = '';
  activeFilterCount = 0;
  private activeFilters: ProductFilters = {};
  private nextPage = 1;
  private readonly pageSize = 8;

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const formValue = {
        search: params.get('search') ?? '',
        category: params.get('category') ?? '',
        itemSize: params.get('itemSize') ?? '',
        color: params.get('color') ?? '',
        audience: params.get('audience') ?? '',
        pricemin: params.get('pricemin') ?? '',
        pricemax: params.get('pricemax') ?? ''
      };
      this.filtersForm.setValue(formValue, { emitEvent: false });
      this.activeFilters = this.toProductFilters(formValue);
      this.activeFilterCount = Object.keys(this.activeFilters).length;
      this.resetCatalog();
      this.loadNext();
    });
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
    this.products.getPage(this.nextPage, this.pageSize, this.activeFilters).pipe(
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

  applyFilters(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: this.toQueryParams(this.filtersForm.getRawValue())
    });
  }

  clearFilters(): void {
    this.filtersForm.reset({
      search: '',
      category: '',
      itemSize: '',
      color: '',
      audience: '',
      pricemin: '',
      pricemax: ''
    });
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {}
    });
  }

  private cardsForPage(page: PageResponse<Item>) {
    const displayItems = page.content.filter((item) =>
      item.variants.some((variant) => variant.active ?? variant.isActive ?? true)
    );
    if (!displayItems.length) {
      return of([] as ProductCardView[]);
    }
    const optionRequests = displayItems.map((item) =>
      this.variants.options(item.variants).pipe(catchError(() => of([] as VariantOption[])))
    );
    return forkJoin(optionRequests).pipe(
      map((optionsByItem) =>
        displayItems.map((item, index) => ({
          item,
          imageUrl: item.imageUrl ?? optionsByItem[index]?.[0]?.imageUrl ?? null
        }))
      )
    );
  }

  private resetCatalog(): void {
    this.page = null;
    this.cards = [];
    this.error = '';
    this.nextPage = 1;
  }

  private toQueryParams(value: ReturnType<typeof this.filtersForm.getRawValue>): Params {
    return this.cleanFilters(value);
  }

  private toProductFilters(value: ReturnType<typeof this.filtersForm.getRawValue>): ProductFilters {
    return this.cleanFilters(value);
  }

  private cleanFilters(value: ReturnType<typeof this.filtersForm.getRawValue>): ProductFilters {
    const filters: ProductFilters = {};
    const search = this.trimValue(value.search);
    const category = this.trimValue(value.category);
    const itemSize = this.trimValue(value.itemSize);
    const color = this.trimValue(value.color);
    const audience = this.trimValue(value.audience);
    const pricemin = this.toPositiveNumber(value.pricemin);
    const pricemax = this.toPositiveNumber(value.pricemax);

    if (search) {
      filters.search = search;
    }
    if (category) {
      filters.category = category;
    }
    if (itemSize) {
      filters.itemSize = itemSize;
    }
    if (color) {
      filters.color = color;
    }
    if (audience) {
      filters.audience = audience;
    }
    if (pricemin !== null) {
      filters.pricemin = pricemin;
    }
    if (pricemax !== null) {
      filters.pricemax = pricemax;
    }
    return filters;
  }

  private trimValue(value: unknown): string {
    return String(value ?? '').trim();
  }

  private toPositiveNumber(value: unknown): number | null {
    const normalized = this.trimValue(value);
    if (!normalized) {
      return null;
    }
    const parsed = Number(normalized);
    return Number.isFinite(parsed) && parsed >= 0 ? parsed : null;
  }
}
