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

  it('should fetch joints list', () => {
    const mockJoints = ['SHOULDER', 'HIP', 'KNEE', 'ANKLE'];
    service.getJoints().subscribe(items => {
      expect(items).toEqual(mockJoints);
    });

    const req = httpTesting.expectOne('/api/joints');
    expect(req.request.method).toBe('GET');
    req.flush(mockJoints);
  });

  it('should fetch stretch types list', () => {
    const mockStretches = ['STATIC', 'DYNAMIC', 'PNF', 'BALLISTIC'];
    service.getStretchTypes().subscribe(items => {
      expect(items).toEqual(mockStretches);
    });

    const req = httpTesting.expectOne('/api/stretch-types');
    expect(req.request.method).toBe('GET');
    req.flush(mockStretches);
  });

  it('should fetch equipment by name', () => {
    service.getEquipmentByName('BARBELL').subscribe(item => {
      expect(item).toBe('BARBELL');
    });

    const req = httpTesting.expectOne('/api/equipment/BARBELL');
    expect(req.request.method).toBe('GET');
    req.flush('BARBELL');
  });
});
