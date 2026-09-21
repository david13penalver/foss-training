import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TrainingService } from '../../../../core/services/training.service';
import { SessionService } from '../../../../core/services/session.service';
import { TrainingCardComponent } from '../../components/training-card/training-card.component';
import { ActiveWorkoutModalComponent } from '../../components/active-workout-modal/active-workout-modal.component';
import { TrainingScheduleModalComponent, ScheduleTrainingPayload } from '../../components/training-schedule-modal/training-schedule-modal.component';
import { TrainingDetailModalComponent } from '../../components/training-detail-modal/training-detail-modal.component';
import { WorkoutSummaryModalComponent } from '../../components/workout-summary-modal/workout-summary-modal.component';
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
    WorkoutSummaryModalComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './trainings-page.component.html',
  styleUrl: './trainings-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrainingsPageComponent {
  private readonly trainingService = inject(TrainingService);
  private readonly sessionService = inject(SessionService);
  readonly Math = Math;

  readonly trainings = this.trainingService.trainingsResource.value;
  readonly isLoading = this.trainingService.trainingsResource.isLoading;
  readonly error = this.trainingService.trainingsResource.error;

  // Filters
  readonly searchQuery = signal('');
  readonly selectedStatus = signal('ALL');
  readonly startDate = signal('');
  readonly endDate = signal('');
  readonly datePreset = signal<'ALL' | 'THIS_WEEK' | 'THIS_MONTH'>('ALL');

  // Pagination
  readonly currentPage = signal(0);
  readonly pageSize = signal(9);

  // Modals
  readonly isScheduleOpen = signal(false);
  readonly isActiveTrackerOpen = signal(false);
  readonly activeWorkout = signal<Training | null>(null);

  readonly isSummaryOpen = signal(false);
  readonly summaryWorkoutId = signal<number | null>(null);

  readonly isDetailOpen = signal(false);
  readonly detailWorkout = signal<Training | null>(null);

  readonly isDeleteConfirmOpen = signal(false);
  readonly workoutToDelete = signal<Training | null>(null);

  readonly notificationMessage = signal('');

  readonly statuses = [
    'ALL',
    'In Progress',
    'Paused',
    'Planned',
    'Completed',
    'Cancelled'
  ];

  // Derived KPI Metrics
  readonly activeCount = computed(() => {
    return (this.trainings() ?? []).filter(t => t.status === 'In Progress' || t.status === 'Paused').length;
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
    const start = this.startDate();
    const end = this.endDate();

    return list.filter(item => {
      const matchesSearch =
        !query ||
        (item.name && item.name.toLowerCase().includes(query)) ||
        (item.description && item.description.toLowerCase().includes(query)) ||
        (item.session?.name && item.session.name.toLowerCase().includes(query)) ||
        (item.session?.sessionExercises?.some(se => se.exercise?.name?.toLowerCase().includes(query)));

      const matchesStatus = st === 'ALL' || item.status === st;
      const matchesStart = !start || (item.trainingDate && item.trainingDate >= start);
      const matchesEnd = !end || (item.trainingDate && item.trainingDate <= end);

      return matchesSearch && matchesStatus && matchesStart && matchesEnd;
    });
  });

  readonly totalPages = computed(() => {
    const count = this.filteredTrainings().length;
    return Math.max(1, Math.ceil(count / this.pageSize()));
  });

  readonly paginatedTrainings = computed(() => {
    const list = this.filteredTrainings();
    const page = this.currentPage();
    const size = this.pageSize();
    const start = page * size;
    return list.slice(start, start + size);
  });

  // Filter change handlers
  onSearchChange(query: string) {
    this.searchQuery.set(query);
    this.currentPage.set(0);
  }

  onStatusChange(status: string) {
    this.selectedStatus.set(status);
    this.currentPage.set(0);
  }

  onStartDateChange(date: string) {
    this.startDate.set(date);
    this.datePreset.set('ALL');
    this.currentPage.set(0);
  }

  onEndDateChange(date: string) {
    this.endDate.set(date);
    this.datePreset.set('ALL');
    this.currentPage.set(0);
  }

  applyDatePreset(preset: 'ALL' | 'THIS_WEEK' | 'THIS_MONTH') {
    this.datePreset.set(preset);
    this.currentPage.set(0);

    if (preset === 'ALL') {
      this.startDate.set('');
      this.endDate.set('');
      return;
    }

    const formatDate = (d: Date) => {
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };

    const now = new Date();
    if (preset === 'THIS_WEEK') {
      const day = now.getDay();
      const diffToMonday = now.getDate() - day + (day === 0 ? -6 : 1);
      const monday = new Date(now.getFullYear(), now.getMonth(), diffToMonday);
      const sunday = new Date(now.getFullYear(), now.getMonth(), diffToMonday + 6);
      this.startDate.set(formatDate(monday));
      this.endDate.set(formatDate(sunday));
    } else if (preset === 'THIS_MONTH') {
      const y = now.getFullYear();
      const m = now.getMonth();
      const firstDay = new Date(y, m, 1);
      const lastDay = new Date(y, m + 1, 0);
      this.startDate.set(formatDate(firstDay));
      this.endDate.set(formatDate(lastDay));
    }
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
    }
  }

  nextPage() {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(p => p + 1);
    }
  }

  prevPage() {
    if (this.currentPage() > 0) {
      this.currentPage.update(p => p - 1);
    }
  }

  onPageSizeChange(size: number) {
    this.pageSize.set(size);
    this.currentPage.set(0);
  }

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
      const request = (training.rpe || training.notes) ? {
        rpe: training.rpe,
        notes: training.notes
      } : undefined;

      const call$ = request
        ? this.trainingService.completeTraining(training.id, request)
        : this.trainingService.completeTraining(training.id);

      call$.subscribe({
        next: () => {
          this.trainingService.trainingsResource.reload();
          this.isActiveTrackerOpen.set(false);
          this.activeWorkout.set(null);
          this.notify(`🎉 Workout "${training.name}" marked as Completed!`);
          this.openSummaryModal(training.id!);
        },
        error: () => {
          this.trainingService.trainingsResource.reload();
          this.isActiveTrackerOpen.set(false);
          this.activeWorkout.set(null);
          this.notify(`🎉 Workout "${training.name}" marked as Completed!`);
        }
      });
    }
  }

  handleCompleteWithDetails(details: { training: Training; rpe?: number; notes?: string }) {
    const trainingWithDetails: Training = {
      ...details.training,
      notes: details.notes,
      rpe: details.rpe !== undefined ? { value: details.rpe } : undefined
    };
    this.handleComplete(trainingWithDetails);
  }

  handleViewSummary(training: Training) {
    if (training.id) {
      this.openSummaryModal(training.id);
    }
  }

  openSummaryModal(trainingId: number) {
    this.summaryWorkoutId.set(trainingId);
    this.isSummaryOpen.set(true);
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
    this.isSummaryOpen.set(false);
    this.summaryWorkoutId.set(null);
  }

  private notify(msg: string) {
    this.notificationMessage.set(msg);
    setTimeout(() => this.notificationMessage.set(''), 4000);
  }
}
