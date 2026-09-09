import { Component, ChangeDetectionStrategy, input, output, computed } from '@angular/core';
import type { Exercise } from '../../../../core/api/models';

@Component({
  selector: 'app-exercise-detail-modal',
  standalone: true,
  templateUrl: './exercise-detail-modal.component.html',
  styleUrl: './exercise-detail-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExerciseDetailModalComponent {
  readonly exercise = input.required<Exercise>();

  readonly closed = output<void>();
  readonly edit = output<Exercise>();

  readonly resistanceMetrics = computed(() => this.exercise().resistanceMetrics);
  readonly enduranceMetrics = computed(() => this.exercise().enduranceMetrics);
  readonly mobilityMetrics = computed(() => this.exercise().mobilityMetrics);

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.closed.emit();
    }
  }
}
