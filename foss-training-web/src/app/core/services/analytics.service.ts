import { Injectable, inject } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { Calculate1RmParams, OneRepMaxResponse, PersonalRecordResponse } from '../api/models';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly http = inject(HttpClient);

  /**
   * Reactive signal-based resource for personal records across all exercises.
   */
  readonly personalRecordsResource = httpResource<PersonalRecordResponse[]>(
    () => '/api/analytics/personal-records',
    { defaultValue: [] }
  );

  /**
   * Calculate 1RM estimate and percentage load breakdown.
   */
  calculate1Rm(params: Calculate1RmParams): Observable<OneRepMaxResponse> {
    const queryParts = [
      `weight=${encodeURIComponent(params.weight)}`,
      `reps=${encodeURIComponent(params.reps)}`
    ];
    if (params.unit) {
      queryParts.push(`unit=${encodeURIComponent(params.unit)}`);
    }
    if (params.formula) {
      queryParts.push(`formula=${encodeURIComponent(params.formula)}`);
    }
    return this.http.get<OneRepMaxResponse>(`/api/analytics/1rm?${queryParts.join('&')}`);
  }

  /**
   * Fetch all personal records.
   */
  getPersonalRecords(): Observable<PersonalRecordResponse[]> {
    return this.http.get<PersonalRecordResponse[]>('/api/analytics/personal-records');
  }

  /**
   * Fetch personal record for a specific exercise.
   */
  getPersonalRecordsByExercise(exerciseId: number): Observable<PersonalRecordResponse> {
    return this.http.get<PersonalRecordResponse>(`/api/analytics/personal-records/exercise/${exerciseId}`);
  }
}
