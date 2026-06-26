import { Injectable, inject } from '@angular/core';
import { forkJoin, map, Observable, shareReplay, switchMap } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { Color, Item, ItemVariant, Size } from '@shared/models/models';

export interface VariantOption {
  variant: ItemVariant;
  color?: Color;
  size?: Size;
  label: string;
  imageUrl: string | null;
}

export interface VariantProductView extends VariantOption {
  item: Item;
  itemImageUrl: string | null;
}

@Injectable({ providedIn: 'root' })
export class VariantLookupService {
  private readonly api = inject(ApiService);
  private readonly colors$ = this.api.getPage<Color>('/colors', 1, 100).pipe(
    map((page) => new Map(page.content.map((color) => [color.id, color]))),
    shareReplay(1)
  );
  private readonly sizes$ = this.api.getPage<Size>('/sizes', 1, 100).pipe(
    map((page) => new Map(page.content.map((size) => [size.id, size]))),
    shareReplay(1)
  );
  private readonly items$ = this.api.getPage<Item>('/items', 1, 100).pipe(
    map((page) => page.content),
    shareReplay(1)
  );
  private readonly variantProducts$ = this.items$.pipe(
    switchMap((items) =>
      this.lookupData().pipe(map(({ colors, sizes }) => this.mapVariantProducts(items, colors, sizes)))
    ),
    shareReplay(1)
  );

  options(variants: ItemVariant[] = []): Observable<VariantOption[]> {
    return this.lookupData().pipe(
      map(({ colors, sizes }) =>
        variants
          .filter((variant) => variant.active ?? variant.isActive ?? true)
          .map((variant) => this.toOption(variant, colors, sizes))
      )
    );
  }

  productByVariantIds(variantIds: number[]): Observable<Map<number, VariantProductView>> {
    return this.variantProducts$.pipe(
      map((allVariants) => {
        const result = new Map<number, VariantProductView>();
        variantIds.forEach((id) => {
          const view = allVariants.get(id);
          if (view) {
            result.set(id, view);
          }
        });
        return result;
      })
    );
  }

  private lookupData(): Observable<{ colors: Map<number, Color>; sizes: Map<number, Size> }> {
    return forkJoin({ colors: this.colors$, sizes: this.sizes$ });
  }

  private mapVariantProducts(
    items: Item[],
    colors: Map<number, Color>,
    sizes: Map<number, Size>
  ): Map<number, VariantProductView> {
    const result = new Map<number, VariantProductView>();
    items.forEach((item) => {
      const itemImageUrl = this.itemDisplayImage(item, colors);
      item.variants.forEach((variant) => {
        result.set(variant.id, {
          ...this.toOption(variant, colors, sizes),
          item,
          itemImageUrl
        });
      });
    });
    return result;
  }

  private toOption(
    variant: ItemVariant,
    colors: Map<number, Color>,
    sizes: Map<number, Size>
  ): VariantOption {
    const color = colors.get(variant.colorId);
    const size = sizes.get(variant.sizeId);
    const label = [color?.name, size?.label].filter(Boolean).join(' / ');
    return {
      variant,
      color,
      size,
      label: label || 'Product option',
      imageUrl: variant.imageUrl ?? color?.imageUrl ?? null
    };
  }

  private itemDisplayImage(item: Item, colors: Map<number, Color>): string | null {
    return item.imageUrl
      ?? item.variants
        .map((variant) => variant.imageUrl ?? colors.get(variant.colorId)?.imageUrl ?? null)
        .find((imageUrl): imageUrl is string => !!imageUrl)
      ?? null;
  }
}
