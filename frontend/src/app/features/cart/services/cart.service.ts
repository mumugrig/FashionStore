import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { CartItem, PageResponse } from '@shared/models/models';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly api = inject(ApiService);

  getPage(page = 1, size = 20): Observable<PageResponse<CartItem>> {
    return this.api.getPage<CartItem>('/cart', page, size);
  }

  addItem(itemVariantId: number, quantity = 1): Observable<CartItem> {
    return this.api.post<CartItem>('/cart/items', { itemVariantId, quantity });
  }

  updateItem(item: CartItem): Observable<CartItem> {
    return this.api.put<CartItem>(`/cart/items/${item.id}`, {
      quantity: item.quantity,
      itemVariantId: item.itemVariantId
    });
  }

  removeItem(id: number): Observable<void> {
    return this.api.delete(`/cart/items/${id}`);
  }
}
