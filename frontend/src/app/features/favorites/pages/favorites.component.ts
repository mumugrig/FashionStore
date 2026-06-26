import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { map, switchMap } from 'rxjs';
import { Favorite, PageResponse } from '@shared/models/models';
import { LineItemComponent } from '@shared/ui/line-item/line-item.component';
import { VariantLookupService, VariantProductView } from '@features/catalog/services/variant-lookup.service';
import { FavoritesService } from '@features/favorites/services/favorites.service';

interface FavoriteRow {
  favorite: Favorite;
  product?: VariantProductView;
}

@Component({
  standalone: true,
  imports: [LineItemComponent, MatButtonModule],
  template: `
    <section class="page-heading">
      <h1>Saved</h1>
    </section>
    <table class="material-table">
      <thead><tr><th>Item</th><th></th></tr></thead>
      <tbody>
        @for (row of rows; track row.favorite.id) {
          <tr>
            <td>
              <app-line-item
                [imageUrl]="row.product?.itemImageUrl"
                [title]="row.product?.item?.name"
                [subtitle]="row.product?.label"
                [price]="row.product?.item?.price"
                fallbackSubtitle="Saved option" />
            </td>
            <td><button mat-flat-button class="danger" (click)="remove(row.favorite.id)">Remove</button></td>
          </tr>
        }
      </tbody>
    </table>
  `
})
export class FavoritesComponent implements OnInit {
  private readonly favorites = inject(FavoritesService);
  private readonly lookup = inject(VariantLookupService);
  page: PageResponse<Favorite> | null = null;
  rows: FavoriteRow[] = [];

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.favorites.getPage().pipe(
      switchMap((page) =>
        this.lookup.productByVariantIds(page.content.map((favorite) => favorite.itemVariantId)).pipe(
          map((products) => ({ page, products }))
        )
      )
    ).subscribe(({ page, products }) => {
      this.page = page;
      this.rows = page.content.map((favorite) => ({
          favorite,
          product: products.get(favorite.itemVariantId)
      }));
    });
  }

  remove(id: number): void {
    this.favorites.removeItem(id).subscribe(() => this.load());
  }
}
