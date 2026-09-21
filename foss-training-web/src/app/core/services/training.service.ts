import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';
import type {
  Training,
  TrainingRequest,
  PageResponseTraining,
  TrainingSearchParams,
  CompleteTrainingRequest,
  LogSetRequest,
  LogIntervalRequest,
  WorkoutSummaryResponse
} from '../api/models';

@Injectable({
  providedIn: 'root'
})
export class TrainingService {
  private readonly http = inject(HttpClient);

  /**
   * Reactive signal-based resource for trainings list.
   */
  readonly trainingsResource = httpResource<Training[]>(() => '/api/trainings', {
    defaultValue: []
  });

  searchTrainings(params?: TrainingSearchParams): Observable<PageResponseTraining> {
    const httpParams = this.buildTrainingHttpParams(params);
    return this.http.get<PageResponseTraining>('/api/trainings/search', { params: httpParams });
  }

  getTrainings(params?: TrainingSearchParams): Observable<Training[]> {
    const httpParams = this.buildTrainingHttpParams(params);
    return this.http.get<Training[]>('/api/trainings', { params: httpParams });
  }

  getTrainingById(id: number): Observable<Training> {
    return this.http.get<Training>(`/api/trainings/${id}`);
  }

  createTraining(request: TrainingRequest): Observable<Training> {
    return this.http.post<Training>('/api/trainings', request);
  }

  updateTraining(id: number, request: TrainingRequest): Observable<Training> {
    return this.http.put<Training>(`/api/trainings/${id}`, request);
  }

  deleteTraining(id: number): Observable<void> {
    return this.http.delete<void>(`/api/trainings/${id}`);
  }

  startTraining(id: number): Observable<Training> {
    return this.http.post<Training>(`/api/trainings/${id}/start`, {});
  }

  pauseTraining(id: number): Observable<Training> {
    return this.http.post<Training>(`/api/trainings/${id}/pause`, {});
  }

  resumeTraining(id: number): Observable<Training> {
    return this.http.post<Training>(`/api/trainings/${id}/resume`, {});
  }

  completeTraining(id: number, request?: CompleteTrainingRequest): Observable<Training> {
    return this.http.post<Training>(`/api/trainings/${id}/complete`, request || {});
  }

  cancelTraining(id: number): Observable<Training> {
    return this.http.post<Training>(`/api/trainings/${id}/cancel`, {});
  }

  logSet(trainingId: number, exerciseId: number, request: LogSetRequest): Observable<Training> {
    return this.http.post<Training>(`/api/trainings/${trainingId}/exercises/${exerciseId}/sets`, request);
  }

  updateSet(trainingId: number, exerciseId: number, setNumber: number, request: LogSetRequest): Observable<Training> {
    return this.http.put<Training>(`/api/trainings/${trainingId}/exercises/${exerciseId}/sets/${setNumber}`, request);
  }

  deleteSet(trainingId: number, exerciseId: number, setNumber: number): Observable<Training> {
    return this.http.delete<Training>(`/api/trainings/${trainingId}/exercises/${exerciseId}/sets/${setNumber}`);
  }

  logInterval(trainingId: number, exerciseId: number, request: LogIntervalRequest): Observable<Training> {
    return this.http.post<Training>(`/api/trainings/${trainingId}/exercises/${exerciseId}/intervals`, request);
  }

  deleteInterval(trainingId: number, exerciseId: number, intervalNumber: number): Observable<Training> {
    return this.http.delete<Training>(`/api/trainings/${trainingId}/exercises/${exerciseId}/intervals/${intervalNumber}`);
  }

  getWorkoutSummary(id: number): Observable<WorkoutSummaryResponse> {
    return this.http.get<WorkoutSummaryResponse>(`/api/trainings/${id}/summary`);
  }

  checkTrainingExists(id: number): Observable<boolean> {
    return this.http.get<boolean>(`/api/trainings/${id}/exists`);
  }

  createTrainingFromSession(sessionId: number, date?: string, customName?: string): Observable<Training> {
    let url = `/api/trainings/from-session/${sessionId}`;
    const params: string[] = [];
    if (date) params.push(`date=${encodeURIComponent(date)}`);
    if (customName) params.push(`customName=${encodeURIComponent(customName)}`);
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }
    return this.http.post<Training>(url, {});
  }

  private buildTrainingHttpParams(params?: TrainingSearchParams): HttpParams {
    let httpParams = new HttpParams();
    if (!params) return httpParams;

    if (params.startDate) httpParams = httpParams.set('startDate', params.startDate);
    if (params.endDate) httpParams = httpParams.set('endDate', params.endDate);
    if (params.status && params.status !== 'ALL') httpParams = httpParams.set('status', params.status);
    if (params.search) httpParams = httpParams.set('search', params.search);
    if (params.q) httpParams = httpParams.set('q', params.q);
    if (params.programId !== undefined) httpParams = httpParams.set('programId', params.programId.toString());
    if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
    if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());
    if (params.sortBy) httpParams = httpParams.set('sortBy', params.sortBy);
    if (params.sortDirection) httpParams = httpParams.set('sortDirection', params.sortDirection);

    return httpParams;
  }
}
