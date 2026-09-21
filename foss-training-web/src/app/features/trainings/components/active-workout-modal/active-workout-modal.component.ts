import { Component, input, output, signal, computed, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TrainingService } from '../../../../core/services/training.service';
import type {
  Training,
  ResistanceSessionExercise,
  ResistanceSet
} from '../../../../core/api/models';

export interface EditableSet {
  setNumber: number;
  setType?: 'WARMUP' | 'WORKING' | 'DROP_SET' | 'MYOREP' | 'FAILURE';
  reps: number;
  weight: number;
  rpe?: number;
  restSeconds?: number;
  completed: boolean;
}

export interface EditableExercise {
  id?: number;
  exerciseId?: number;
  name: string;
  category?: string;
  sets: EditableSet[];
}

@Component({
  selector: 'app-active-workout-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './active-workout-modal.component.html',
  styleUrl: './active-workout-modal.component.scss'
})
export class ActiveWorkoutModalComponent implements OnInit, OnDestroy {
  private readonly trainingService = inject(TrainingService);

  readonly training = input.required<Training>();

  readonly close = output<void>();
  readonly finishWorkout = output<Training>();
  readonly completeWithDetails = output<{ training: Training; rpe?: number; notes?: string }>();
  readonly cancelWorkout = output<Training>();
  readonly saveProgress = output<Training>();
  readonly pauseWorkoutOutput = output<Training>();
  readonly resumeWorkoutOutput = output<Training>();

  readonly editableExercises = signal<EditableExercise[]>([]);
  readonly currentStatus = signal<string>('In Progress');

  // Live Timer
  readonly elapsedSeconds = signal<number>(0);
  private timerInterval: any = null;

  // Rest Timer
  readonly restTimerSeconds = signal<number | null>(null);
  readonly restTimerInitial = signal<number>(90);
  private restInterval: any = null;

  // Post-workout rating & notes
  readonly sessionRpe = signal<number>(8.0);
  readonly sessionNotes = signal<string>('');

  readonly isPaused = computed(() => this.currentStatus() === 'Paused');

  readonly formattedTimer = computed(() => {
    const totalSecs = this.elapsedSeconds();
    const hours = Math.floor(totalSecs / 3600);
    const mins = Math.floor((totalSecs % 3600) / 60);
    const secs = totalSecs % 60;
    if (hours > 0) {
      return `${String(hours).padStart(2, '0')}:${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
    }
    return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  });

  readonly completedSetsCount = computed(() => {
    return this.editableExercises().reduce((acc, ex) => {
      return acc + ex.sets.filter(s => s.completed).length;
    }, 0);
  });

  readonly totalSetsCount = computed(() => {
    return this.editableExercises().reduce((acc, ex) => acc + ex.sets.length, 0);
  });

  readonly currentVolume = computed(() => {
    return this.editableExercises().reduce((acc, ex) => {
      const exVol = ex.sets
        .filter(s => s.completed)
        .reduce((sAcc, s) => sAcc + (s.reps || 0) * (s.weight || 0), 0);
      return acc + exVol;
    }, 0);
  });

  readonly rpeDescriptor = computed(() => {
    const val = this.sessionRpe();
    if (val <= 4) return 'Very Light Effort (Warmup pace)';
    if (val <= 6) return 'Moderate Effort (Target heart rate)';
    if (val <= 7.5) return 'Challenging (3-4 reps in reserve)';
    if (val <= 8.5) return 'Hard Effort (1-2 reps in reserve)';
    if (val <= 9.5) return 'Very Hard Effort (Near failure)';
    return 'Maximum Effort (Complete exhaustion)';
  });

  ngOnInit() {
    this.currentStatus.set(this.training().status || 'In Progress');
    if (this.training().notes) {
      this.sessionNotes.set(this.training().notes || '');
    }
    if (this.training().rpe?.value) {
      this.sessionRpe.set(this.training().rpe!.value!);
    }
    this.initExercises();
    this.initTimer();
  }

  ngOnDestroy() {
    this.stopTimer();
    this.stopRestTimer();
  }

  private initTimer() {
    if (this.training().startTime) {
      const startMs = new Date(this.training().startTime!).getTime();
      const nowMs = Date.now();
      const diffSecs = Math.max(0, Math.floor((nowMs - startMs) / 1000));
      this.elapsedSeconds.set(diffSecs);
    }

    if (this.currentStatus() === 'In Progress') {
      this.startTimer();
    }
  }

  private startTimer() {
    this.stopTimer();
    this.timerInterval = setInterval(() => {
      this.elapsedSeconds.update(s => s + 1);
    }, 1000);
  }

  private stopTimer() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  private initExercises() {
    const session = this.training().session;
    if (!session?.sessionExercises) return;

    const list: EditableExercise[] = [];
    for (const se of session.sessionExercises) {
      if (se.exerciseType === 'ResistanceSessionExercise' || (se.exerciseType as string) === 'resistance') {
        const rse = se as ResistanceSessionExercise;
        const sets: EditableSet[] = (rse.sets || []).map((s, idx) => ({
          setNumber: s.setNumber || idx + 1,
          setType: s.setType,
          reps: s.repetitions || 0,
          weight: s.weight?.value || 0,
          rpe: s.rpe?.value,
          restSeconds: s.restSeconds || 90,
          completed: !!s.completed
        }));
        list.push({
          id: rse.id,
          exerciseId: rse.exercise?.id || rse.id,
          name: rse.exercise?.name || 'Exercise',
          category: rse.exercise?.primaryCategory || 'RESISTANCE',
          sets
        });
      }
    }
    this.editableExercises.set(list);
  }

  isSetCompleted(exerciseId: number | undefined, setNumber: number): boolean {
    const ex = this.editableExercises().find(e => e.id === exerciseId || e.exerciseId === exerciseId);
    if (!ex) return false;
    const s = ex.sets.find(st => st.setNumber === setNumber);
    return !!s?.completed;
  }

  toggleSet(exIndex: number, setIndex: number) {
    let newlyCompleted = false;
    let targetRest = 90;

    this.editableExercises.update(exercises => {
      const updated = [...exercises];
      const ex = { ...updated[exIndex] };
      const sets = [...ex.sets];
      const current = sets[setIndex];
      const nextCompleted = !current.completed;
      newlyCompleted = nextCompleted;
      targetRest = current.restSeconds || 90;

      sets[setIndex] = {
        ...current,
        completed: nextCompleted
      };
      ex.sets = sets;
      updated[exIndex] = ex;
      return updated;
    });

    const ex = this.editableExercises()[exIndex];
    const s = ex.sets[setIndex];

    // Trigger Rest Timer when completing a set
    if (newlyCompleted) {
      this.startRestTimer(targetRest);
    }

    this.syncSetToBackend(ex, s);
  }

  updateSetValues(exIndex: number, setIndex: number) {
    const ex = this.editableExercises()[exIndex];
    const s = ex.sets[setIndex];
    this.syncSetToBackend(ex, s);
  }

  addSet(exIndex: number) {
    this.editableExercises.update(exercises => {
      const updated = [...exercises];
      const ex = { ...updated[exIndex] };
      const sets = [...ex.sets];

      const lastSet = sets[sets.length - 1];
      const nextNum = sets.length + 1;
      const newSet: EditableSet = {
        setNumber: nextNum,
        setType: 'WORKING',
        reps: lastSet ? lastSet.reps : 10,
        weight: lastSet ? lastSet.weight : 50,
        rpe: lastSet ? lastSet.rpe : 8.0,
        restSeconds: lastSet?.restSeconds || 90,
        completed: false
      };

      sets.push(newSet);
      ex.sets = sets;
      updated[exIndex] = ex;

      this.syncSetToBackend(ex, newSet);
      return updated;
    });
  }

  deleteSet(exIndex: number, setIndex: number) {
    const ex = this.editableExercises()[exIndex];
    const targetSetNumber = ex.sets[setIndex].setNumber;

    this.editableExercises.update(exercises => {
      const updated = [...exercises];
      const exUpdated = { ...updated[exIndex] };
      const sets = exUpdated.sets.filter((_, idx) => idx !== setIndex);
      // Renumber
      exUpdated.sets = sets.map((s, idx) => ({ ...s, setNumber: idx + 1 }));
      updated[exIndex] = exUpdated;
      return updated;
    });

    const trainingId = this.training().id;
    const exerciseId = ex.exerciseId || ex.id;
    if (trainingId && exerciseId) {
      this.trainingService.deleteSet(trainingId, exerciseId, targetSetNumber).subscribe({
        next: (t) => this.saveProgress.emit(t),
        error: (err) => console.warn('Failed to delete set on backend', err)
      });
    }
  }

  private syncSetToBackend(ex: EditableExercise, s: EditableSet) {
    const trainingId = this.training().id;
    const exerciseId = ex.exerciseId || ex.id;
    if (!trainingId || !exerciseId) return;

    this.trainingService.logSet(trainingId, exerciseId, {
      setNumber: s.setNumber,
      setType: s.setType,
      weight: { value: s.weight, unit: 'KG' },
      repetitions: s.reps,
      rpe: s.rpe ? { value: s.rpe } : undefined,
      restSeconds: s.restSeconds,
      completed: s.completed
    }).subscribe({
      next: (updatedTraining) => {
        this.saveProgress.emit(updatedTraining);
      },
      error: (err) => console.warn('Failed to sync set to backend', err)
    });
  }

  // Rest Timer logic
  startRestTimer(seconds: number) {
    this.stopRestTimer();
    this.restTimerInitial.set(seconds);
    this.restTimerSeconds.set(seconds);

    this.restInterval = setInterval(() => {
      this.restTimerSeconds.update(s => {
        if (s === null || s <= 1) {
          this.stopRestTimer();
          return null;
        }
        return s - 1;
      });
    }, 1000);
  }

  addRestTime(seconds: number) {
    this.restTimerSeconds.update(s => (s ?? 0) + seconds);
    this.restTimerInitial.update(init => init + seconds);
  }

  stopRestTimer() {
    if (this.restInterval) {
      clearInterval(this.restInterval);
      this.restInterval = null;
    }
    this.restTimerSeconds.set(null);
  }

  // Lifecycle transitions
  togglePause() {
    const id = this.training().id;
    if (!id) return;

    if (this.isPaused()) {
      this.trainingService.resumeTraining(id).subscribe({
        next: (t) => {
          this.currentStatus.set('In Progress');
          this.startTimer();
          this.resumeWorkoutOutput.emit(t);
        },
        error: (err) => console.warn('Failed to resume workout', err)
      });
    } else {
      this.trainingService.pauseTraining(id).subscribe({
        next: (t) => {
          this.currentStatus.set('Paused');
          this.stopTimer();
          this.pauseWorkoutOutput.emit(t);
        },
        error: (err) => console.warn('Failed to pause workout', err)
      });
    }
  }

  onFinish() {
    const updatedTraining: Training = {
      ...this.training(),
      notes: this.sessionNotes(),
      rpe: { value: this.sessionRpe() }
    };
    this.finishWorkout.emit(updatedTraining);
    this.completeWithDetails.emit({
      training: this.training(),
      rpe: this.sessionRpe(),
      notes: this.sessionNotes()
    });
  }

  onCancel() {
    this.cancelWorkout.emit(this.training());
  }

  onClose() {
    this.close.emit();
  }
}
