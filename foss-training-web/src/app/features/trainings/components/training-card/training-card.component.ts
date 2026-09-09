import { Component, input, output, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import type { Training } from '../../../../core/api/models';

@Component({
  selector: 'app-training-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './training-card.component.html',
  styleUrl: './training-card.component.scss'
})
export class TrainingCardComponent {
  readonly training = input.required<Training>();

  readonly viewDetails = output<Training>();
  readonly start = output<Training>();
  readonly complete = output<Training>();
  readonly cancel = output<Training>();
  readonly delete = output<Training>();

  readonly statusClass = computed(() => {
    const status = this.training().status;
    switch (status) {
      case 'In Progress':
        return 'status-in-progress';
      case 'Completed':
        return 'status-completed';
      case 'Cancelled':
      case 'Skipped':
        return 'status-cancelled';
      case 'Planned':
      default:
        return 'status-planned';
    }
  });

  readonly exerciseNames = computed(() => {
    const t = this.training();
    if (!t.session?.sessionExercises) return [];
    return t.session.sessionExercises
      .map(se => se.exercise?.name)
      .filter((name): name is string => !!name);
  });

  readonly formattedVolume = computed(() => {
    const vol = this.training().totalVolume;
    if (vol === undefined || vol === null) return '0 kg';
    return `${Number(vol).toLocaleString()} kg`;
  });
}
