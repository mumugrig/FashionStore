import { Routes } from '@angular/router';
import { adminGuard, authGuard } from '@core/auth/guards';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'products' },
  {
    path: 'login',
    loadComponent: () => import('@features/auth/pages/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('@features/auth/pages/register.component').then((m) => m.RegisterComponent)
  },
  {
    path: 'products',
    canActivate: [authGuard],
    loadComponent: () =>
      import('@features/catalog/pages/product-list/product-list.component').then((m) => m.ProductListComponent)
  },
  {
    path: 'products/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('@features/catalog/pages/product-detail/product-detail.component').then((m) => m.ProductDetailComponent)
  },
  {
    path: 'cart',
    canActivate: [authGuard],
    loadComponent: () => import('@features/cart/pages/cart.component').then((m) => m.CartComponent)
  },
  {
    path: 'favorites',
    canActivate: [authGuard],
    loadComponent: () => import('@features/favorites/pages/favorites.component').then((m) => m.FavoritesComponent)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('@features/account/pages/profile.component').then((m) => m.ProfileComponent)
  },
  {
    path: 'admin/:resource/new',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('@features/admin/pages/admin-resource-form.component').then((m) => m.AdminResourceFormComponent)
  },
  {
    path: 'admin/:resource/:id/edit',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('@features/admin/pages/admin-resource-form.component').then((m) => m.AdminResourceFormComponent)
  },
  {
    path: 'admin/:resource',
    canActivate: [adminGuard],
    loadComponent: () => import('@features/admin/pages/admin-resource.component').then((m) => m.AdminResourceComponent)
  },
  { path: '**', redirectTo: 'products' }
];
