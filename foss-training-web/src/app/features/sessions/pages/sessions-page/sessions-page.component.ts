import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SessionService } from '../../../../core/services/session.service';
import { ExerciseService } from '../../../../core/services/exercise.service';
import { SessionCardComponent } from '../../components/session-card/session-card.component';
import { SessionDetailModalComponent } from '../../components/session-detail-modal/session-detail-modal.component';
import { SessionBuilderModalComponent } from '../../components/session-builder-modal/session-builder-modal.component';
import { ConfirmDialogComponent } from '../../../../shared/ui/confirm-dialog/confirm-dialog.component';
import type { Session, SessionRequest, Exercise } from '../../../../core/api/models';

@Component({
  selector: 'app-sessions-page',
  standalone: true,
  imports: [
    FormsModule,
    SessionCardComponent,
    SessionDetailModalComponent,
    SessionBuilderModalComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './sessions-page.component.html',
  styleUrl: './sessions-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionsPageComponent {
  private readonly sessionService = inject(SessionService);
  private readonly exerciseService = inject(ExerciseService);

  readonly sessions = this.sessionService.sessionsResource.value;
  readonly isLoading = this.sessionService.sessionsResource.isLoading;
  readonly error = this.sessionService.sessionsResource.error;

  readonly availableExercises = this.exerciseService.exercisesResource.value;

  // Filter Signals
  readonly searchQuery = signal('');
  readonly selectedStatus = signal('ALL');

  // Modal Signals
  readonly isBuilderOpen = signal(false);
  readonly editingSession = signal<Session | null>(null);

  readonly isDetailOpen = signal(false);
  readonly activeSession = signal<Session | null>(null);

  readonly isDeleteConfirmOpen = signal(false);
  readonly sessionToDelete = signal<Session | null>(null);

  readonly notificationMessage = signal('');

  readonly statuses = [
    'ALL',
    'Planned',
    'Draft',
    'Ready to Start',
    'In Progress',
    'Completed'
  ];

  readonly filteredSessions = computed(() => {
    const list = this.sessions() ?? [];
    const query = this.searchQuery().trim().toLowerCase();
    const st = this.selectedStatus();

    return list.filter(item => {
      const matchesSearch =
        !query ||
        (item.name && item.name.toLowerCase().includes(query)) ||
        (item.description && item.description.toLowerCase().includes(query)) ||
        (item.sessionExercises?.some(se => se.exercise?.name?.toLowerCase().includes(query)));

      const statusStr = (item.sessionStatus as string)?.toUpperCase();
      const matchesStatus =
        st === 'ALL' ||
        item.sessionStatus === st ||
        statusStr === st.toUpperCase() ||
        (st === 'Planned' && statusStr === 'PLANNED') ||
        (st === 'Draft' && statusStr === 'DRAFT') ||
        (st === 'Ready to Start' && (statusStr === 'READY' || statusStr === 'READY_TO_START')) ||
        (st === 'In Progress' && statusStr === 'IN_PROGRESS') ||
        (st === 'Completed' && statusStr === 'COMPLETED');

      return matchesSearch && matchesStatus;
    });
  });

  openCreateModal() {
    this.editingSession.set(null);
    this.isBuilderOpen.set(true);
  }

  openEditModal(session: Session) {
    this.editingSession.set(session);
    this.isDetailOpen.set(false);
    this.isBuilderOpen.set(true);
  }

  openDetailModal(session: Session) {
    this.activeSession.set(session);
    this.isDetailOpen.set(true);
  }

  openDeleteConfirm(session: Session) {
    this.sessionToDelete.set(session);
    this.isDeleteConfirmOpen.set(true);
  }

  closeModals() {
    this.isBuilderOpen.set(false);
    this.editingSession.set(null);
    this.isDetailOpen.set(false);
    this.activeSession.set(null);
    this.isDeleteConfirmOpen.set(false);
    this.sessionToDelete.set(null);
  }

  confirmDelete() {
    const target = this.sessionToDelete();
    if (target?.id) {
      this.sessionService.deleteSession(target.id).subscribe({
        next: () => {
          this.sessionService.sessionsResource.reload();
          this.closeModals();
        }
      });
    }
  }

  handleSave(request: SessionRequest) {
    const isEdit = !!request.id;
    const action$ = isEdit
      ? this.sessionService.updateSession(request.id!, request)
      : this.sessionService.createSession(request);

    action$.subscribe({
      next: () => {
        this.sessionService.sessionsResource.reload();
        this.closeModals();
      }
    });
  }

  handleStartTraining(session: Session) {
    if (!session.id) return;
    this.sessionService.startTrainingFromSession(session.id).subscribe({
      next: (training) => {
        this.notificationMessage.set(`⚡ Workout "${session.name}" started! Added to Trainings log.`);
        setTimeout(() => this.notificationMessage.set(''), 4000);
      },
      error: () => {
        this.notificationMessage.set(`⚡ Workout "${session.name}" started!`);
        setTimeout(() => this.notificationMessage.set(''), 4000);
      }
    });
  }

  handleClone(session: Session) {
    if (!session.id) return;
    this.sessionService.cloneSession(session.id).subscribe({
      next: (cloned) => {
        this.sessionService.sessionsResource.reload();
        this.closeModals();
        this.notificationMessage.set(`📋 Routine template "${session.name}" duplicated as "${cloned.name}"!`);
        setTimeout(() => this.notificationMessage.set(''), 4000);
      },
      error: () => {
        this.notificationMessage.set(`Failed to duplicate routine template.`);
        setTimeout(() => this.notificationMessage.set(''), 4000);
      }
    });
  }
}
