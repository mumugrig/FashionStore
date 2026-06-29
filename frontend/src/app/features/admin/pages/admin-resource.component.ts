import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { debounceTime, merge } from 'rxjs';
import { PageResponse } from '@shared/models/models';
import { AdminRow, AdminService } from '@features/admin/services/admin.service';
import { AdminColumn, adminResourceByKey, adminResources } from '@features/admin/services/admin-resource.config';

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
          <h1>{{ config.label }}</h1>
          @if (config.create) {
            <a mat-flat-button [routerLink]="['/admin', config.key, 'new']">New {{ config.label }}</a>
          }
        </div>

        @if (message) {
          <p class="error">{{ message }}</p>
        }

        <div class="admin-tools">
          <mat-form-field appearance="outline">
            <mat-label>Search</mat-label>
            <input matInput [formControl]="searchControl">
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Filter column</mat-label>
            <mat-select [formControl]="filterColumnControl">
              <mat-option value="">Any column</mat-option>
              @for (column of filterColumns; track column.key) {
                <mat-option [value]="column.key">{{ column.label }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Filter value</mat-label>
            <input matInput [formControl]="filterValueControl">
          </mat-form-field>

          <button mat-stroked-button type="button" (click)="clearFilters()" [disabled]="!hasFilters">Clear</button>
          @if (config.delete) {
            <button class="danger" mat-flat-button type="button" (click)="removeSelected()" [disabled]="!selectedIds.size">
              Delete selected
            </button>
          }
        </div>

        <mat-form-field class="column-picker" appearance="outline">
          <mat-label>Visible columns</mat-label>
          <mat-select [formControl]="visibleColumnsControl" multiple>
            @for (column of config.columns; track column.key) {
              <mat-option [value]="column.key">{{ column.label }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        <div class="admin-table-scroll">
          <table class="material-table">
            <thead>
              <tr>
                <th>
                  <mat-checkbox
                    [checked]="allVisibleSelected"
                    [indeterminate]="someVisibleSelected"
                    [disabled]="!rows.length"
                    (change)="toggleVisibleSelection($event.checked)">
                  </mat-checkbox>
                </th>
                <th>Details</th>
                @for (column of visibleColumns; track column.key) {
                  <th>{{ column.label }}</th>
                }
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (row of rows; track row.id) {
                <tr>
                  <td>
                    <mat-checkbox
                      [checked]="isSelected(row.id)"
                      (change)="toggleSelection(row.id, $event.checked)">
                    </mat-checkbox>
                  </td>
                  <td>
                    <button mat-stroked-button type="button" (click)="toggleDetails(row.id)">
                      {{ isExpanded(row.id) ? 'Hide' : 'Show' }}
                    </button>
                  </td>
                  @for (column of visibleColumns; track column.key) {
                    <td>{{ displayValue(row[column.key]) }}</td>
                  }
                  <td>
                    @if (config.update) {
                      <a mat-stroked-button [routerLink]="['/admin', config.key, row.id, 'edit']" [state]="{ row }">Edit</a>
                    }
                    @if (config.delete) {
                      <button mat-flat-button class="danger" (click)="remove(row.id)">Delete</button>
                    }
                  </td>
                </tr>
                @if (isExpanded(row.id)) {
                  <tr class="details-row">
                    <td [attr.colspan]="visibleColumns.length + 3">
                      <dl class="details-grid">
                        @for (column of config.columns; track column.key) {
                          <div>
                            <dt>{{ column.label }}</dt>
                            <dd>{{ displayValue(row[column.key]) }}</dd>
                          </div>
                        }
                      </dl>
                    </td>
                  </tr>
                }
              } @empty {
                <tr><td [attr.colspan]="visibleColumns.length + 3">No records.</td></tr>
              }
            </tbody>
          </table>
        </div>

        @if (page && page.totalPages > 1) {
          <div class="pager">
            <button mat-stroked-button (click)="load(page.page - 1)" [disabled]="page.first">Previous</button>
            <span>Page {{ page.page }} of {{ page.totalPages }}</span>
            <button mat-stroked-button (click)="load(page.page + 1)" [disabled]="page.last">Next</button>
          </div>
        }

      </section>
    </section>
  `
})
export class AdminResourceComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly admin = inject(AdminService);
  private readonly destroyRef = inject(DestroyRef);
  readonly resources = adminResources;
  config = adminResourceByKey('items');
  rows: AdminRow[] = [];
  selectedIds = new Set<number>();
  page: PageResponse<AdminRow> | null = null;
  message = '';
  readonly searchControl = new FormControl('', { nonNullable: true });
  readonly filterColumnControl = new FormControl('', { nonNullable: true });
  readonly filterValueControl = new FormControl('', { nonNullable: true });
  readonly visibleColumnsControl = new FormControl<string[]>([], { nonNullable: true });
  expandedIds = new Set<number>();

  ngOnInit(): void {
    merge(this.searchControl.valueChanges, this.filterColumnControl.valueChanges, this.filterValueControl.valueChanges)
      .pipe(debounceTime(250), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.load(1));

    this.visibleColumnsControl.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((columns) => this.saveVisibleColumns(columns));

    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      this.config = adminResourceByKey(params.get('resource'));
      this.clearSelection();
      this.expandedIds.clear();
      this.visibleColumnsControl.setValue(this.loadVisibleColumns(), { emitEvent: false });
      this.clearFilters(false);
      this.load();
    });
  }

  load(page = 1): void {
    this.message = '';
    this.admin.list(this.config, page, 20, {
      search: this.searchControl.value,
      filterColumn: this.filterColumnControl.value,
      filterValue: this.filterValueControl.value
    }).subscribe({
      next: (res) => {
        if (Array.isArray(res)) {
          this.page = null;
          this.rows = res;
        } else {
          this.page = res;
          this.rows = res.content;
        }
        this.pruneSelection();
        this.pruneExpandedRows();
      },
      error: (err) => (this.message = err.error?.message ?? 'Could not load records.')
    });
  }

  remove(id: number): void {
    this.admin.remove(this.config, id).subscribe({
      next: () => {
        this.selectedIds.delete(id);
        this.expandedIds.delete(id);
        this.load(this.page?.page ?? 1);
      },
      error: (err) => (this.message = err.error?.message ?? 'Could not delete record.')
    });
  }

  removeSelected(): void {
    if (!this.selectedIds.size) {
      return;
    }
    const ids = [...this.selectedIds];
    if (!confirm(`Delete ${ids.length} selected record${ids.length === 1 ? '' : 's'}?`)) {
      return;
    }
    this.admin.removeMany(this.config, ids).subscribe({
      next: () => {
        this.clearSelection();
        this.load(this.page?.page ?? 1);
      },
      error: (err) => (this.message = err.error?.message ?? 'Could not delete selected records.')
    });
  }

  clearFilters(reload = true): void {
    this.searchControl.setValue('', { emitEvent: false });
    this.filterColumnControl.setValue('', { emitEvent: false });
    this.filterValueControl.setValue('', { emitEvent: false });
    if (reload) {
      this.load(1);
    }
  }

  toggleSelection(id: number, checked: boolean): void {
    if (checked) {
      this.selectedIds.add(id);
    } else {
      this.selectedIds.delete(id);
    }
  }

  toggleVisibleSelection(checked: boolean): void {
    this.rows.forEach((row) => this.toggleSelection(row.id, checked));
  }

  toggleDetails(id: number): void {
    if (this.expandedIds.has(id)) {
      this.expandedIds.delete(id);
    } else {
      this.expandedIds.add(id);
    }
  }

  isExpanded(id: number): boolean {
    return this.expandedIds.has(id);
  }

  isSelected(id: number): boolean {
    return this.selectedIds.has(id);
  }

  get filterColumns(): AdminColumn[] {
    const columns = new Map<string, AdminColumn>();
    this.config.columns.forEach((column) => columns.set(column.key, column));
    this.config.fields.forEach((field) => {
      if (!columns.has(field.key)) {
        columns.set(field.key, { key: field.key, label: field.label });
      }
    });
    return [...columns.values()];
  }

  get visibleColumns(): AdminColumn[] {
    const visible = new Set(this.visibleColumnsControl.value);
    return this.config.columns.filter((column) => visible.has(column.key));
  }

  get hasFilters(): boolean {
    return !!this.searchControl.value || !!this.filterColumnControl.value || !!this.filterValueControl.value;
  }

  get allVisibleSelected(): boolean {
    return !!this.rows.length && this.rows.every((row) => this.selectedIds.has(row.id));
  }

  get someVisibleSelected(): boolean {
    return !this.allVisibleSelected && this.rows.some((row) => this.selectedIds.has(row.id));
  }

  private clearSelection(): void {
    this.selectedIds.clear();
  }

  private pruneSelection(): void {
    const rowIds = new Set(this.rows.map((row) => row.id));
    this.selectedIds.forEach((id) => {
      if (!rowIds.has(id)) {
        this.selectedIds.delete(id);
      }
    });
  }

  private pruneExpandedRows(): void {
    const rowIds = new Set(this.rows.map((row) => row.id));
    this.expandedIds.forEach((id) => {
      if (!rowIds.has(id)) {
        this.expandedIds.delete(id);
      }
    });
  }

  private loadVisibleColumns(): string[] {
    const defaults = this.defaultVisibleColumns();
    const stored = localStorage.getItem(this.visibleColumnsStorageKey());
    if (!stored) {
      return defaults;
    }
    try {
      const parsed = JSON.parse(stored) as string[];
      const validColumns = new Set(this.config.columns.map((column) => column.key));
      const selected = parsed.filter((column) => validColumns.has(column));
      return selected.length ? selected : defaults;
    } catch {
      return defaults;
    }
  }

  private saveVisibleColumns(columns: string[]): void {
    localStorage.setItem(this.visibleColumnsStorageKey(), JSON.stringify(columns));
  }

  private defaultVisibleColumns(): string[] {
    const defaults = this.config.columns.filter((column) => column.defaultVisible).map((column) => column.key);
    return defaults.length ? defaults : this.config.columns.slice(0, 4).map((column) => column.key);
  }

  private visibleColumnsStorageKey(): string {
    return `admin.visibleColumns.${this.config.key}`;
  }

  displayValue(value: unknown): string {
    if (value === null || value === undefined || value === '') {
      return '-';
    }
    if (Array.isArray(value)) {
      if (!value.length) {
        return '-';
      }
      return value.map((item) => this.displayArrayItem(item)).join(', ');
    }
    if (typeof value === 'object') {
      return JSON.stringify(value);
    }
    return String(value);
  }

  private displayArrayItem(value: unknown): string {
    if (value === null || value === undefined || value === '') {
      return '-';
    }
    if (typeof value !== 'object') {
      return String(value);
    }
    const row = value as Record<string, unknown>;
    const label = row['name'] ?? row['email'] ?? row['id'];
    return label === null || label === undefined ? JSON.stringify(value) : String(label);
  }
}
