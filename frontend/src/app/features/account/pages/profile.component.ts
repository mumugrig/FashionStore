import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AccountService } from '@features/account/services/account.service';

@Component({
  standalone: true,
  imports: [MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule],
  template: `
    <mat-card class="auth-card">
      <mat-card-title>Profile</mat-card-title>
      <form [formGroup]="form" (ngSubmit)="save()">
        <mat-form-field appearance="outline"><mat-label>First name</mat-label><input matInput formControlName="firstName"></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Last name</mat-label><input matInput formControlName="lastName"></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Email</mat-label><input matInput type="email" formControlName="email"></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Phone</mat-label><input matInput formControlName="phoneNumber"></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Current or new password</mat-label><input matInput type="password" formControlName="password"></mat-form-field>
        @if (message) {
          <p>{{ message }}</p>
        }
        <button mat-flat-button type="submit" [disabled]="form.invalid">Save</button>
      </form>
    </mat-card>
  `
})
export class ProfileComponent implements OnInit {
  private readonly account = inject(AccountService);
  private readonly fb = inject(FormBuilder);
  message = '';
  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  ngOnInit(): void {
    this.account.getProfile().subscribe((user) => {
      this.form.patchValue({ ...user, password: '' });
    });
  }

  save(): void {
    this.account.updateProfile(this.form.getRawValue()).subscribe({
      next: () => (this.message = 'Profile updated.'),
      error: (err) => (this.message = err.error?.message ?? 'Could not update profile.')
    });
  }
}
