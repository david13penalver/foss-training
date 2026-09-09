import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ReferenceDataService } from './reference-data.service';

describe('ReferenceDataService', () => {
  let service: ReferenceDataService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ReferenceDataService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ReferenceDataService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch equipment list', () => {
    const mockEquipment = ['BARBELL', 'DUMBBELL', 'BODYWEIGHT'];
    service.getEquipment().subscribe(items => {
      expect(items).toEqual(mockEquipment);
    });

    const req = httpTesting.expectOne('/api/equipment');
    expect(req.request.method).toBe('GET');
    req.flush(mockEquipment);
  });

  it('should fetch muscle groups list', () => {
    const mockMuscles = ['CHEST', 'LATS', 'QUADRICEPS'];
    service.getMuscleGroups().subscribe(items => {
      expect(items).toEqual(mockMuscles);
    });

    const req = httpTesting.expectOne('/api/muscle-groups');
    expect(req.request.method).toBe('GET');
    req.flush(mockMuscles);
  });

  it('should fetch movement patterns list', () => {
    const mockPatterns = ['PUSH', 'PULL', 'SQUAT'];
    service.getMovementPatterns().subscribe(items => {
      expect(items).toEqual(mockPatterns);
    });

    const req = httpTesting.expectOne('/api/movement-patterns');
    expect(req.request.method).toBe('GET');
    req.flush(mockPatterns);
  });
});
