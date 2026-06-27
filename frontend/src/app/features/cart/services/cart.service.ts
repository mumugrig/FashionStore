import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { CartItem, PageResponse } from '@shared/models/models';

export interface CartFilters {
  search?: string;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly api = inject(ApiService);

  getPage(page = 1, size = 20, filters: CartFilters = {}): Observable<PageResponse<CartItem>> {
    return this.api.get<PageResponse<CartItem>>('/cart', { ...filters, page, size });
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
