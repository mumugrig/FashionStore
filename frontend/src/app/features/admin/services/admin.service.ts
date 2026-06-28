import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { PageResponse } from '@shared/models/models';
import { AdminResourceConfig } from './admin-resource.config';

export type AdminRow = Record<string, unknown> & { id: number };

export interface AdminListFilters {
  search?: string;
  filterColumn?: string;
  filterValue?: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly api = inject(ApiService);

  list(config: AdminResourceConfig, page = 1, size = 20, filters: AdminListFilters = {}): Observable<PageResponse<AdminRow> | AdminRow[]> {
    if (config.paged) {
      return this.api.get<PageResponse<AdminRow>>(config.endpoint, { ...filters, page, size });
    }
    return this.api.get<AdminRow[]>(config.endpoint);
  }

  get(config: AdminResourceConfig, id: number): Observable<AdminRow> {
    return this.api.get<AdminRow>(config.getPath?.(id) ?? `${config.endpoint}/${id}`);
  }

  save(config: AdminResourceConfig, row: AdminRow, body: Record<string, unknown>): Observable<AdminRow> {
    if (!row.id) {
      return this.api.post<AdminRow>(config.endpoint, body);
    }
    const path = config.updatePath?.(row.id) ?? `${config.endpoint}/${row.id}`;
    return config.updateMethod === 'patch'
      ? this.api.patch<AdminRow>(path, body)
      : this.api.put<AdminRow>(path, body);
  }

  remove(config: AdminResourceConfig, id: number): Observable<void> {
    const path = config.deletePath?.(id) ?? `${config.endpoint}/${id}`;
    return this.api.delete(path);
  }

  removeMany(config: AdminResourceConfig, ids: number[]): Observable<void> {
    return this.api.post<void>(config.bulkDeletePath ?? `${config.endpoint}/bulk-delete`, { ids });
  }
}
