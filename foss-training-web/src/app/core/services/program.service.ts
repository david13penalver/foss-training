import { Injectable, inject } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { Training, TrainingProgram, TrainingProgramRequest } from '../api/models';

@Injectable({
  providedIn: 'root'
})
export class ProgramService {
  private readonly http = inject(HttpClient);

  /**
   * Reactive signal-based resource for training programs list.
   */
  readonly programsResource = httpResource<TrainingProgram[]>(() => '/api/programs', {
    defaultValue: []
  });

  getAllPrograms(): Observable<TrainingProgram[]> {
    return this.http.get<TrainingProgram[]>('/api/programs');
  }

  getProgramById(id: number): Observable<TrainingProgram> {
    return this.http.get<TrainingProgram>(`/api/programs/${id}`);
  }

  createProgram(request: TrainingProgramRequest): Observable<TrainingProgram> {
    return this.http.post<TrainingProgram>('/api/programs', request);
  }

  updateProgram(id: number, request: TrainingProgramRequest): Observable<TrainingProgram> {
    return this.http.put<TrainingProgram>(`/api/programs/${id}`, request);
  }

  deleteProgram(id: number): Observable<void> {
    return this.http.delete<void>(`/api/programs/${id}`);
  }

  checkProgramExists(id: number): Observable<boolean> {
    return this.http.get<boolean>(`/api/programs/${id}/exists`);
  }

  generateSchedule(id: number, startDate?: string): Observable<Training[]> {
    let url = `/api/programs/${id}/generate-schedule`;
    if (startDate) {
      url += `?startDate=${encodeURIComponent(startDate)}`;
    }
    return this.http.post<Training[]>(url, {});
  }
}
