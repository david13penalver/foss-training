import { Component, ChangeDetectionStrategy, input, output, signal, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';
import type { Exercise, ExerciseRequest } from '../../../../core/api/models';

type PrimaryCategory = ExerciseRequest['primaryCategory'];
type DifficultyLevel = NonNullable<ExerciseRequest['difficultyLevel']>;

@Component({
  selector: 'app-exercise-form-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './exercise-form-modal.component.html',
  styleUrl: './exercise-form-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExerciseFormModalComponent {
  readonly exercise = input<Exercise | null>(null);
  readonly availableEquipment = input<string[]>([]);
  readonly availableMuscles = input<string[]>([]);

  readonly save = output<ExerciseRequest>();
  readonly closed = output<void>();

  // Form Field Signals
  readonly name = signal('');
  readonly description = signal('');
  readonly primaryCategory = signal<PrimaryCategory>('RESISTANCE');
  readonly difficultyLevel = signal<DifficultyLevel>('INTERMEDIATE');
  readonly movementPattern = signal<string>('PUSH');
  readonly selectedEquipment = signal<string[]>([]);
  readonly selectedPrimaryMuscles = signal<string[]>([]);
  readonly instructionsText = signal('');
  readonly safetyTipsText = signal('');

  // Metrics Signals
  readonly sets = signal<number | null>(3);
  readonly repsMin = signal<number | null>(8);
  readonly repsMax = signal<number | null>(12);
  readonly restSeconds = signal<number | null>(90);

  readonly isEditMode = signal(false);
  readonly errorMessage = signal('');

  readonly categories: PrimaryCategory[] = [
    'RESISTANCE',
    'ENDURANCE',
    'MOBILITY',
    'FUNCTIONAL',
    'CALISTHENICS',
    'PLYOMETRICS',
    'CORE'
  ];

  readonly difficultyLevels: DifficultyLevel[] = [
    'BEGINNER',
    'INTERMEDIATE',
    'ADVANCED',
    'EXPERT'
  ];

  readonly movementPatterns = [
    'PUSH',
    'PULL',
    'SQUAT',
    'HINGE',
    'LUNGE',
    'CARRY',
    'ROTATION',
    'ISOLATION',
    'COMPOUND'
  ];

  constructor() {
    effect(() => {
      const ex = this.exercise();
      if (ex) {
        this.isEditMode.set(true);
        this.name.set(ex.name ?? '');
        this.description.set(ex.description ?? '');
        this.primaryCategory.set(ex.primaryCategory ?? 'RESISTANCE');
        this.difficultyLevel.set(ex.difficultyLevel ?? 'INTERMEDIATE');
        this.selectedEquipment.set(ex.equipmentRequired ? [...ex.equipmentRequired] : []);

        const rm = ex.resistanceMetrics;
        if (rm) {
          this.movementPattern.set(rm.movementPattern ?? 'PUSH');
          this.selectedPrimaryMuscles.set(rm.primaryMuscles ? [...rm.primaryMuscles] : []);
          this.sets.set(rm.recommendedSets ?? null);
          this.repsMin.set(rm.recommendedRepsMin ?? null);
          this.repsMax.set(rm.recommendedRepsMax ?? null);
          this.restSeconds.set(rm.recommendedRestSeconds ?? null);
        }

        this.instructionsText.set((ex.stepByStepInstructions ?? []).join('\n'));
        this.safetyTipsText.set((ex.safetyTips ?? []).join('\n'));
      } else {
        this.isEditMode.set(false);
      }
    });
  }

  toggleEquipment(equip: string) {
    const curr = this.selectedEquipment();
    if (curr.includes(equip)) {
      this.selectedEquipment.set(curr.filter(e => e !== equip));
    } else {
      this.selectedEquipment.set([...curr, equip]);
    }
  }

  toggleMuscle(muscle: string) {
    const curr = this.selectedPrimaryMuscles();
    if (curr.includes(muscle)) {
      this.selectedPrimaryMuscles.set(curr.filter(m => m !== muscle));
    } else {
      this.selectedPrimaryMuscles.set([...curr, muscle]);
    }
  }

  onSubmit() {
    const trimmedName = this.name().trim();
    if (!trimmedName) {
      this.errorMessage.set('Exercise name is required.');
      return;
    }

    const instructions = this.instructionsText()
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);

    const safetyTips = this.safetyTipsText()
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0);

    const request: ExerciseRequest = {
      id: this.exercise()?.id,
      name: trimmedName,
      description: this.description().trim() || undefined,
      primaryCategory: this.primaryCategory(),
      difficultyLevel: this.difficultyLevel(),
      equipmentRequired: this.selectedEquipment() as any,
      stepByStepInstructions: instructions.length > 0 ? instructions : undefined,
      safetyTips: safetyTips.length > 0 ? safetyTips : undefined,
      resistanceMetrics: {
        movementPattern: this.movementPattern() as any,
        primaryMuscles: this.selectedPrimaryMuscles() as any,
        recommendedSets: this.sets() ?? undefined,
        recommendedRepsMin: this.repsMin() ?? undefined,
        recommendedRepsMax: this.repsMax() ?? undefined,
        recommendedRestSeconds: this.restSeconds() ?? undefined
      }
    };

    this.save.emit(request);
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.closed.emit();
    }
  }
}
