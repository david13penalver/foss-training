import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { DataPortabilityService } from '../../../../core/services/data-portability.service';
import type { FullBackupData, ImportSummary } from '../../../../core/api/models';

@Component({
  selector: 'app-data-sovereignty-hub',
  standalone: true,
  templateUrl: './data-sovereignty-hub.component.html',
  styleUrl: './data-sovereignty-hub.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DataSovereigntyHubComponent {
  private readonly dataService = inject(DataPortabilityService);

  readonly isExporting = signal<boolean>(false);
  readonly isImporting = signal<boolean>(false);

  readonly successMessage = signal<string | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly importSummary = signal<ImportSummary | null>(null);

  downloadBackup(): void {
    this.isExporting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.dataService.downloadJsonBackup().subscribe({
      next: () => {
        this.isExporting.set(false);
        this.successMessage.set('Full JSON backup downloaded successfully.');
      },
      error: (err) => {
        this.isExporting.set(false);
        this.errorMessage.set(err?.error?.detail || 'Failed to download JSON backup.');
      }
    });
  }

  downloadWorkoutsCsv(): void {
    this.isExporting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.dataService.downloadWorkoutsCsv().subscribe({
      next: () => {
        this.isExporting.set(false);
        this.successMessage.set('Workout logs CSV downloaded successfully.');
      },
      error: (err) => {
        this.isExporting.set(false);
        this.errorMessage.set(err?.error?.detail || 'Failed to export CSV workouts.');
      }
    });
  }

  onJsonFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    const reader = new FileReader();

    this.isImporting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.importSummary.set(null);

    reader.onload = () => {
      try {
        const data: FullBackupData = JSON.parse(reader.result as string);
        this.dataService.importBackup(data).subscribe({
          next: (summary) => {
            this.isImporting.set(false);
            this.importSummary.set(summary);
            this.successMessage.set(`Backup restore complete! Restored ${summary.totalImported} items.`);
            input.value = '';
          },
          error: (err) => {
            this.isImporting.set(false);
            this.errorMessage.set(err?.error?.detail || 'Failed to restore database from backup file.');
            input.value = '';
          }
        });
      } catch {
        this.isImporting.set(false);
        this.errorMessage.set('Invalid JSON backup file. Please select a valid export file.');
        input.value = '';
      }
    };

    reader.onerror = () => {
      this.isImporting.set(false);
      this.errorMessage.set('Could not read file from disk.');
      input.value = '';
    };

    reader.readAsText(file);
  }

  onCsvFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    const reader = new FileReader();

    this.isImporting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.importSummary.set(null);

    reader.onload = () => {
      const csvContent = reader.result as string;
      this.dataService.importWorkoutsCsv(csvContent).subscribe({
        next: (summary) => {
          this.isImporting.set(false);
          this.importSummary.set(summary);
          this.successMessage.set(`CSV import complete! Restored ${summary.trainingsImported} workouts.`);
          input.value = '';
        },
        error: (err) => {
          this.isImporting.set(false);
          this.errorMessage.set(err?.error?.detail || 'Failed to import workouts from CSV.');
          input.value = '';
        }
      });
    };

    reader.onerror = () => {
      this.isImporting.set(false);
      this.errorMessage.set('Could not read CSV file from disk.');
      input.value = '';
    };

    reader.readAsText(file);
  }
}
