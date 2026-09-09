import { Component, ChangeDetectionStrategy, input, output, signal, effect, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import type { Session, SessionRequest, Exercise, ResistanceSessionExercise } from '../../../../core/api/models';

type SessionStatus = NonNullable<SessionRequest['sessionStatus']>;
type SetType = 'WARMUP' | 'WORKING' | 'DROP_SET' | 'MYOREP' | 'FAILURE';

export interface EditableSet {
  setNumber: number;
  setType: SetType;
  weight: number | null;
  repetitions: number | null;
  rpe: number | null;
  restSeconds: number | null;
}

export interface EditableSessionExercise {
  id?: number;
  exercise: Exercise;
  notes?: string;
  sets: EditableSet[];
}

@Component({
  selector: 'app-session-builder-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './session-builder-modal.component.html',
  styleUrl: './session-builder-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionBuilderModalComponent {
  readonly session = input<Session | null>(null);
  readonly availableExercises = input<Exercise[]>([]);

  readonly save = output<SessionRequest>();
  readonly closed = output<void>();

  // Form Signals
  readonly name = signal('');
  readonly description = signal('');
  readonly sessionStatus = signal<SessionStatus>('Planned');
  readonly notes = signal('');
  readonly selectedExercises = signal<EditableSessionExercise[]>([]);

  // Picker Signals
  readonly pickerSearchQuery = signal('');
  readonly isPickerOpen = signal(false);
  readonly errorMessage = signal('');

  readonly isEditMode = signal(false);

  readonly availableFilteredExercises = computed(() => {
    const list = this.availableExercises() ?? [];
    const q = this.pickerSearchQuery().trim().toLowerCase();
    if (!q) return list;
    return list.filter(e =>
      (e.name && e.name.toLowerCase().includes(q)) ||
      (e.primaryCategory && e.primaryCategory.toLowerCase().includes(q))
    );
  });

  readonly statusOptions: SessionStatus[] = [
    'Planned',
    'Draft',
    'Ready to Start',
    'In Progress',
    'Paused',
    'Completed'
  ];

  readonly setTypeOptions: SetType[] = [
    'WORKING',
    'WARMUP',
    'DROP_SET',
    'MYOREP',
    'FAILURE'
  ];

  constructor() {
    effect(() => {
      const s = this.session();
      if (s) {
        this.isEditMode.set(true);
        this.name.set(s.name ?? '');
        this.description.set(s.description ?? '');
        this.sessionStatus.set(s.sessionStatus ?? 'Planned');
        this.notes.set(s.notes ?? '');

        const mappedExercises: EditableSessionExercise[] = (s.sessionExercises ?? []).map(se => {
          const res = se as ResistanceSessionExercise;
          const sets: EditableSet[] = (res.sets ?? []).map((set, idx) => ({
            setNumber: set.setNumber ?? idx + 1,
            setType: (set.setType as SetType) ?? 'WORKING',
            weight: set.weight?.value ?? null,
            repetitions: set.repetitions ?? null,
            rpe: set.rpe?.value ?? null,
            restSeconds: set.restSeconds ?? null
          }));

          return {
            id: se.id,
            exercise: se.exercise!,
            notes: se.notes,
            sets: sets.length > 0 ? sets : [{
              setNumber: 1,
              setType: 'WORKING',
              weight: 0,
              repetitions: 10,
              rpe: 8,
              restSeconds: 90
            }]
          };
        });

        this.selectedExercises.set(mappedExercises);
      } else {
        this.isEditMode.set(false);
      }
    });
  }

  addExercise(exercise: Exercise) {
    const curr = this.selectedExercises();
    const newEx: EditableSessionExercise = {
      exercise,
      sets: [
        {
          setNumber: 1,
          setType: 'WORKING',
          weight: exercise.resistanceMetrics?.minWeight ?? null,
          repetitions: exercise.resistanceMetrics?.recommendedRepsMin ?? 10,
          rpe: 8,
          restSeconds: exercise.resistanceMetrics?.recommendedRestSeconds ?? 90
        }
      ]
    };
    this.selectedExercises.set([...curr, newEx]);
    this.isPickerOpen.set(false);
    this.pickerSearchQuery.set('');
  }

  removeExercise(index: number) {
    const curr = [...this.selectedExercises()];
    curr.splice(index, 1);
    this.selectedExercises.set(curr);
  }

  moveExercise(index: number, direction: 'up' | 'down') {
    const curr = [...this.selectedExercises()];
    const targetIdx = direction === 'up' ? index - 1 : index + 1;
    if (targetIdx < 0 || targetIdx >= curr.length) return;
    const temp = curr[index];
    curr[index] = curr[targetIdx];
    curr[targetIdx] = temp;
    this.selectedExercises.set(curr);
  }

  addSet(exerciseIndex: number) {
    const curr = [...this.selectedExercises()];
    const ex = curr[exerciseIndex];
    const prevSet = ex.sets[ex.sets.length - 1];

    const nextSet: EditableSet = {
      setNumber: ex.sets.length + 1,
      setType: prevSet?.setType ?? 'WORKING',
      weight: prevSet?.weight ?? null,
      repetitions: prevSet?.repetitions ?? 10,
      rpe: prevSet?.rpe ?? 8,
      restSeconds: prevSet?.restSeconds ?? 90
    };

    ex.sets = [...ex.sets, nextSet];
    this.selectedExercises.set(curr);
  }

  removeSet(exerciseIndex: number, setIndex: number) {
    const curr = [...this.selectedExercises()];
    const ex = curr[exerciseIndex];
    if (ex.sets.length <= 1) return; // Keep at least 1 set
    ex.sets = ex.sets.filter((_, idx) => idx !== setIndex).map((s, idx) => ({
      ...s,
      setNumber: idx + 1
    }));
    this.selectedExercises.set(curr);
  }

  onSubmit() {
    const trimmedName = this.name().trim();
    if (!trimmedName) {
      this.errorMessage.set('Routine name is required.');
      return;
    }

    const sessionExercises = this.selectedExercises().map((ex, orderIdx) => {
      return {
        exerciseType: 'ResistanceSessionExercise' as const,
        id: ex.id,
        orderIndex: orderIdx + 1,
        notes: ex.notes,
        exercise: ex.exercise,
        sets: ex.sets.map((s, sIdx) => ({
          setNumber: sIdx + 1,
          setType: s.setType,
          repetitions: s.repetitions ?? undefined,
          weight: s.weight !== null ? { value: s.weight, unit: 'KG' as const } : undefined,
          rpe: s.rpe !== null ? { value: s.rpe } : undefined,
          restSeconds: s.restSeconds ?? undefined
        }))
      };
    });

    const request: SessionRequest = {
      id: this.session()?.id,
      name: trimmedName,
      description: this.description().trim() || undefined,
      sessionStatus: this.sessionStatus(),
      notes: this.notes().trim() || undefined,
      sessionExercises: sessionExercises as any
    };

    this.save.emit(request);
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.closed.emit();
    }
  }
}
