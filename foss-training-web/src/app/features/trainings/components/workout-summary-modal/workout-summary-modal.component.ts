import { Component, input, output, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TrainingService } from '../../../../core/services/training.service';
import type { WorkoutSummaryResponse } from '../../../../core/api/models';

@Component({
  selector: 'app-workout-summary-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './workout-summary-modal.component.html',
  styleUrl: './workout-summary-modal.component.scss'
})
export class WorkoutSummaryModalComponent implements OnInit {
  private readonly trainingService = inject(TrainingService);

  readonly summary = input<WorkoutSummaryResponse | null>(null);
  readonly trainingId = input<number | null>(null);

  readonly close = output<void>();

  readonly loadedSummary = signal<WorkoutSummaryResponse | null>(null);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit() {
    if (this.summary()) {
      this.loadedSummary.set(this.summary());
    } else if (this.trainingId()) {
      this.fetchSummary(this.trainingId()!);
    }
  }

  fetchSummary(id: number) {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.trainingService.getWorkoutSummary(id).subscribe({
      next: (data) => {
        this.loadedSummary.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err?.message || 'Failed to load workout summary');
        this.isLoading.set(false);
      }
    });
  }

  getRpeDescriptor(rpe?: number | null): string {
    if (rpe === undefined || rpe === null) return '';
    if (rpe <= 4) return 'Very Light Effort';
    if (rpe <= 6) return 'Moderate Effort';
    if (rpe <= 7.5) return 'Challenging Effort';
    if (rpe <= 8.5) return 'Hard Effort';
    if (rpe <= 9.5) return 'Near Maximum Effort';
    return 'Maximum Effort';
  }

  onClose() {
    this.close.emit();
  }
}
