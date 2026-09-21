import { Component, ChangeDetectionStrategy, input, output } from '@angular/core';
import type { TrainingProgram } from '../../../../core/api/models';

@Component({
  selector: 'app-program-detail-modal',
  standalone: true,
  templateUrl: './program-detail-modal.component.html',
  styleUrl: './program-detail-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgramDetailModalComponent {
  readonly program = input.required<TrainingProgram>();

  readonly closed = output<void>();
  readonly viewAdherence = output<TrainingProgram>();
  readonly clone = output<TrainingProgram>();
  readonly edit = output<TrainingProgram>();
  readonly schedule = output<TrainingProgram>();

  getDayLabel(dayOfWeek?: number): string {
    const days = ['', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    return dayOfWeek && days[dayOfWeek] ? days[dayOfWeek] : `Day ${dayOfWeek}`;
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.closed.emit();
    }
  }
}
