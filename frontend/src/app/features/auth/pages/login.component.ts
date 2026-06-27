import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
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
      <mat-card-title>Login</mat-card-title>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <mat-form-field appearance="outline">
          <mat-label>Email</mat-label>
          <input matInput type="email" formControlName="email">
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Password</mat-label>
          <input matInput type="password" formControlName="password">
        </mat-form-field>
        @if (error) {
          <p class="error">{{ error }}</p>
        }
        @if (notice) {
          <p class="notice">{{ notice }}</p>
        }
        <button mat-flat-button type="submit" [disabled]="form.invalid || loading">Login</button>
      </form>
      <p>Admin seed: admin@fashionstore.com / password</p>
      <a mat-button routerLink="/register">Create account</a>
    </mat-card>
  `
})
export class LoginComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  loading = false;
  error = '';
  notice = '';
  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  ngOnInit(): void {
    const reason = this.route.snapshot.queryParamMap.get('reason');
    const storedNotice = sessionStorage.getItem('fashion-store-auth-message');
    if (reason) {
      this.notice = storedNotice ?? (reason === 'account-deleted' ? 'Your account has been deleted.' : 'Your session expired. Please log in again.');
      sessionStorage.removeItem('fashion-store-auth-message');
    }
  }

  submit(): void {
    this.loading = true;
    this.error = '';
    this.notice = '';
    this.auth.login(this.form.controls.email.value, this.form.controls.password.value).subscribe({
      next: () => this.router.navigateByUrl('/products'),
      error: (err) => {
        this.error = err.error?.message ?? 'Login failed';
        this.loading = false;
      }
    });
  }
}
