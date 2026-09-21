import { Injectable, inject } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';
import type {
  AcwrResponse,
  Calculate1RmParams,
  CalculateHeartRateZonesParams,
  ExerciseProgressionParams,
  ExerciseProgressionResponse,
  HeartRateZonesResponse,
  OneRepMaxResponse,
  PersonalRecordResponse,
  WeeklyMuscleVolumeResponse
} from '../api/models';

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
   * Reactive signal-based resource for ACWR (Acute:Chronic Workload Ratio).
   */
  readonly acwrResource = httpResource<AcwrResponse | undefined>(
    () => '/api/analytics/acwr'
  );

  /**
   * Reactive signal-based resource for weekly muscle group volume.
   */
  readonly muscleVolumeResource = httpResource<WeeklyMuscleVolumeResponse | undefined>(
    () => '/api/analytics/muscle-volume'
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

  /**
   * Fetch ACWR (Acute:Chronic Workload Ratio) for a target date.
   */
  getAcwr(targetDate?: string): Observable<AcwrResponse> {
    const query = targetDate ? `?targetDate=${encodeURIComponent(targetDate)}` : '';
    return this.http.get<AcwrResponse>(`/api/analytics/acwr${query}`);
  }

  /**
   * Fetch weekly muscle group volume & hypertrophy balance breakdown.
   */
  getWeeklyMuscleVolume(startDate?: string, endDate?: string): Observable<WeeklyMuscleVolumeResponse> {
    const queryParts: string[] = [];
    if (startDate) queryParts.push(`startDate=${encodeURIComponent(startDate)}`);
    if (endDate) queryParts.push(`endDate=${encodeURIComponent(endDate)}`);
    const query = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return this.http.get<WeeklyMuscleVolumeResponse>(`/api/analytics/muscle-volume${query}`);
  }

  /**
   * Fetch exercise strength progression time-series.
   */
  getExerciseProgression(params: ExerciseProgressionParams): Observable<ExerciseProgressionResponse> {
    const queryParts: string[] = [];
    if (params.startDate) queryParts.push(`startDate=${encodeURIComponent(params.startDate)}`);
    if (params.endDate) queryParts.push(`endDate=${encodeURIComponent(params.endDate)}`);
    if (params.formula) queryParts.push(`formula=${encodeURIComponent(params.formula)}`);
    const query = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return this.http.get<ExerciseProgressionResponse>(`/api/analytics/progression/${params.exerciseId}${query}`);
  }

  /**
   * Calculate target heart rate and cardio zones (Zones 1-5).
   */
  calculateHeartRateZones(params?: CalculateHeartRateZonesParams): Observable<HeartRateZonesResponse> {
    const queryParts: string[] = [];
    if (params?.maxHr !== undefined) queryParts.push(`maxHr=${encodeURIComponent(params.maxHr)}`);
    if (params?.restingHr !== undefined) queryParts.push(`restingHr=${encodeURIComponent(params.restingHr)}`);
    if (params?.age !== undefined) queryParts.push(`age=${encodeURIComponent(params.age)}`);
    const query = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
    return this.http.get<HeartRateZonesResponse>(`/api/analytics/heart-rate-zones${query}`);
  }
}

