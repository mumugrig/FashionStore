import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '@core/api/api.service';
import { User } from '@shared/models/models';

export interface ProfileUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  currentPassword?: string;
  newPassword?: string;
}

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly api = inject(ApiService);

  getProfile(): Observable<User> {
    return this.api.get<User>('/users/me');
  }

  updateProfile(payload: ProfileUpdateRequest): Observable<User> {
    return this.api.patch<User>('/users/me', payload);
  }

  deleteProfile(): Observable<void> {
    return this.api.delete<void>('/users/me');
  }
}
