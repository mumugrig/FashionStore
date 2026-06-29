import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
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

interface ReferenceOption {
  value: number;
  label: string;
  searchText: string;
}

@Component({
  standalone: true,
  imports: [
    MatButtonModule,
    MatAutocompleteModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
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
                } @else if (field.reference) {
                  @if (field.multiple) {
                    <mat-form-field appearance="outline">
                      <mat-label>{{ field.label }}</mat-label>
                      <mat-chip-grid #multiReferenceChipGrid>
                        @for (option of selectedMultiReferenceOptions(field); track option.value) {
                          <mat-chip-row (removed)="removeMultiReferenceOption(field, option.value)">
                            {{ option.label }}
                            <button matChipRemove type="button" [attr.aria-label]="'Remove ' + option.label">x</button>
                          </mat-chip-row>
                        }
                      </mat-chip-grid>
                      <input
                        matInput
                        type="text"
                        [formControl]="multiReferenceSearchControl(field)"
                        [matChipInputFor]="multiReferenceChipGrid"
                        [matAutocomplete]="multiReferenceAuto">
                      <mat-autocomplete
                        #multiReferenceAuto="matAutocomplete"
                        (optionSelected)="selectMultiReferenceOption(field, $event.option.value)">
                        @for (option of filteredMultiReferenceOptions(field); track option.value) {
                          <mat-option [value]="option">{{ option.label }}</mat-option>
                        }
                      </mat-autocomplete>
                      @if (control(field).hasError('required')) {
                        <mat-error>{{ field.label }} is required.</mat-error>
                      }
                    </mat-form-field>
                  } @else {
                    <mat-form-field appearance="outline">
                      <mat-label>{{ field.label }}</mat-label>
                      <input
                        matInput
                        type="text"
                        [formControl]="referenceSearchControl(field)"
                        [matAutocomplete]="referenceAuto"
                        (keydown)="clearReferenceSelectionForTyping(field, $event)">
                      <mat-autocomplete
                        #referenceAuto="matAutocomplete"
                        [displayWith]="referenceDisplay(field)"
                        (optionSelected)="selectReferenceOption(field, $event.option.value)">
                        @if (!isFieldRequired(field)) {
                          <mat-option [value]="null">None</mat-option>
                        }
                        @for (option of filteredReferenceOptions(field); track option.value) {
                          <mat-option [value]="option">{{ option.label }}</mat-option>
                        }
                      </mat-autocomplete>
                      @if (control(field).hasError('required')) {
                        <mat-error>{{ field.label }} is required.</mat-error>
                      } @else if (control(field).hasError('min')) {
                        <mat-error>Choose a valid option.</mat-error>
                      }
                    </mat-form-field>
                  }
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
  referenceOptions: Record<string, ReferenceOption[]> = {};
  referenceSearch: Record<string, FormControl<string | ReferenceOption | null>> = {};
  multiReferenceSearch: Record<string, FormControl<string>> = {};

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

  isFieldRequired(field: AdminField): boolean {
    return !!field.required;
  }

  referenceSearchControl(field: AdminField): FormControl<string | ReferenceOption | null> {
    let control = this.referenceSearch[field.key];
    if (!control) {
      control = new FormControl<string | ReferenceOption | null>('');
      this.referenceSearch[field.key] = control;
    }
    return control;
  }

  filteredReferenceOptions(field: AdminField): ReferenceOption[] {
    const options = this.referenceOptions[field.key] ?? [];
    const searchValue = this.referenceSearchControl(field).value;
    const search = typeof searchValue === 'string' ? searchValue.trim().toLowerCase() : '';
    if (!search) {
      return options;
    }
    return options.filter((option) => option.searchText.includes(search));
  }

  referenceOptionsFor(field: AdminField): ReferenceOption[] {
    return this.referenceOptions[field.key] ?? [];
  }

  multiReferenceSearchControl(field: AdminField): FormControl<string> {
    let control = this.multiReferenceSearch[field.key];
    if (!control) {
      control = new FormControl('', { nonNullable: true });
      this.multiReferenceSearch[field.key] = control;
    }
    return control;
  }

  filteredMultiReferenceOptions(field: AdminField): ReferenceOption[] {
    const options = this.referenceOptionsFor(field);
    const search = this.multiReferenceSearchControl(field).value.trim().toLowerCase();
    const selected = new Set(this.selectedMultiReferenceIds(field));
    const available = options.filter((option) => !selected.has(option.value));
    if (!search) {
      return available;
    }
    return available.filter((option) => option.searchText.includes(search));
  }

  selectedMultiReferenceOptions(field: AdminField): ReferenceOption[] {
    const selected = new Set(this.selectedMultiReferenceIds(field));
    return this.referenceOptionsFor(field).filter((option) => selected.has(option.value));
  }

  selectMultiReferenceOption(field: AdminField, option: ReferenceOption): void {
    const selected = this.selectedMultiReferenceIds(field);
    if (!selected.includes(option.value)) {
      this.control(field).setValue([...selected, option.value]);
    }
    this.multiReferenceSearchControl(field).setValue('');
  }

  removeMultiReferenceOption(field: AdminField, value: number): void {
    this.control(field).setValue(this.selectedMultiReferenceIds(field).filter((id) => id !== value));
  }

  referenceDisplay(field: AdminField): (value: ReferenceOption | string | null) => string {
    return (value) => {
      if (!value) {
        return '';
      }
      if (typeof value !== 'string') {
        return value.label;
      }
      const selected = this.selectedReferenceOption(field);
      return selected?.label ?? value;
    };
  }

  selectReferenceOption(field: AdminField, option: ReferenceOption | null): void {
    this.control(field).setValue(option?.value ?? null);
    this.referenceSearchControl(field).setValue(option);
  }

  syncReferenceSearchLabel(field: AdminField): void {
    const selected = this.selectedReferenceOption(field);
    this.referenceSearchControl(field).setValue(selected ?? '');
  }

  clearReferenceSelectionForTyping(field: AdminField, event: KeyboardEvent): void {
    if (!this.isTextEditingKey(event.key) || typeof this.referenceSearchControl(field).value === 'string') {
      return;
    }
    this.control(field).setValue(null);
    this.referenceSearchControl(field).setValue('');
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
    this.referenceOptions = {};
    this.referenceSearch = {};
    this.multiReferenceSearch = {};
    this.config.fields.forEach((field) => {
      controls[field.key] = new FormControl(this.initialValue(field, row), this.validators(field, row));
    });
    this.form = this.fb.group(controls);
    this.config.fields
      .filter((field) => field.reference && !field.multiple)
      .forEach((field) => this.watchReferenceSearch(field));
    this.loadReferenceOptions();
  }

  private initialValue(field: AdminField, row: AdminRow): unknown {
    const value = row[field.key];
    if (value !== null && value !== undefined) {
      return value;
    }
    if (field.multiple) {
      return [];
    }
    if (field.type === 'checkbox') {
      return false;
    }
    return '';
  }

  private validators(field: AdminField, row: AdminRow): ValidatorFn[] {
    const validators: ValidatorFn[] = [];
    if (field.required) {
      validators.push(Validators.required);
    }
    if (field.type === 'email') {
      validators.push(Validators.email);
    }
    if (field.type === 'number' && !field.multiple) {
      validators.push(Validators.min(0));
    }
    if (field.type === 'color') {
      validators.push(Validators.pattern(/^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/));
    }
    return validators;
  }

  private loadReferenceOptions(): void {
    this.config.fields
      .filter((field) => field.reference)
      .forEach((field) => {
        const reference = field.reference!;
        const resource = adminResourceByKey(reference.resourceKey);
        this.admin.list(resource, 1, 100).subscribe({
          next: (result) => {
            const rows = Array.isArray(result) ? result : result.content;
            this.referenceOptions[field.key] = rows
              .map((row) => this.toReferenceOption(field, row))
              .filter((option): option is ReferenceOption => option !== null);
            if (!field.multiple) {
              this.syncReferenceSearchLabel(field);
            }
          },
          error: () => {
            this.referenceOptions[field.key] = [];
          }
        });
      });
  }

  private watchReferenceSearch(field: AdminField): void {
    this.referenceSearchControl(field).valueChanges.subscribe((value) => {
      if (typeof value === 'string') {
        this.control(field).setValue(null);
      }
    });
  }

  private toReferenceOption(field: AdminField, row: AdminRow): ReferenceOption | null {
    const reference = field.reference;
    if (!reference) {
      return null;
    }
    const rawValue = row[reference.valueKey];
    const value = typeof rawValue === 'number' ? rawValue : Number(rawValue);
    if (!Number.isFinite(value)) {
      return null;
    }
    const details = reference.labelKeys
      .map((key) => row[key])
      .filter((value) => value !== null && value !== undefined && value !== '')
      .map((value) => String(value));
    const label = details.length ? `#${value} - ${details.join(' / ')}` : `#${value}`;
    return {
      value,
      label,
      searchText: `${value} ${details.join(' ')}`.toLowerCase()
    };
  }

  private selectedReferenceOption(field: AdminField): ReferenceOption | null {
    const selectedValue = this.control(field).value;
    const numericValue = typeof selectedValue === 'number' ? selectedValue : Number(selectedValue);
    if (!Number.isFinite(numericValue)) {
      return null;
    }
    return (this.referenceOptions[field.key] ?? []).find((option) => option.value === numericValue) ?? null;
  }

  private selectedMultiReferenceIds(field: AdminField): number[] {
    return this.toNumberArray(this.control(field).value);
  }

  private isTextEditingKey(key: string): boolean {
    return key.length === 1 || key === 'Backspace' || key === 'Delete';
  }

  private formValue(): Record<string, unknown> {
    const raw = this.form.getRawValue();
    const body: Record<string, unknown> = {};
    this.config.fields.forEach((field) => {
      const value = raw[field.key];
      if (this.config.key === 'users' && field.key === 'password' && this.row.id && !String(value ?? '').trim()) {
        return;
      }
      body[field.requestKey ?? field.key] = field.multiple
        ? this.toNumberArray(value)
        : field.type === 'number'
          ? this.toNumberOrNull(value)
          : value;
    });
    return body;
  }

  private toNumberArray(value: unknown): number[] {
    if (!Array.isArray(value)) {
      return [];
    }
    return value
      .map((item) => Number(item))
      .filter((item) => Number.isFinite(item));
  }

  private toNumberOrNull(value: unknown): number | null {
    if (value === '' || value === null || value === undefined) {
      return null;
    }
    return Number(value);
  }
}
