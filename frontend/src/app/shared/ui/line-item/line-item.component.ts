import { CurrencyPipe } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-line-item',
  standalone: true,
  imports: [CurrencyPipe],
  template: `
    <div class="line-item">
      <div
        class="line-item-image"
        [style.background-image]="imageUrl ? 'url(' + imageUrl + ')' : null">
      </div>
      <div>
        <strong>{{ title || 'Product option' }}</strong>
        <span>{{ subtitle || fallbackSubtitle }}</span>
        @if (price) {
          <small>{{ price | currency }}</small>
        }
      </div>
    </div>
  `
})
export class LineItemComponent {
  @Input() imageUrl: string | null | undefined;
  @Input() title: string | null | undefined;
  @Input() subtitle: string | null | undefined;
  @Input() price: number | null | undefined;
  @Input() fallbackSubtitle = 'Selected option';
}
