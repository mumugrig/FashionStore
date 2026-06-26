import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, finalize, shareReplay, tap, throwError } from 'rxjs';
import { AuthResponse, User } from '@shared/models/models';

interface Session {
  accessToken: string;
  refreshToken: string;
  expiresAt: string;
  user: User;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'fashion-store-session';
  private readonly expiryBufferMs = 30_000;
  private readonly session = signal<Session | null>(this.readSession());
  private refreshRequest: Observable<AuthResponse> | null = null;

  readonly currentUser = computed(() => this.session()?.user ?? null);
  readonly isAuthenticated = computed(() => !!this.session()?.accessToken);
  readonly isAdmin = computed(() => this.roles().includes('ROLE_ADMIN'));

  constructor(private readonly http: HttpClient) {}

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', { email, password }).pipe(tap((res) => this.save(res)));
  }

  register(payload: Record<string, string>): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/register', payload).pipe(tap((res) => this.save(res)));
  }

  logout(): void {
    const refreshToken = this.session()?.refreshToken;
    if (refreshToken) {
      this.http.post('/api/auth/logout', { refreshToken }).subscribe({ error: () => undefined });
    }
    this.clearSession();
  }

  token(): string | null {
    return this.session()?.accessToken ?? null;
  }

  refreshToken(): string | null {
    return this.session()?.refreshToken ?? null;
  }

  hasRefreshToken(): boolean {
    return !!this.session()?.refreshToken;
  }

  isAccessTokenExpiredOrExpiringSoon(): boolean {
    const expiresAt = this.session()?.expiresAt;
    if (!expiresAt) {
      return true;
    }
    return new Date(expiresAt).getTime() - Date.now() <= this.expiryBufferMs;
  }

  refreshSession(): Observable<AuthResponse> {
    const refreshToken = this.refreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }
    if (!this.refreshRequest) {
      this.refreshRequest = this.http.post<AuthResponse>('/api/auth/refresh', { refreshToken }).pipe(
        tap((res) => this.save(res)),
        finalize(() => (this.refreshRequest = null)),
        shareReplay(1)
      );
    }
    return this.refreshRequest;
  }

  clearSession(reason?: string): void {
    localStorage.removeItem(this.storageKey);
    this.session.set(null);
    if (reason) {
      sessionStorage.setItem('fashion-store-auth-message', reason);
    }
  }

  private save(response: AuthResponse): void {
    const session: Session = {
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
      expiresAt: response.accessTokenExpiresAt,
      user: response.user
    };
    localStorage.setItem(this.storageKey, JSON.stringify(session));
    sessionStorage.removeItem('fashion-store-auth-message');
    this.session.set(session);
  }

  private readSession(): Session | null {
    const raw = localStorage.getItem(this.storageKey);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as Session;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }

  private roles(): string[] {
    const token = this.session()?.accessToken;
    if (!token) {
      return [];
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1] ?? '')) as { roles?: string[] };
      return payload.roles ?? [];
    } catch {
      return [];
    }
  }
}
