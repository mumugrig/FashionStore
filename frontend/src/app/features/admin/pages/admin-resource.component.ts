import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { PageResponse } from '@shared/models/models';
import { AdminRow, AdminService } from '@features/admin/services/admin.service';
import { adminResourceByKey, adminResources } from '@features/admin/services/admin-resource.config';

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
            <button mat-flat-button (click)="newRow()">New {{ config.label }}</button>
          }
        </div>

        @if (message) {
          <p class="error">{{ message }}</p>
        }

        <table class="material-table">
          <thead>
            <tr>
              @for (column of config.columns; track column) {
                <th>{{ column }}</th>
              }
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (row of rows; track row.id) {
              <tr>
                @for (column of config.columns; track column) {
                  <td>{{ row[column] }}</td>
                }
                <td>
                  @if (config.update) {
                    <button mat-stroked-button (click)="edit(row)">Edit</button>
                  }
                  @if (config.delete) {
                    <button mat-flat-button class="danger" (click)="remove(row.id)">Delete</button>
                  }
                </td>
              </tr>
            } @empty {
              <tr><td [attr.colspan]="config.columns.length + 1">No records.</td></tr>
            }
          </tbody>
        </table>

        @if (page && page.totalPages > 1) {
          <div class="pager">
            <button mat-stroked-button (click)="load(page.page - 1)" [disabled]="page.first">Previous</button>
            <span>Page {{ page.page }} of {{ page.totalPages }}</span>
            <button mat-stroked-button (click)="load(page.page + 1)" [disabled]="page.last">Next</button>
          </div>
        }

        @if (editing) {
          <mat-card class="panel">
            <mat-card-title>{{ editing.id ? 'Edit' : 'Create' }} {{ config.label }}</mat-card-title>
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
                  </mat-form-field>
                }
              }
              <div class="inline-actions">
                <button mat-flat-button type="submit" [disabled]="form.invalid">Save</button>
                <button mat-stroked-button type="button" (click)="cancel()">Cancel</button>
              </div>
            </form>
          </mat-card>
        }
      </section>
    </section>
  `
})
export class AdminResourceComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly admin = inject(AdminService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  readonly resources = adminResources;
  config = adminResourceByKey('items');
  rows: AdminRow[] = [];
  page: PageResponse<AdminRow> | null = null;
  editing: AdminRow | null = null;
  message = '';
  form = this.fb.group<Record<string, FormControl<unknown>>>({});

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      this.config = adminResourceByKey(params.get('resource'));
      this.cancel();
      this.load();
    });
  }

  load(page = 1): void {
    this.message = '';
    this.admin.list(this.config, page, 20).subscribe({
      next: (res) => {
        if (Array.isArray(res)) {
          this.page = null;
          this.rows = res;
        } else {
          this.page = res;
          this.rows = res.content;
        }
      },
      error: (err) => (this.message = err.error?.message ?? 'Could not load records.')
    });
  }

  newRow(): void {
    this.startEdit({ id: 0 });
  }

  edit(row: AdminRow): void {
    this.startEdit(row);
  }

  save(): void {
    if (!this.editing) {
      return;
    }
    const body = this.form.getRawValue();
    this.admin.save(this.config, this.editing, body).subscribe({
      next: () => {
        this.cancel();
        this.load(this.page?.page ?? 1);
      },
      error: (err) => (this.message = err.error?.message ?? 'Could not save record.')
    });
  }

  remove(id: number): void {
    this.admin.remove(this.config, id).subscribe({
      next: () => this.load(this.page?.page ?? 1),
      error: (err) => (this.message = err.error?.message ?? 'Could not delete record.')
    });
  }

  cancel(): void {
    this.editing = null;
    this.form = this.fb.group<Record<string, FormControl<unknown>>>({});
  }

  private startEdit(row: AdminRow): void {
    this.editing = row;
    const controls: Record<string, FormControl<unknown>> = {};
    this.config.fields.forEach((field) => {
      const value = row[field.key] ?? (field.type === 'checkbox' ? false : '');
      controls[field.key] = new FormControl(
        value,
        field.required ? { nonNullable: true, validators: Validators.required } : undefined
      );
    });
    this.form = this.fb.group(controls);
  }
}
