import { Component, input, output, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import type {
  Training,
  ResistanceSessionExercise,
  ResistanceSet
} from '../../../../core/api/models';

interface EditableSet {
  setNumber: number;
  setType?: 'WARMUP' | 'WORKING' | 'DROP_SET' | 'MYOREP' | 'FAILURE';
  reps: number;
  weight: number;
  completed: boolean;
}

interface EditableExercise {
  id?: number;
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
export class ActiveWorkoutModalComponent {
  readonly training = input.required<Training>();

  readonly close = output<void>();
  readonly finishWorkout = output<Training>();
  readonly cancelWorkout = output<Training>();
  readonly saveProgress = output<Training>();

  readonly editableExercises = signal<EditableExercise[]>([]);

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

  ngOnInit() {
    this.initExercises();
  }

  private initExercises() {
    const session = this.training().session;
    if (!session?.sessionExercises) return;

    const list: EditableExercise[] = [];
    for (const se of session.sessionExercises) {
      if (se.exerciseType === 'ResistanceSessionExercise' || se.exerciseType === 'resistance') {
        const rse = se as ResistanceSessionExercise;
        const sets: EditableSet[] = (rse.sets || []).map((s, idx) => ({
          setNumber: s.setNumber || idx + 1,
          setType: s.setType,
          reps: s.repetitions || 0,
          weight: s.weight?.value || 0,
          completed: false
        }));
        list.push({
          id: rse.id,
          name: rse.exercise?.name || 'Exercise',
          category: rse.exercise?.primaryCategory || 'RESISTANCE',
          sets
        });
      }
    }
    this.editableExercises.set(list);
  }

  isSetCompleted(exerciseId: number | undefined, setNumber: number): boolean {
    const ex = this.editableExercises().find(e => e.id === exerciseId);
    if (!ex) return false;
    const s = ex.sets.find(st => st.setNumber === setNumber);
    return !!s?.completed;
  }

  toggleSet(exIndex: number, setIndex: number) {
    this.editableExercises.update(exercises => {
      const updated = [...exercises];
      const ex = { ...updated[exIndex] };
      const sets = [...ex.sets];
      sets[setIndex] = {
        ...sets[setIndex],
        completed: !sets[setIndex].completed
      };
      ex.sets = sets;
      updated[exIndex] = ex;
      return updated;
    });
  }

  onFinish() {
    this.finishWorkout.emit(this.training());
  }

  onCancel() {
    this.cancelWorkout.emit(this.training());
  }

  onClose() {
    this.close.emit();
  }
}
