import { Component, ChangeDetectionStrategy, input, output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfirmDialogComponent {
  readonly title = input<string>('Confirm Action');
  readonly message = input<string>('Are you sure you want to proceed?');
  readonly confirmText = input<string>('Delete');
  readonly cancelText = input<string>('Cancel');
  readonly isDestructive = input<boolean>(true);

  readonly confirmed = output<void>();
  readonly cancelled = output<void>();

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.cancelled.emit();
    }
  }
}
