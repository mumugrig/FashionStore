import { CurrencyPipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Item } from '@shared/models/models';

@Component({
  standalone: true,
  imports: [CurrencyPipe],
  template: `
    @if (item) {
      <h1>{{ item.name }}</h1>
      <p>{{ item.description }}</p>
      <p class="price">{{ item.price | currency }}</p>
    }
  `
})
export class ProductSummaryWidgetComponent {
  @Input({ required: true }) item!: Item;
}
