import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { map, switchMap } from 'rxjs';
import { CartItem, PageResponse } from '@shared/models/models';
import { LineItemComponent } from '@shared/ui/line-item/line-item.component';
import { VariantLookupService, VariantProductView } from '@features/catalog/services/variant-lookup.service';
import { CartService } from '@features/cart/services/cart.service';

interface CartRow {
  cartItem: CartItem;
  product?: VariantProductView;
}

@Component({
  standalone: true,
  imports: [FormsModule, LineItemComponent, MatButtonModule, MatInputModule],
  template: `
    <section class="page-heading">
      <h1>Bag</h1>
    </section>
    <table class="material-table">
      <thead><tr><th>Item</th><th>Quantity</th><th></th></tr></thead>
      <tbody>
        @for (row of rows; track row.cartItem.id) {
          <tr>
            <td>
              <app-line-item
                [imageUrl]="row.product?.imageUrl ?? row.product?.itemImageUrl"
                [title]="row.product?.item?.name"
                [subtitle]="row.product?.label"
                [price]="row.product?.item?.price"
                fallbackSubtitle="Selected option" />
            </td>
            <td><input matInput type="number" min="1" max="999" [(ngModel)]="row.cartItem.quantity"></td>
            <td>
              <button mat-stroked-button (click)="update(row.cartItem)">Save</button>
              <button mat-flat-button class="danger" (click)="remove(row.cartItem.id)">Remove</button>
            </td>
          </tr>
        }
      </tbody>
    </table>
  `
})
export class CartComponent implements OnInit {
  private readonly cart = inject(CartService);
  private readonly lookup = inject(VariantLookupService);
  page: PageResponse<CartItem> | null = null;
  rows: CartRow[] = [];

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.cart.getPage().pipe(
      switchMap((page) =>
        this.lookup.productByVariantIds(page.content.map((item) => item.itemVariantId)).pipe(
          map((products) => ({ page, products }))
        )
      )
    ).subscribe(({ page, products }) => {
      this.page = page;
      this.rows = page.content.map((cartItem) => ({
          cartItem,
          product: products.get(cartItem.itemVariantId)
      }));
    });
  }

  update(item: CartItem): void {
    this.cart.updateItem(item).subscribe(() => this.load());
  }

  remove(id: number): void {
    this.cart.removeItem(id).subscribe(() => this.load());
  }
}
