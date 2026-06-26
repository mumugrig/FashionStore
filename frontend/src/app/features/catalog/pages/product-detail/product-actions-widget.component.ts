import { Component, Input, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { CartService } from '@features/cart/services/cart.service';
import { FavoritesService } from '@features/favorites/services/favorites.service';
import { VariantLookupService, VariantOption } from '@features/catalog/services/variant-lookup.service';
import { Item } from '@shared/models/models';

@Component({
  standalone: true,
  imports: [MatButtonModule],
  template: `
    <h2>Bag and saved items</h2>
    @if (options.length) {
      <div class="variant-grid">
        @for (option of options; track option.variant.id) {
          <button
            mat-stroked-button
            type="button"
            [class.selected]="selected?.variant?.id === option.variant.id"
            (click)="selected = option">
            @if (option.color?.value) {
              <span class="swatch" [style.background]="option.color?.value"></span>
            }
            {{ option.label }}
            <small>{{ option.variant.stockLeft }} left</small>
          </button>
        }
      </div>
    } @else {
      <p>No product options are currently available.</p>
    }
    <div class="inline-actions">
      <button mat-flat-button [disabled]="!selected" (click)="addCart()">Add to bag</button>
      <button mat-stroked-button [disabled]="!selected" (click)="addFavorite()">Save</button>
    </div>
    @if (message) {
      <p>{{ message }}</p>
    }
  `
})
export class ProductActionsWidgetComponent implements OnInit {
  private readonly cart = inject(CartService);
  private readonly favorites = inject(FavoritesService);
  private readonly lookup = inject(VariantLookupService);
  @Input({ required: true }) item!: Item;
  options: VariantOption[] = [];
  selected: VariantOption | null = null;
  message = '';

  ngOnInit(): void {
    this.lookup.options(this.item.variants).subscribe((options) => {
      this.options = options;
      this.selected = options[0] ?? null;
    });
  }

  addCart(): void {
    if (!this.selected) {
      return;
    }
    this.cart.addItem(this.selected.variant.id).subscribe({
      next: () => (this.message = 'Added to bag.'),
      error: (err) => (this.message = err.error?.message ?? 'Could not add to bag.')
    });
  }

  addFavorite(): void {
    if (!this.selected) {
      return;
    }
    this.favorites.addItem(this.selected.variant.id).subscribe({
      next: () => (this.message = 'Saved.'),
      error: (err) => (this.message = err.error?.message ?? 'Could not save item.')
    });
  }
}
