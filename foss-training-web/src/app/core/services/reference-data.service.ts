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

  readonly enduranceTypesResource = httpResource<string[]>(() => '/api/endurance-types', {
    defaultValue: []
  });

  readonly mobilityTypesResource = httpResource<string[]>(() => '/api/mobility-types', {
    defaultValue: []
  });

  readonly jointsResource = httpResource<string[]>(() => '/api/joints', {
    defaultValue: []
  });

  readonly stretchTypesResource = httpResource<string[]>(() => '/api/stretch-types', {
    defaultValue: []
  });

  // Observable methods for specific calls / tests
  getEquipment(): Observable<string[]> {
    return this.http.get<string[]>('/api/equipment');
  }

  getEquipmentByName(name: string): Observable<string> {
    return this.http.get<string>(`/api/equipment/${encodeURIComponent(name)}`);
  }

  getMuscleGroups(): Observable<string[]> {
    return this.http.get<string[]>('/api/muscle-groups');
  }

  getMuscleGroupByName(name: string): Observable<string> {
    return this.http.get<string>(`/api/muscle-groups/${encodeURIComponent(name)}`);
  }

  getMovementPatterns(): Observable<string[]> {
    return this.http.get<string[]>('/api/movement-patterns');
  }

  getMovementPatternByName(name: string): Observable<string> {
    return this.http.get<string>(`/api/movement-patterns/${encodeURIComponent(name)}`);
  }

  getEnduranceTypes(): Observable<string[]> {
    return this.http.get<string[]>('/api/endurance-types');
  }

  getEnduranceTypeByName(name: string): Observable<string> {
    return this.http.get<string>(`/api/endurance-types/${encodeURIComponent(name)}`);
  }

  getMobilityTypes(): Observable<string[]> {
    return this.http.get<string[]>('/api/mobility-types');
  }

  getMobilityTypeByName(name: string): Observable<string> {
    return this.http.get<string>(`/api/mobility-types/${encodeURIComponent(name)}`);
  }

  getJoints(): Observable<string[]> {
    return this.http.get<string[]>('/api/joints');
  }

  getJointByName(name: string): Observable<string> {
    return this.http.get<string>(`/api/joints/${encodeURIComponent(name)}`);
  }

  getStretchTypes(): Observable<string[]> {
    return this.http.get<string[]>('/api/stretch-types');
  }

  getStretchTypeByName(name: string): Observable<string> {
    return this.http.get<string>(`/api/stretch-types/${encodeURIComponent(name)}`);
  }
}
