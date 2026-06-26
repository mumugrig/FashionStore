import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from '@core/auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [MatButtonModule, MatMenuModule, MatToolbarModule, RouterLink, RouterOutlet],
  template: `
    <mat-toolbar class="topbar">
      <a routerLink="/" class="brand">FASHION</a>
      <nav>
        <a mat-button routerLink="/products">COLLECTION</a>
        @if (auth.isAuthenticated()) {
          <a mat-button routerLink="/favorites">SAVED</a>
          <a mat-button routerLink="/cart">BAG</a>
          <button mat-button type="button" [matMenuTriggerFor]="profileMenu">PROFILE</button>
          <mat-menu #profileMenu="matMenu" xPosition="before">
            @if (auth.currentUser(); as user) {
              <div class="menu-identity">{{ user.firstName }} {{ user.lastName }}</div>
              <div class="menu-email">{{ user.email }}</div>
            }
            <a mat-menu-item routerLink="/profile">Account details</a>
            @if (auth.isAdmin()) {
              <a mat-menu-item routerLink="/admin/items">Admin panels</a>
            }
            <button mat-menu-item type="button" (click)="logout()">Logout</button>
          </mat-menu>
        } @else {
          <a mat-button routerLink="/login">LOGIN</a>
          <a mat-button routerLink="/register">REGISTER</a>
        }
      </nav>
    </mat-toolbar>
    <main class="shell">
      <router-outlet />
    </main>
  `
})
export class AppComponent {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
