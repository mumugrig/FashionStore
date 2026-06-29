import { Injectable, inject } from '@angular/core';
import { map, shareReplay } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { Category, Color, Size } from '@shared/models/models';

@Injectable({ providedIn: 'root' })
export class CatalogFilterOptionsService {
  private readonly api = inject(ApiService);

  readonly categories$ = this.api.getPage<Category>('/categories', 1, 100).pipe(
    map((page) => page.content),
    shareReplay(1)
  );

  readonly colors$ = this.api.getPage<Color>('/colors', 1, 100).pipe(
    map((page) => page.content),
    shareReplay(1)
  );

  readonly sizes$ = this.api.getPage<Size>('/sizes', 1, 100).pipe(
    map((page) => page.content),
    shareReplay(1)
  );
}
