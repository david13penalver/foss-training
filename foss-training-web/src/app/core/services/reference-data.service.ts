import { Injectable, inject } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReferenceDataService {
  private readonly http = inject(HttpClient);

  // Reactive Signal Resources
  readonly equipmentResource = httpResource<string[]>(() => '/api/equipment', {
    defaultValue: []
  });

  readonly muscleGroupsResource = httpResource<string[]>(() => '/api/muscle-groups', {
    defaultValue: []
  });

  readonly movementPatternsResource = httpResource<string[]>(() => '/api/movement-patterns', {
    defaultValue: []
  });

  // Observable methods for specific calls / tests
  getEquipment(): Observable<string[]> {
    return this.http.get<string[]>('/api/equipment');
  }

  getMuscleGroups(): Observable<string[]> {
    return this.http.get<string[]>('/api/muscle-groups');
  }

  getMovementPatterns(): Observable<string[]> {
    return this.http.get<string[]>('/api/movement-patterns');
  }

  getEnduranceTypes(): Observable<string[]> {
    return this.http.get<string[]>('/api/endurance-types');
  }

  getMobilityTypes(): Observable<string[]> {
    return this.http.get<string[]>('/api/mobility-types');
  }
}
