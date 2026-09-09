import { Injectable, inject } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import type { Exercise, ExerciseRequest } from '../api/models';

@Injectable({
  providedIn: 'root'
})
export class ExerciseService {
  private readonly http = inject(HttpClient);

  /**
   * Reactive signal-based resource for exercises list.
   * Exposes .value(), .isLoading(), .error(), and .reload() signals natively.
   */
  readonly exercisesResource = httpResource<Exercise[]>(() => '/api/exercises', {
    defaultValue: []
  });

  getExerciseById(id: number) {
    return this.http.get<Exercise>(`/api/exercises/${id}`);
  }

  createExercise(request: ExerciseRequest) {
    return this.http.post<Exercise>('/api/exercises', request);
  }

  updateExercise(id: number, request: ExerciseRequest) {
    return this.http.put<Exercise>(`/api/exercises/${id}`, request);
  }

  deleteExercise(id: number) {
    return this.http.delete<void>(`/api/exercises/${id}`);
  }

  checkExerciseExists(id: number) {
    return this.http.get<boolean>(`/api/exercises/${id}/exists`);
  }
}
