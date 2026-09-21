import {
  Component,
  ChangeDetectionStrategy,
  OnInit,
  input,
  output,
  signal,
  computed,
  inject
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProgramService } from '../../../../core/services/program.service';
import type {
  ProgramAdherenceResponse,
  TrainingProgram,
  WeeklyAdherence,
  WorkoutAdherenceItem
} from '../../../../core/api/models';

@Component({
  selector: 'app-program-adherence-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './program-adherence-modal.component.html',
  styleUrl: './program-adherence-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgramAdherenceModalComponent implements OnInit {
  private readonly programService = inject(ProgramService);

  readonly program = input.required<TrainingProgram>();
  readonly closed = output<void>();

  readonly adherence = signal<ProgramAdherenceResponse | null>(null);
  readonly isLoading = signal<boolean>(true);
  readonly errorMessage = signal<string | null>(null);
  readonly activeTab = signal<'overview' | 'weeks' | 'workouts'>('overview');

  readonly hasAdherenceData = computed(() => !!this.adherence());

  ngOnInit(): void {
    this.loadAdherence();
  }

  loadAdherence(): void {
    const progId = this.program().id;
    if (!progId) {
      this.errorMessage.set('Invalid program identifier.');
      this.isLoading.set(false);
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.programService.getProgramAdherence(progId).subscribe({
      next: (data) => {
        this.adherence.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage.set('Failed to load program adherence metrics.');
        this.isLoading.set(false);
      }
    });
  }

  getStatusBadgeClass(status?: string): string {
    switch (status) {
      case 'ON_TRACK':
        return 'badge-on-track';
      case 'BEHIND_SCHEDULE':
        return 'badge-behind-schedule';
      case 'AT_RISK':
        return 'badge-at-risk';
      case 'COMPLETED':
        return 'badge-completed';
      case 'NOT_STARTED':
      default:
        return 'badge-not-started';
    }
  }

  getStatusIcon(status?: string): string {
    switch (status) {
      case 'ON_TRACK':
        return '🟢';
      case 'BEHIND_SCHEDULE':
        return '🟠';
      case 'AT_RISK':
        return '🔴';
      case 'COMPLETED':
        return '🏆';
      case 'NOT_STARTED':
      default:
        return '⚪';
    }
  }

  getWorkoutStatusClass(status?: string): string {
    switch (status?.toUpperCase()) {
      case 'COMPLETED':
        return 'status-completed';
      case 'IN_PROGRESS':
        return 'status-in-progress';
      case 'PLANNED':
        return 'status-planned';
      case 'CANCELLED':
        return 'status-cancelled';
      case 'MISSED':
      case 'OVERDUE':
        return 'status-missed';
      default:
        return 'status-default';
    }
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.closed.emit();
    }
  }
}
