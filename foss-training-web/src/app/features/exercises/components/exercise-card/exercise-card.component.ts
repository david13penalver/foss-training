import { Component, ChangeDetectionStrategy, input, output, computed } from '@angular/core';
import type { Exercise } from '../../../../core/api/models';

@Component({
  selector: 'app-exercise-card',
  standalone: true,
  templateUrl: './exercise-card.component.html',
  styleUrl: './exercise-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExerciseCardComponent {
  readonly exercise = input.required<Exercise>();

  readonly viewDetails = output<Exercise>();
  readonly edit = output<Exercise>();
  readonly delete = output<Exercise>();

  readonly primaryMuscles = computed(() => {
    return this.exercise().resistanceMetrics?.primaryMuscles ?? [];
  });

  readonly movementPattern = computed(() => {
    return this.exercise().resistanceMetrics?.movementPattern;
  });

  readonly categoryIcon = computed(() => {
    switch (this.exercise().primaryCategory) {
      case 'RESISTANCE': return '🏋️';
      case 'ENDURANCE': return '🏃';
      case 'MOBILITY': return '🧘';
      case 'CALISTHENICS': return '🤸';
      case 'PLYOMETRICS': return '⚡';
      default: return '💪';
    }
  });

  readonly difficultyClass = computed(() => {
    const level = this.exercise().difficultyLevel;
    if (!level) return 'diff-intermediate';
    return `diff-${level.toLowerCase()}`;
  });

  onCardClick() {
    this.viewDetails.emit(this.exercise());
  }

  onEditClick(event: MouseEvent) {
    event.stopPropagation();
    this.edit.emit(this.exercise());
  }

  onDeleteClick(event: MouseEvent) {
    event.stopPropagation();
    this.delete.emit(this.exercise());
  }
}
