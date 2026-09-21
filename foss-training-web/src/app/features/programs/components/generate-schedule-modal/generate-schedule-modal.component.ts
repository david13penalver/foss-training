import { Component, ChangeDetectionStrategy, input, output, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProgramService } from '../../../../core/services/program.service';
import type { Training, TrainingProgram } from '../../../../core/api/models';

@Component({
  selector: 'app-generate-schedule-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './generate-schedule-modal.component.html',
  styleUrl: './generate-schedule-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GenerateScheduleModalComponent {
  private readonly programService = inject(ProgramService);

  readonly program = input.required<TrainingProgram>();

  readonly closed = output<void>();
  readonly scheduleGenerated = output<Training[]>();

  readonly startDate = signal<string>(this.getDefaultStartDate());
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);

  private getDefaultStartDate(): string {
    const d = new Date();
    // Next Monday or today if today is Monday
    const day = d.getDay();
    const diff = day === 1 ? 0 : (8 - day) % 7;
    d.setDate(d.getDate() + (diff === 0 && day !== 1 ? 7 : diff));
    return d.toISOString().split('T')[0];
  }

  generate() {
    const p = this.program();
    if (!p.id) return;

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    this.programService.generateSchedule(p.id, this.startDate()).subscribe({
      next: trainings => {
        this.isSubmitting.set(false);
        this.scheduleGenerated.emit(trainings);
      },
      error: err => {
        this.isSubmitting.set(false);
        this.errorMessage.set('Failed to generate program schedule. Please check your start date.');
      }
    });
  }

  onBackdropClick(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('glass-backdrop')) {
      this.closed.emit();
    }
  }
}
