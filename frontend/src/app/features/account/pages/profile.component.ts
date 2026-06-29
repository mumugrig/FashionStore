import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '@core/auth/auth.service';
import { AccountService } from '@features/account/services/account.service';
import { Address, AddressRequest, PageResponse, User } from '@shared/models/models';

@Component({
  standalone: true,
  imports: [MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule],
  template: `
    <section class="profile-layout">
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

      <mat-card class="auth-card">
        <mat-card-title>Address book</mat-card-title>
        @if (addressMessage) {
          <p [class.error]="addressError" [class.notice]="!addressError">{{ addressMessage }}</p>
        }

        @if (!addressEditing) {
          <div class="inline-actions">
            <button mat-flat-button type="button" (click)="startAddressCreate()">Add address</button>
          </div>
        } @else {
          <form [formGroup]="addressForm" (ngSubmit)="saveAddress()">
            <mat-form-field appearance="outline"><mat-label>Country</mat-label><input matInput formControlName="country"></mat-form-field>
            <mat-form-field appearance="outline"><mat-label>Region</mat-label><input matInput formControlName="region"></mat-form-field>
            <mat-form-field appearance="outline"><mat-label>City</mat-label><input matInput formControlName="city"></mat-form-field>
            <mat-form-field appearance="outline"><mat-label>Postal code</mat-label><input matInput type="number" formControlName="postalCode"></mat-form-field>
            <mat-form-field appearance="outline"><mat-label>Address line</mat-label><input matInput formControlName="addressLine"></mat-form-field>
            <div class="inline-actions">
              <button mat-flat-button type="submit" [disabled]="addressForm.invalid || addressSaving">
                {{ addressSaving ? 'Saving' : 'Save address' }}
              </button>
              <button mat-button type="button" (click)="cancelAddressEdit()" [disabled]="addressSaving">Cancel</button>
            </div>
          </form>
        }

        <div class="address-list">
          @for (address of addresses; track address.id) {
            <article class="address-card">
              <strong>{{ address.addressLine }}</strong>
              <span>{{ address.city }}, {{ address.region }} {{ address.postalCode }}</span>
              <span>{{ address.country }}</span>
              <div class="inline-actions">
                <button mat-stroked-button type="button" (click)="startAddressEdit(address)" [disabled]="addressSaving">Edit</button>
                <button class="danger" mat-flat-button type="button" (click)="removeAddress(address)" [disabled]="addressSaving">Remove</button>
              </div>
            </article>
          } @empty {
            <p class="notice">No saved addresses.</p>
          }
        </div>

        @if (addressPage && addressPage.totalPages > 1) {
          <div class="pager">
            <button mat-stroked-button type="button" (click)="loadAddresses(addressPage.page - 1)" [disabled]="addressPage.first">Previous</button>
            <span>Page {{ addressPage.page }} of {{ addressPage.totalPages }}</span>
            <button mat-stroked-button type="button" (click)="loadAddresses(addressPage.page + 1)" [disabled]="addressPage.last">Next</button>
          </div>
        }
      </mat-card>
    </section>
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
  addresses: Address[] = [];
  addressPage: PageResponse<Address> | null = null;
  addressEditing = false;
  addressSaving = false;
  addressError = false;
  addressMessage = '';
  editingAddressId: number | null = null;
  readonly form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', [Validators.required, Validators.minLength(10)]],
    currentPassword: ['', Validators.minLength(6)],
    newPassword: ['', Validators.minLength(6)]
  });
  readonly addressForm = this.fb.nonNullable.group({
    country: ['', [Validators.required, Validators.minLength(2)]],
    region: ['', Validators.required],
    city: ['', Validators.required],
    postalCode: [1000, [Validators.required, Validators.min(1000)]],
    addressLine: ['', [Validators.required, Validators.minLength(5)]]
  });

  ngOnInit(): void {
    this.account.getProfile().subscribe((user) => {
      this.user = user;
      this.patchForm(user);
    });
    this.loadAddresses();
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

  loadAddresses(page = 1): void {
    this.account.getAddresses(page, 20).subscribe({
      next: (addressPage) => {
        this.addressPage = addressPage;
        this.addresses = addressPage.content;
      },
      error: (err) => this.setAddressMessage(err.error?.message ?? 'Could not load addresses.', true)
    });
  }

  startAddressCreate(): void {
    this.editingAddressId = null;
    this.addressEditing = true;
    this.addressMessage = '';
    this.addressError = false;
    this.addressForm.reset({
      country: '',
      region: '',
      city: '',
      postalCode: 1000,
      addressLine: ''
    });
  }

  startAddressEdit(address: Address): void {
    this.editingAddressId = address.id;
    this.addressEditing = true;
    this.addressMessage = '';
    this.addressError = false;
    this.addressForm.setValue({
      country: address.country,
      region: address.region,
      city: address.city,
      postalCode: address.postalCode,
      addressLine: address.addressLine
    });
  }

  cancelAddressEdit(): void {
    this.addressEditing = false;
    this.editingAddressId = null;
    this.addressSaving = false;
    this.addressForm.reset({
      country: '',
      region: '',
      city: '',
      postalCode: 1000,
      addressLine: ''
    });
  }

  saveAddress(): void {
    if (this.addressForm.invalid || this.addressSaving) {
      this.addressForm.markAllAsTouched();
      return;
    }

    const payload = this.addressPayload();
    const request = this.editingAddressId
      ? this.account.updateAddress(this.editingAddressId, payload)
      : this.account.createAddress(payload);

    this.addressSaving = true;
    this.addressMessage = '';
    this.addressError = false;
    request.subscribe({
      next: () => {
        this.cancelAddressEdit();
        this.setAddressMessage('Address saved.', false);
        this.loadAddresses(this.addressPage?.page ?? 1);
      },
      error: (err) => {
        this.addressSaving = false;
        this.setAddressMessage(err.error?.message ?? 'Could not save address.', true);
      }
    });
  }

  removeAddress(address: Address): void {
    if (!confirm('Remove this address from your address book?')) {
      return;
    }
    this.addressSaving = true;
    this.addressMessage = '';
    this.addressError = false;
    this.account.deleteAddress(address.id).subscribe({
      next: () => {
        this.addressSaving = false;
        this.setAddressMessage('Address removed.', false);
        const page = this.addresses.length === 1 && this.addressPage && this.addressPage.page > 1
          ? this.addressPage.page - 1
          : this.addressPage?.page ?? 1;
        this.loadAddresses(page);
      },
      error: (err) => {
        this.addressSaving = false;
        this.setAddressMessage(err.error?.message ?? 'Could not remove address.', true);
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

  private addressPayload(): AddressRequest {
    const value = this.addressForm.getRawValue();
    return {
      country: value.country,
      region: value.region,
      city: value.city,
      postalCode: Number(value.postalCode),
      addressLine: value.addressLine
    };
  }

  private setAddressMessage(message: string, isError: boolean): void {
    this.addressMessage = message;
    this.addressError = isError;
  }
}
