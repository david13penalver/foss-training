import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TrainingService } from '../../../../core/services/training.service';
import { SessionService } from '../../../../core/services/session.service';
import { TrainingCardComponent } from '../../components/training-card/training-card.component';
import { ActiveWorkoutModalComponent } from '../../components/active-workout-modal/active-workout-modal.component';
import { TrainingScheduleModalComponent, ScheduleTrainingPayload } from '../../components/training-schedule-modal/training-schedule-modal.component';
import { TrainingDetailModalComponent } from '../../components/training-detail-modal/training-detail-modal.component';
import { ConfirmDialogComponent } from '../../../../shared/ui/confirm-dialog/confirm-dialog.component';
import type { Training } from '../../../../core/api/models';

@Component({
  selector: 'app-trainings-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TrainingCardComponent,
    ActiveWorkoutModalComponent,
    TrainingScheduleModalComponent,
    TrainingDetailModalComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './trainings-page.component.html',
  styleUrl: './trainings-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrainingsPageComponent {
  private readonly trainingService = inject(TrainingService);
  private readonly sessionService = inject(SessionService);

  readonly trainings = this.trainingService.trainingsResource.value;
  readonly isLoading = this.trainingService.trainingsResource.isLoading;
  readonly error = this.trainingService.trainingsResource.error;

  // Filters
  readonly searchQuery = signal('');
  readonly selectedStatus = signal('ALL');

  // Modals
  readonly isScheduleOpen = signal(false);
  readonly isActiveTrackerOpen = signal(false);
  readonly activeWorkout = signal<Training | null>(null);

  readonly isDetailOpen = signal(false);
  readonly detailWorkout = signal<Training | null>(null);

  readonly isDeleteConfirmOpen = signal(false);
  readonly workoutToDelete = signal<Training | null>(null);

  readonly notificationMessage = signal('');

  readonly statuses = [
    'ALL',
    'In Progress',
    'Planned',
    'Completed',
    'Cancelled'
  ];

  // Derived KPI Metrics
  readonly activeCount = computed(() => {
    return (this.trainings() ?? []).filter(t => t.status === 'In Progress').length;
  });

  readonly completedCount = computed(() => {
    return (this.trainings() ?? []).filter(t => t.status === 'Completed').length;
  });

  readonly totalVolume = computed(() => {
    return (this.trainings() ?? []).reduce((acc, t) => acc + (t.totalVolume || 0), 0);
  });

  // Filtered List
  readonly filteredTrainings = computed(() => {
    const list = this.trainings() ?? [];
    const query = this.searchQuery().trim().toLowerCase();
    const st = this.selectedStatus();

    return list.filter(item => {
      const matchesSearch =
        !query ||
        (item.name && item.name.toLowerCase().includes(query)) ||
        (item.description && item.description.toLowerCase().includes(query)) ||
        (item.session?.name && item.session.name.toLowerCase().includes(query)) ||
        (item.session?.sessionExercises?.some(se => se.exercise?.name?.toLowerCase().includes(query)));

      const matchesStatus = st === 'ALL' || item.status === st;

      return matchesSearch && matchesStatus;
    });
  });

  openScheduleModal() {
    this.isScheduleOpen.set(true);
  }

  handleSchedule(payload: ScheduleTrainingPayload) {
    this.sessionService.startTrainingFromSession(payload.sessionId, payload.date, payload.customName).subscribe({
      next: () => {
        this.trainingService.trainingsResource.reload();
        this.isScheduleOpen.set(false);
        this.notify('Workout scheduled successfully!');
      },
      error: () => {
        this.trainingService.trainingsResource.reload();
        this.isScheduleOpen.set(false);
      }
    });
  }

  handleStart(training: Training) {
    if (training.status === 'Planned' && training.id) {
      this.trainingService.startTraining(training.id).subscribe({
        next: (started) => {
          this.trainingService.trainingsResource.reload();
          this.activeWorkout.set(started);
          this.isActiveTrackerOpen.set(true);
        },
        error: () => {
          this.activeWorkout.set({ ...training, status: 'In Progress' });
          this.isActiveTrackerOpen.set(true);
        }
      });
    } else {
      this.activeWorkout.set(training);
      this.isActiveTrackerOpen.set(true);
    }
  }

  handleComplete(training: Training) {
    if (training.id) {
      this.trainingService.completeTraining(training.id).subscribe({
        next: () => {
          this.trainingService.trainingsResource.reload();
          this.isActiveTrackerOpen.set(false);
          this.notify(`🎉 Workout "${training.name}" marked as Completed!`);
        },
        error: () => {
          this.trainingService.trainingsResource.reload();
          this.isActiveTrackerOpen.set(false);
          this.notify(`🎉 Workout "${training.name}" marked as Completed!`);
        }
      });
    }
  }

  handleCancel(training: Training) {
    if (training.id) {
      this.trainingService.cancelTraining(training.id).subscribe({
        next: () => {
          this.trainingService.trainingsResource.reload();
          this.isActiveTrackerOpen.set(false);
          this.notify(`Workout "${training.name}" cancelled.`);
        },
        error: () => {
          this.trainingService.trainingsResource.reload();
          this.isActiveTrackerOpen.set(false);
        }
      });
    }
  }

  openDetailModal(training: Training) {
    this.detailWorkout.set(training);
    this.isDetailOpen.set(true);
  }

  openDeleteConfirm(training: Training) {
    this.workoutToDelete.set(training);
    this.isDeleteConfirmOpen.set(true);
  }

  confirmDelete() {
    const target = this.workoutToDelete();
    if (target?.id) {
      this.trainingService.deleteTraining(target.id).subscribe({
        next: () => {
          this.trainingService.trainingsResource.reload();
          this.closeModals();
          this.notify(`Workout deleted.`);
        }
      });
    }
  }

  closeModals() {
    this.isScheduleOpen.set(false);
    this.isActiveTrackerOpen.set(false);
    this.activeWorkout.set(null);
    this.isDetailOpen.set(false);
    this.detailWorkout.set(null);
    this.isDeleteConfirmOpen.set(false);
    this.workoutToDelete.set(null);
  }

  private notify(msg: string) {
    this.notificationMessage.set(msg);
    setTimeout(() => this.notificationMessage.set(''), 4000);
  }
}
