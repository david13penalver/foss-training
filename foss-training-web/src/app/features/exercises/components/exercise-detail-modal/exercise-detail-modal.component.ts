import { Component, ChangeDetectionStrategy, input, output, computed, inject, signal, effect } from '@angular/core';
import type { Exercise, PersonalRecordResponse } from '../../../../core/api/models';
import { AnalyticsService } from '../../../../core/services/analytics.service';

@Component({
  selector: 'app-exercise-detail-modal',
  standalone: true,
  templateUrl: './exercise-detail-modal.component.html',
  styleUrl: './exercise-detail-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExerciseDetailModalComponent {
  private readonly analyticsService = inject(AnalyticsService, { optional: true });

  readonly exercise = input.required<Exercise>();

  readonly closed = output<void>();
  readonly edit = output<Exercise>();

  readonly personalRecord = signal<PersonalRecordResponse | null>(null);

  readonly resistanceMetrics = computed(() => this.exercise().resistanceMetrics);
  readonly enduranceMetrics = computed(() => this.exercise().enduranceMetrics);
  readonly mobilityMetrics = computed(() => this.exercise().mobilityMetrics);

  constructor() {
    effect(() => {
      const ex = this.exercise();
      if (ex?.id && this.analyticsService) {
        this.analyticsService.getPersonalRecordsByExercise(ex.id).subscribe({
          next: pr => this.personalRecord.set(pr),
          error: () => this.personalRecord.set(null)
        });
      }
    });
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.closed.emit();
    }
  }
}
