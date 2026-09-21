import { Injectable, inject } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';
import type {
  BodyweightEntry,
  BodyweightRequest,
  RelativeStrengthRequest,
  RelativeStrengthResponse
} from '../api/models';

@Injectable({
  providedIn: 'root'
})
export class AthleteService {
  private readonly http = inject(HttpClient);

  /**
   * Reactive signal-based resource for full bodyweight history.
   */
  readonly bodyweightHistoryResource = httpResource<BodyweightEntry[]>(
    () => '/api/athlete/bodyweight/history',
    { defaultValue: [] }
  );

  /**
   * Reactive signal-based resource for latest bodyweight measurement.
   */
  readonly latestBodyweightResource = httpResource<BodyweightEntry | undefined>(
    () => '/api/athlete/bodyweight/latest'
  );

  /**
   * Log or update a bodyweight record.
   */
  logBodyweight(request: BodyweightRequest): Observable<BodyweightEntry> {
    return this.http.post<BodyweightEntry>('/api/athlete/bodyweight', request);
  }

  /**
   * Fetch all historical bodyweight entries.
   */
  getBodyweightHistory(): Observable<BodyweightEntry[]> {
    return this.http.get<BodyweightEntry[]>('/api/athlete/bodyweight/history');
  }

  /**
   * Fetch latest bodyweight entry.
   */
  getLatestBodyweight(): Observable<BodyweightEntry> {
    return this.http.get<BodyweightEntry>('/api/athlete/bodyweight/latest');
  }

  /**
   * Delete a bodyweight record by ID.
   */
  deleteBodyweight(id: number): Observable<void> {
    return this.http.delete<void>(`/api/athlete/bodyweight/${id}`);
  }

  /**
   * Calculate DOTS, Wilks, and Bodyweight Ratio powerlifting relative strength metrics.
   */
  calculateRelativeStrength(request: RelativeStrengthRequest): Observable<RelativeStrengthResponse> {
    return this.http.post<RelativeStrengthResponse>('/api/athlete/relative-strength', request);
  }
}
