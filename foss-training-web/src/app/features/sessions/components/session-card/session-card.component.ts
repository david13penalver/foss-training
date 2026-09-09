import { Component, ChangeDetectionStrategy, input, output, computed } from '@angular/core';
import type { Session, ResistanceSessionExercise } from '../../../../core/api/models';

@Component({
  selector: 'app-session-card',
  standalone: true,
  templateUrl: './session-card.component.html',
  styleUrl: './session-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionCardComponent {
  readonly session = input.required<Session>();

  readonly startTraining = output<Session>();
  readonly viewDetails = output<Session>();
  readonly edit = output<Session>();
  readonly delete = output<Session>();

  readonly exerciseCount = computed(() => {
    return this.session().sessionExercises?.length ?? 0;
  });

  readonly exerciseNames = computed(() => {
    const list = this.session().sessionExercises ?? [];
    return list
      .map(se => se.exercise?.name)
      .filter((name): name is string => !!name);
  });

  readonly totalSets = computed(() => {
    let count = 0;
    const list = this.session().sessionExercises ?? [];
    for (const item of list) {
      if ('sets' in item && Array.isArray((item as ResistanceSessionExercise).sets)) {
        count += (item as ResistanceSessionExercise).sets?.length ?? 0;
      }
    }
    return count;
  });

  readonly statusClass = computed(() => {
    const status = this.session().sessionStatus?.toLowerCase().replace(/\s+/g, '-') ?? 'draft';
    return `status-${status}`;
  });

  onCardClick() {
    this.viewDetails.emit(this.session());
  }

  onStartTrainingClick(event: MouseEvent) {
    event.stopPropagation();
    this.startTraining.emit(this.session());
  }

  onEditClick(event: MouseEvent) {
    event.stopPropagation();
    this.edit.emit(this.session());
  }

  onDeleteClick(event: MouseEvent) {
    event.stopPropagation();
    this.delete.emit(this.session());
  }
}
