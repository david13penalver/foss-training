import { Injectable, inject } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { Training, TrainingRequest } from '../api/models';

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

  completeTraining(id: number): Observable<Training> {
    return this.http.post<Training>(`/api/trainings/${id}/complete`, {});
  }

  cancelTraining(id: number): Observable<Training> {
    return this.http.post<Training>(`/api/trainings/${id}/cancel`, {});
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
}
