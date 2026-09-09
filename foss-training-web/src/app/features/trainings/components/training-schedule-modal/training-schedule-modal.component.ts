import { Component, inject, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SessionService } from '../../../../core/services/session.service';

export interface ScheduleTrainingPayload {
  sessionId: number;
  date: string;
  customName?: string;
}

@Component({
  selector: 'app-training-schedule-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './training-schedule-modal.component.html',
  styleUrl: './training-schedule-modal.component.scss'
})
export class TrainingScheduleModalComponent {
  private readonly sessionService = inject(SessionService);

  readonly sessions = this.sessionService.sessionsResource.value;

  readonly selectedSessionId = signal<number | null>(null);
  readonly trainingDate = signal<string>(new Date().toISOString().substring(0, 10));
  readonly customName = signal<string>('');

  readonly close = output<void>();
  readonly scheduleTraining = output<ScheduleTrainingPayload>();

  onSubmit() {
    const sId = this.selectedSessionId();
    const date = this.trainingDate();
    if (!sId || !date) return;

    this.scheduleTraining.emit({
      sessionId: Number(sId),
      date,
      customName: this.customName().trim() || undefined
    });
  }

  onClose() {
    this.close.emit();
  }
}
