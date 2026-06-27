import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '@core/auth/auth.service';
import { AccountService } from '@features/account/services/account.service';
import { User } from '@shared/models/models';

@Component({
  standalone: true,
  imports: [MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule],
  template: `
    <mat-card class="auth-card">
      <mat-card-title>Profile</mat-card-title>
      @if (!editing) {
        <dl class="profile-details">
          <div>
            <dt>First name</dt>
            <dd>{{ user?.firstName }}</dd>
          </div>
          <div>
            <dt>Last name</dt>
            <dd>{{ user?.lastName }}</dd>
          </div>
          <div>
            <dt>Email</dt>
            <dd>{{ user?.email }}</dd>
          </div>
          <div>
            <dt>Phone</dt>
            <dd>{{ user?.phoneNumber }}</dd>
          </div>
        </dl>
        @if (message) {
          <p class="notice">{{ message }}</p>
        }
        <div class="inline-actions">
          <button mat-flat-button type="button" (click)="startEdit()" [disabled]="!user || deleting">Edit</button>
          <button class="danger" mat-flat-button type="button" (click)="deleteAccount()" [disabled]="!user || deleting">Delete account</button>
        </div>
      } @else {
        <form [formGroup]="form" (ngSubmit)="save()">
          <mat-form-field appearance="outline"><mat-label>First name</mat-label><input matInput formControlName="firstName"></mat-form-field>
          <mat-form-field appearance="outline"><mat-label>Last name</mat-label><input matInput formControlName="lastName"></mat-form-field>
          <mat-form-field appearance="outline"><mat-label>Email</mat-label><input matInput type="email" formControlName="email"></mat-form-field>
          <mat-form-field appearance="outline"><mat-label>Phone</mat-label><input matInput formControlName="phoneNumber"></mat-form-field>
          <mat-form-field appearance="outline"><mat-label>Current password</mat-label><input matInput type="password" formControlName="currentPassword"></mat-form-field>
          <mat-form-field appearance="outline"><mat-label>New password</mat-label><input matInput type="password" formControlName="newPassword"></mat-form-field>
          @if (message) {
            <p class="error">{{ message }}</p>
          }
          <div class="inline-actions">
            <button mat-flat-button type="submit" [disabled]="form.invalid || saving">Save</button>
            <button mat-button type="button" (click)="cancelEdit()" [disabled]="saving">Cancel</button>
          </div>
        </form>
      }
    </mat-card>
  `
})
export class ProfileComponent implements OnInit {
  private readonly account = inject(AccountService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  user: User | null = null;
  editing = false;
  saving = false;
  deleting = false;
  message = '';
  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', [Validators.required, Validators.minLength(10)]],
    currentPassword: ['', Validators.minLength(6)],
    newPassword: ['', Validators.minLength(6)]
  });

  ngOnInit(): void {
    this.account.getProfile().subscribe((user) => {
      this.user = user;
      this.patchForm(user);
    });
  }

  startEdit(): void {
    if (!this.user) {
      return;
    }
    this.message = '';
    this.patchForm(this.user);
    this.editing = true;
  }

  cancelEdit(): void {
    if (this.user) {
      this.patchForm(this.user);
    }
    this.message = '';
    this.editing = false;
  }

  save(): void {
    const values = this.form.getRawValue();
    if (values.newPassword && !values.currentPassword) {
      this.message = 'Enter your current password before setting a new one.';
      return;
    }

    this.saving = true;
    this.message = '';
    this.account.updateProfile({
      firstName: values.firstName,
      lastName: values.lastName,
      email: values.email,
      phoneNumber: values.phoneNumber,
      currentPassword: values.currentPassword || undefined,
      newPassword: values.newPassword || undefined
    }).subscribe({
      next: (user) => {
        this.user = user;
        this.patchForm(user);
        this.message = 'Profile updated.';
        this.editing = false;
        this.saving = false;
      },
      error: (err) => {
        this.message = err.error?.message ?? 'Could not update profile.';
        this.saving = false;
      }
    });
  }

  deleteAccount(): void {
    if (!this.user || !confirm('Delete your account permanently? This cannot be undone.')) {
      return;
    }

    this.deleting = true;
    this.message = '';
    this.account.deleteProfile().subscribe({
      next: () => {
        this.auth.clearSession('Your account has been deleted.');
        this.router.navigateByUrl('/login?reason=account-deleted');
      },
      error: (err) => {
        this.message = err.error?.message ?? 'Could not delete account.';
        this.deleting = false;
      }
    });
  }

  private patchForm(user: User): void {
    this.form.patchValue({
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      phoneNumber: user.phoneNumber,
      currentPassword: '',
      newPassword: ''
    });
  }
}
