import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { Favorite, PageResponse } from '@shared/models/models';

@Injectable({ providedIn: 'root' })
export class FavoritesService {
  private readonly api = inject(ApiService);

  getPage(page = 1, size = 20): Observable<PageResponse<Favorite>> {
    return this.api.getPage<Favorite>('/favorites', page, size);
  }

  addItem(itemVariantId: number): Observable<Favorite> {
    return this.api.post<Favorite>('/favorites/items', { itemVariantId });
  }

  removeItem(id: number): Observable<void> {
    return this.api.delete(`/favorites/items/${id}`);
  }
}
