import { Component, ChangeDetectionStrategy, input, output } from '@angular/core';
import type { Session, ResistanceSessionExercise } from '../../../../core/api/models';

@Component({
  selector: 'app-session-detail-modal',
  standalone: true,
  templateUrl: './session-detail-modal.component.html',
  styleUrl: './session-detail-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionDetailModalComponent {
  readonly session = input.required<Session>();

  readonly closed = output<void>();
  readonly edit = output<Session>();
  readonly clone = output<Session>();
  readonly startTraining = output<Session>();

  asResistance(item: any): ResistanceSessionExercise {
    return item as ResistanceSessionExercise;
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.closed.emit();
    }
  }
}
