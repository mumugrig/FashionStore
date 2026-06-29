import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { Favorite, PageResponse } from '@shared/models/models';

export interface FavoriteFilters {
  search?: string;
}

@Injectable({ providedIn: 'root' })
export class FavoritesService {
  private readonly api = inject(ApiService);

  getPage(page = 1, size = 20, filters: FavoriteFilters = {}): Observable<PageResponse<Favorite>> {
    return this.api.get<PageResponse<Favorite>>('/favorites', { ...filters, page, size });
  }

  addItem(itemVariantId: number): Observable<Favorite> {
    return this.api.post<Favorite>('/favorites/items', { itemVariantId });
  }

  removeItem(id: number): Observable<void> {
    return this.api.delete(`/favorites/${id}`);
  }
}
