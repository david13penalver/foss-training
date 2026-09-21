import { Component, ChangeDetectionStrategy, input, output, signal, effect, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SessionService } from '../../../../core/services/session.service';
import type {
  PeriodizationType,
  ProgramLevel,
  ProgramWorkoutRequest,
  Session,
  TrainingProgram,
  TrainingProgramRequest
} from '../../../../core/api/models';

interface WorkoutDayItem {
  dayOfWeek: number;
  focus: string;
  sessionId: number | null;
}

@Component({
  selector: 'app-program-builder-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './program-builder-modal.component.html',
  styleUrl: './program-builder-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgramBuilderModalComponent {
  readonly sessionService = inject(SessionService);

  readonly program = input<TrainingProgram | null>(null);

  readonly save = output<TrainingProgramRequest>();
  readonly closed = output<void>();

  readonly name = signal('');
  readonly description = signal('');
  readonly durationWeeks = signal(12);
  readonly periodizationType = signal<PeriodizationType>('LINEAR');
  readonly level = signal<ProgramLevel>('INTERMEDIATE');

  readonly workouts = signal<WorkoutDayItem[]>([]);
  readonly isEditMode = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly periodizationTypes: PeriodizationType[] = [
    'LINEAR',
    'BLOCK',
    'UNDULATING',
    'REVERSE_LINEAR'
  ];

  readonly programLevels: ProgramLevel[] = [
    'BEGINNER',
    'INTERMEDIATE',
    'ADVANCED',
    'ELITE'
  ];

  readonly daysOfWeek = [
    { value: 1, label: 'Monday' },
    { value: 2, label: 'Tuesday' },
    { value: 3, label: 'Wednesday' },
    { value: 4, label: 'Thursday' },
    { value: 5, label: 'Friday' },
    { value: 6, label: 'Saturday' },
    { value: 7, label: 'Sunday' }
  ];

  constructor() {
    effect(() => {
      const p = this.program();
      if (p) {
        this.isEditMode.set(true);
        this.name.set(p.name || '');
        this.description.set(p.description || '');
        this.durationWeeks.set(p.durationWeeks || 12);
        this.periodizationType.set(p.periodizationType || 'LINEAR');
        this.level.set(p.level || 'INTERMEDIATE');

        const items: WorkoutDayItem[] = (p.workouts || []).map(w => ({
          dayOfWeek: w.dayOfWeek || 1,
          focus: w.focus || '',
          sessionId: w.session?.id || null
        }));
        this.workouts.set(items);
      } else {
        this.isEditMode.set(false);
        this.name.set('');
        this.description.set('');
        this.durationWeeks.set(12);
        this.periodizationType.set('LINEAR');
        this.level.set('INTERMEDIATE');
        this.workouts.set([]);
      }
    });
  }

  addWorkoutDay() {
    const current = this.workouts();
    const nextDay = Math.min(7, (current.length ? current[current.length - 1].dayOfWeek + 2 : 1));
    this.workouts.set([...current, { dayOfWeek: nextDay <= 7 ? nextDay : 1, focus: '', sessionId: null }]);
  }

  removeWorkoutDay(index: number) {
    const current = this.workouts();
    this.workouts.set(current.filter((_, i) => i !== index));
  }

  updateWorkoutDay(index: number, field: keyof WorkoutDayItem, value: any) {
    const current = [...this.workouts()];
    current[index] = { ...current[index], [field]: value };
    this.workouts.set(current);
  }

  submit() {
    if (!this.name().trim()) {
      this.errorMessage.set('Program name is required.');
      return;
    }

    if (this.durationWeeks() < 1) {
      this.errorMessage.set('Duration must be at least 1 week.');
      return;
    }

    const availableSessions = this.sessionService.sessionsResource.value() || [];

    const builtWorkouts: ProgramWorkoutRequest[] = this.workouts().map(w => {
      const foundSession = availableSessions.find(s => s.id === Number(w.sessionId));
      return {
        dayOfWeek: Number(w.dayOfWeek),
        focus: w.focus || undefined,
        session: {
          id: foundSession?.id,
          name: foundSession?.name || `Workout Day ${w.dayOfWeek}`,
          sessionExercises: foundSession?.sessionExercises as any,
          sessionStatus: 'Planned'
        }
      };
    });

    const request: TrainingProgramRequest = {
      id: this.isEditMode() && this.program()?.id ? this.program()!.id : undefined,
      name: this.name().trim(),
      description: this.description().trim() || undefined,
      durationWeeks: this.durationWeeks(),
      periodizationType: this.periodizationType(),
      level: this.level(),
      workouts: builtWorkouts,
      isActive: true
    };

    this.save.emit(request);
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.closed.emit();
    }
  }
}
