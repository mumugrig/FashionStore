import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '@core/auth/auth.service';

const authPaths = ['/api/auth/login', '/api/auth/register', '/api/auth/refresh'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!req.url.startsWith('/api') || authPaths.some((path) => req.url.startsWith(path))) {
    return next(req);
  }

  if (req.url.startsWith('/api/auth/logout')) {
    return next(withToken(req, auth.token()));
  }

  const send = (request: HttpRequest<unknown>) => next(withToken(request, auth.token())).pipe(
    catchError((error) => {
      if (!(error instanceof HttpErrorResponse) || error.status !== 401 || !auth.hasRefreshToken()) {
        return throwError(() => error);
      }
      return auth.refreshSession().pipe(
        catchError((refreshError) => {
          auth.clearSession('Your session expired. Please log in again.');
          router.navigate(['/login'], { queryParams: { reason: 'session-expired' } });
          return throwError(() => refreshError);
        }),
        switchMap(() => next(withToken(request, auth.token())).pipe(
          catchError((retryError) => {
            if (retryError instanceof HttpErrorResponse && retryError.status === 401) {
              auth.clearSession('Your session expired. Please log in again.');
              router.navigate(['/login'], { queryParams: { reason: 'session-expired' } });
            }
            return throwError(() => retryError);
          })
        ))
      );
    })
  );

  if (auth.hasRefreshToken() && auth.isAccessTokenExpiredOrExpiringSoon()) {
    return auth.refreshSession().pipe(
      catchError((error) => {
        auth.clearSession('Your session expired. Please log in again.');
        router.navigate(['/login'], { queryParams: { reason: 'session-expired' } });
        return throwError(() => error);
      }),
      switchMap(() => send(req))
    );
  }

  return send(req);
};

function withToken(req: HttpRequest<unknown>, token: string | null): HttpRequest<unknown> {
  if (!token) {
    return req;
  }
  return req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
}
