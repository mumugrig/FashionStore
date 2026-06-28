import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { AdminRow, AdminService } from '@features/admin/services/admin.service';
import {
  AdminField,
  adminResourceByKey,
  adminResources
} from '@features/admin/services/admin-resource.config';

@Component({
  standalone: true,
  imports: [
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatSelectModule,
    ReactiveFormsModule,
    RouterLink
  ],
  template: `
    <section class="admin-layout">
      <mat-card class="admin-nav">
        <h2>Admin</h2>
        <mat-nav-list>
          @for (resource of resources; track resource.key) {
            <a mat-list-item [routerLink]="['/admin', resource.key]">{{ resource.label }}</a>
          }
        </mat-nav-list>
      </mat-card>

      <section class="admin-content">
        <div class="page-heading">
          <h1>{{ pageTitle }}</h1>
          <a mat-stroked-button [routerLink]="['/admin', config.key]">Back to {{ config.label }}</a>
        </div>

        @if (message) {
          <p class="error">{{ message }}</p>
        }

        @if (!loading) {
          <mat-card class="panel admin-form-panel">
            <form [formGroup]="form" (ngSubmit)="save()">
              @for (field of config.fields; track field.key) {
                @if (field.type === 'checkbox') {
                  <mat-checkbox [formControlName]="field.key">{{ field.label }}</mat-checkbox>
                } @else {
                  <mat-form-field appearance="outline">
                    <mat-label>{{ field.label }}</mat-label>
                    @switch (field.type) {
                      @case ('textarea') {
                        <textarea matInput [formControlName]="field.key"></textarea>
                      }
                      @case ('select') {
                        <mat-select [formControlName]="field.key">
                          @for (option of field.options ?? []; track option) {
                            <mat-option [value]="option">{{ option }}</mat-option>
                          }
                        </mat-select>
                      }
                      @default {
                        <input matInput [type]="field.type" [formControlName]="field.key">
                      }
                    }
                    @if (control(field).hasError('required')) {
                      <mat-error>{{ field.label }} is required.</mat-error>
                    } @else if (control(field).hasError('email')) {
                      <mat-error>Enter a valid email address.</mat-error>
                    } @else if (control(field).hasError('pattern')) {
                      <mat-error>Enter a valid hex color.</mat-error>
                    } @else if (control(field).hasError('min')) {
                      <mat-error>Enter a value of 0 or greater.</mat-error>
                    }
                  </mat-form-field>
                }
              }

              <div class="inline-actions">
                <button mat-flat-button type="submit" [disabled]="form.invalid || saving">
                  {{ saving ? 'Saving' : 'Save' }}
                </button>
                <a mat-stroked-button [routerLink]="['/admin', config.key]">Cancel</a>
              </div>
            </form>
          </mat-card>
        }
      </section>
    </section>
  `
})
export class AdminResourceFormComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly admin = inject(AdminService);
  private readonly fb = inject(FormBuilder);
  readonly resources = adminResources;
  config = adminResourceByKey('items');
  form = this.fb.group<Record<string, FormControl<unknown>>>({});
  row: AdminRow = { id: 0 };
  loading = true;
  saving = false;
  message = '';

  ngOnInit(): void {
    const resource = this.route.snapshot.paramMap.get('resource');
    const id = Number(this.route.snapshot.paramMap.get('id') ?? 0);
    this.config = adminResourceByKey(resource);

    if (!id && !this.config.create) {
      this.message = `${this.config.label} cannot be created from the admin panel.`;
      this.loading = false;
      return;
    }
    if (id && !this.config.update) {
      this.message = `${this.config.label} cannot be edited from the admin panel.`;
      this.loading = false;
      return;
    }

    const stateRow = this.router.getCurrentNavigation()?.extras.state?.['row'] as AdminRow | undefined;
    if (id && stateRow?.id === id) {
      this.row = stateRow;
      this.buildForm(stateRow);
      this.loading = false;
      return;
    }

    if (id) {
      this.loadRow(id);
      return;
    }

    this.buildForm({ id: 0 });
    this.loading = false;
  }

  get pageTitle(): string {
    return `${this.row.id ? 'Edit' : 'Create'} ${this.config.label}`;
  }

  control(field: AdminField): FormControl<unknown> {
    return this.form.controls[field.key];
  }

  save(): void {
    if (this.form.invalid || this.saving) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.message = '';
    this.admin.save(this.config, this.row, this.formValue()).subscribe({
      next: () => this.router.navigate(['/admin', this.config.key]),
      error: (err) => {
        this.saving = false;
        this.message = err.error?.message ?? 'Could not save record.';
      }
    });
  }

  private loadRow(id: number): void {
    this.admin.get(this.config, id).subscribe({
      next: (row) => {
        this.row = row;
        this.buildForm(row);
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.message = err.status === 405 || err.status === 404
          ? 'Open this record from the admin list to edit it.'
          : err.error?.message ?? 'Could not load record.';
      }
    });
  }

  private buildForm(row: AdminRow): void {
    const controls: Record<string, FormControl<unknown>> = {};
    this.config.fields.forEach((field) => {
      controls[field.key] = new FormControl(this.initialValue(field, row), this.validators(field));
    });
    this.form = this.fb.group(controls);
  }

  private initialValue(field: AdminField, row: AdminRow): unknown {
    const value = row[field.key];
    if (value !== null && value !== undefined) {
      return value;
    }
    if (field.type === 'checkbox') {
      return false;
    }
    return '';
  }

  private validators(field: AdminField): ValidatorFn[] {
    const validators: ValidatorFn[] = [];
    if (field.required) {
      validators.push(Validators.required);
    }
    if (field.type === 'email') {
      validators.push(Validators.email);
    }
    if (field.type === 'number') {
      validators.push(Validators.min(0));
    }
    if (field.type === 'color') {
      validators.push(Validators.pattern(/^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/));
    }
    return validators;
  }

  private formValue(): Record<string, unknown> {
    const raw = this.form.getRawValue();
    const body: Record<string, unknown> = {};
    this.config.fields.forEach((field) => {
      const value = raw[field.key];
      body[field.key] = field.type === 'number' ? this.toNumberOrNull(value) : value;
    });
    return body;
  }

  private toNumberOrNull(value: unknown): number | null {
    if (value === '' || value === null || value === undefined) {
      return null;
    }
    return Number(value);
  }
}
