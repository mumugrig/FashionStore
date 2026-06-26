import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { Item, PageResponse, Review } from '@shared/models/models';

export interface ProductFilters {
  category?: string;
  search?: string;
  size?: string;
  color?: string;
  audience?: string;
  pricemin?: number;
  pricemax?: number;
}

@Injectable({ providedIn: 'root' })
export class ProductsService {
  private readonly api = inject(ApiService);

  getPage(page = 1, size = 20, filters: ProductFilters = {}): Observable<PageResponse<Item>> {
    return this.api.get<PageResponse<Item>>('/items', { ...filters, page, size });
  }

  getById(id: number): Observable<Item> {
    return this.api.get<Item>(`/items/${id}`);
  }

  getReviews(itemId: number, page = 1, size = 20): Observable<PageResponse<Review>> {
    return this.api.get<PageResponse<Review>>(`/items/${itemId}/reviews`, { page, size });
  }
}
