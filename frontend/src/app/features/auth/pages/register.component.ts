import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '@core/auth/auth.service';

@Component({
  standalone: true,
  imports: [MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule, RouterLink],
  template: `
    <mat-card class="auth-card">
      <mat-card-title>Create Account</mat-card-title>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <mat-form-field appearance="outline"><mat-label>First name</mat-label><input matInput formControlName="firstName"></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Last name</mat-label><input matInput formControlName="lastName"></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Email</mat-label><input matInput type="email" formControlName="email"></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Phone</mat-label><input matInput formControlName="phoneNumber"></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Password</mat-label><input matInput type="password" formControlName="password"></mat-form-field>
        @if (error) {
          <p class="error">{{ error }}</p>
        }
        <button mat-flat-button type="submit" [disabled]="form.invalid || loading">Register</button>
      </form>
      <a mat-button routerLink="/login">Back to login</a>
    </mat-card>
  `
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  loading = false;
  error = '';
  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', [Validators.required, Validators.minLength(10)]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  submit(): void {
    this.loading = true;
    this.error = '';
    this.auth.register(this.form.getRawValue()).subscribe({
      next: () => this.router.navigateByUrl('/products'),
      error: (err) => {
        this.error = err.error?.message ?? 'Registration failed';
        this.loading = false;
      }
    });
  }
}
