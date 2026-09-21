import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { Exercise, ExerciseRequest, PageResponseExercise, ExerciseSearchParams } from '../api/models';

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

  searchExercises(params?: ExerciseSearchParams): Observable<PageResponseExercise> {
    const httpParams = this.buildExerciseHttpParams(params);
    return this.http.get<PageResponseExercise>('/api/exercises/search', { params: httpParams });
  }

  getExercises(params?: ExerciseSearchParams): Observable<Exercise[]> {
    const httpParams = this.buildExerciseHttpParams(params);
    return this.http.get<Exercise[]>('/api/exercises', { params: httpParams });
  }

  getExerciseById(id: number): Observable<Exercise> {
    return this.http.get<Exercise>(`/api/exercises/${id}`);
  }

  createExercise(request: ExerciseRequest): Observable<Exercise> {
    return this.http.post<Exercise>('/api/exercises', request);
  }

  updateExercise(id: number, request: ExerciseRequest): Observable<Exercise> {
    return this.http.put<Exercise>(`/api/exercises/${id}`, request);
  }

  deleteExercise(id: number): Observable<void> {
    return this.http.delete<void>(`/api/exercises/${id}`);
  }

  checkExerciseExists(id: number): Observable<boolean> {
    return this.http.get<boolean>(`/api/exercises/${id}/exists`);
  }

  private buildExerciseHttpParams(params?: ExerciseSearchParams): HttpParams {
    let httpParams = new HttpParams();
    if (!params) return httpParams;

    if (params.search) httpParams = httpParams.set('search', params.search);
    if (params.q) httpParams = httpParams.set('q', params.q);
    if (params.primaryCategory && params.primaryCategory !== 'ALL') {
      httpParams = httpParams.set('primaryCategory', params.primaryCategory);
    }
    if (params.muscleGroup && params.muscleGroup !== 'ALL') {
      httpParams = httpParams.set('muscleGroup', params.muscleGroup);
    }
    if (params.equipment && params.equipment !== 'ALL') {
      httpParams = httpParams.set('equipment', params.equipment);
    }
    if (params.difficultyLevel && params.difficultyLevel !== 'ALL') {
      httpParams = httpParams.set('difficultyLevel', params.difficultyLevel);
    }
    if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
    if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());
    if (params.sortBy) httpParams = httpParams.set('sortBy', params.sortBy);
    if (params.sortDirection) httpParams = httpParams.set('sortDirection', params.sortDirection);

    return httpParams;
  }
}
