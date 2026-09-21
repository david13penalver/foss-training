import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AthleteService } from '../../../../core/services/athlete.service';
import type { BodyweightEntry, BodyweightRequest } from '../../../../core/api/models';

@Component({
  selector: 'app-bodyweight-tracker',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './bodyweight-tracker.component.html',
  styleUrl: './bodyweight-tracker.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BodyweightTrackerComponent {
  private readonly athleteService = inject(AthleteService);

  readonly historyResource = this.athleteService.bodyweightHistoryResource;
  readonly entries = computed<BodyweightEntry[]>(() => {
    const list = this.historyResource.value() ?? [];
    return [...list].sort((a, b) => (b.entryDate ?? '').localeCompare(a.entryDate ?? ''));
  });

  readonly latestEntry = computed<BodyweightEntry | undefined>(() => {
    const list = this.entries();
    return list.length > 0 ? list[0] : undefined;
  });

  readonly previousEntry = computed<BodyweightEntry | undefined>(() => {
    const list = this.entries();
    return list.length > 1 ? list[1] : undefined;
  });

  readonly weightDelta = computed<number | undefined>(() => {
    const latest = this.latestEntry();
    const prev = this.previousEntry();
    if (latest?.weightKg !== undefined && prev?.weightKg !== undefined) {
      return Math.round((latest.weightKg - prev.weightKg) * 10) / 10;
    }
    return undefined;
  });

  // Form inputs
  readonly entryDate = signal<string>(this.getLocalDateString());
  readonly weightKg = signal<number | null>(null);
  readonly bodyFatPercentage = signal<number | null>(null);
  readonly notes = signal<string>('');

  readonly isSubmitting = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);

  logWeight(): void {
    const weight = this.weightKg();
    const date = this.entryDate();

    if (!weight || weight <= 0) {
      this.errorMessage.set('Please enter a valid positive weight in kg.');
      return;
    }
    if (!date) {
      this.errorMessage.set('Please select an entry date.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const payload: BodyweightRequest = {
      entryDate: date,
      weightKg: weight,
      bodyFatPercentage: this.bodyFatPercentage() ?? undefined,
      notes: this.notes()?.trim() || undefined
    };

    this.athleteService.logBodyweight(payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.weightKg.set(null);
        this.bodyFatPercentage.set(null);
        this.notes.set('');
        this.historyResource.reload();
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(err?.error?.detail || 'Failed to log bodyweight measurement.');
      }
    });
  }

  deleteEntry(id?: number): void {
    if (id === undefined) return;
    this.athleteService.deleteBodyweight(id).subscribe({
      next: () => this.historyResource.reload(),
      error: (err) => this.errorMessage.set(err?.error?.detail || 'Failed to delete record.')
    });
  }

  private getLocalDateString(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
