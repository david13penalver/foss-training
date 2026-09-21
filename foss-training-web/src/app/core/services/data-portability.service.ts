import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import type { FullBackupData, ImportSummary } from '../api/models';

@Injectable({
  providedIn: 'root'
})
export class DataPortabilityService {
  private readonly http = inject(HttpClient);

  /**
   * Export full database backup as JSON object.
   */
  exportBackup(): Observable<FullBackupData> {
    return this.http.get<FullBackupData>('/api/data/export/backup');
  }

  /**
   * Export all workout records and exercise sets as CSV.
   */
  exportWorkoutsCsv(): Observable<string> {
    return this.http.get('/api/data/export/workouts.csv', { responseType: 'text' });
  }

  /**
   * Trigger download of full JSON backup file in the browser.
   */
  downloadJsonBackup(): Observable<FullBackupData> {
    return this.exportBackup().pipe(
      tap((data) => {
        const json = JSON.stringify(data, null, 2);
        const dateStr = new Date().toISOString().slice(0, 10);
        this.triggerBrowserDownload(json, `foss-training-backup-${dateStr}.json`, 'application/json');
      })
    );
  }

  /**
   * Trigger download of workout execution logs CSV file in the browser.
   */
  downloadWorkoutsCsv(): Observable<string> {
    return this.exportWorkoutsCsv().pipe(
      tap((csv) => {
        const dateStr = new Date().toISOString().slice(0, 10);
        this.triggerBrowserDownload(csv, `foss-training-workouts-${dateStr}.csv`, 'text/csv;charset=utf-8;');
      })
    );
  }

  /**
   * Restore full database snapshot from backup data.
   */
  importBackup(backupData: FullBackupData): Observable<ImportSummary> {
    return this.http.post<ImportSummary>('/api/data/import/backup', backupData);
  }

  /**
   * Import workouts and exercise sets from CSV content string.
   */
  importWorkoutsCsv(csvContent: string): Observable<ImportSummary> {
    return this.http.post<ImportSummary>('/api/data/import/workouts.csv', csvContent, {
      headers: { 'Content-Type': 'text/csv' }
    });
  }

  private triggerBrowserDownload(content: string, filename: string, mimeType: string): void {
    if (typeof window === 'undefined') return;
    const blob = new Blob([content], { type: mimeType });
    const url = window.URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = filename;
    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);
    window.URL.revokeObjectURL(url);
  }
}
