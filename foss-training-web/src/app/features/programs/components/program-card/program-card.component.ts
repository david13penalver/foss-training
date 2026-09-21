import { Component, ChangeDetectionStrategy, input, output } from '@angular/core';
import type { TrainingProgram } from '../../../../core/api/models';

@Component({
  selector: 'app-program-card',
  standalone: true,
  templateUrl: './program-card.component.html',
  styleUrl: './program-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgramCardComponent {
  readonly program = input.required<TrainingProgram>();

  readonly view = output<TrainingProgram>();
  readonly viewAdherence = output<TrainingProgram>();
  readonly clone = output<TrainingProgram>();
  readonly edit = output<TrainingProgram>();
  readonly delete = output<TrainingProgram>();
  readonly schedule = output<TrainingProgram>();

  getDayName(dayOfWeek?: number): string {
    switch (dayOfWeek) {
      case 1: return 'Mon';
      case 2: return 'Tue';
      case 3: return 'Wed';
      case 4: return 'Thu';
      case 5: return 'Fri';
      case 6: return 'Sat';
      case 7: return 'Sun';
      default: return `Day ${dayOfWeek}`;
    }
  }
}
