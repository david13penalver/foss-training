import { Component, input, output, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import type { Training, ResistanceSessionExercise } from '../../../../core/api/models';

@Component({
  selector: 'app-training-detail-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './training-detail-modal.component.html',
  styleUrl: './training-detail-modal.component.scss'
})
export class TrainingDetailModalComponent {
  readonly training = input.required<Training>();

  readonly close = output<void>();

  readonly formattedVolume = computed(() => {
    const vol = this.training().totalVolume;
    if (vol === undefined || vol === null) return '0 kg';
    return `${Number(vol).toLocaleString()} kg`;
  });

  asResistance(exercise: any): ResistanceSessionExercise {
    return exercise as ResistanceSessionExercise;
  }
}
