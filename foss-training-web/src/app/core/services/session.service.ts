import { Injectable, inject } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { CloneSessionRequest, Session, SessionRequest, Training } from '../api/models';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private readonly http = inject(HttpClient);

  /**
   * Reactive signal-based resource for sessions list.
   */
  readonly sessionsResource = httpResource<Session[]>(() => '/api/sessions', {
    defaultValue: []
  });

  getSessionById(id: number): Observable<Session> {
    return this.http.get<Session>(`/api/sessions/${id}`);
  }

  createSession(request: SessionRequest): Observable<Session> {
    return this.http.post<Session>('/api/sessions', request);
  }

  updateSession(id: number, request: SessionRequest): Observable<Session> {
    return this.http.put<Session>(`/api/sessions/${id}`, request);
  }

  deleteSession(id: number): Observable<void> {
    return this.http.delete<void>(`/api/sessions/${id}`);
  }

  cloneSession(id: number, request?: CloneSessionRequest): Observable<Session> {
    return this.http.post<Session>(`/api/sessions/${id}/clone`, request ?? {});
  }

  checkSessionExists(id: number): Observable<boolean> {
    return this.http.get<boolean>(`/api/sessions/${id}/exists`);
  }

  startTrainingFromSession(sessionId: number, date?: string, customName?: string): Observable<Training> {
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
