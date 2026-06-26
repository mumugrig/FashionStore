import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PageResponse } from '@shared/models/models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api';

  get<T>(path: string, params?: Record<string, string | number | boolean | null | undefined>): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}`, { params: this.toParams(params) });
  }

  getPage<T>(path: string, page = 1, size = 20): Observable<PageResponse<T>> {
    return this.get<PageResponse<T>>(path, { page, size });
  }

  post<T>(path: string, body: unknown): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, body);
  }

  put<T>(path: string, body: unknown): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${path}`, body);
  }

  patch<T>(path: string, body: unknown): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}${path}`, body);
  }

  delete(path: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}${path}`);
  }

  private toParams(params?: Record<string, string | number | boolean | null | undefined>): HttpParams {
    let result = new HttpParams();
    Object.entries(params ?? {}).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        result = result.set(key, String(value));
      }
    });
    return result;
  }
}
