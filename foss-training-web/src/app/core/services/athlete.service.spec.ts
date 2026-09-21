import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AthleteService } from './athlete.service';
import type { BodyweightEntry, RelativeStrengthResponse } from '../api/models';

describe('AthleteService', () => {
  let service: AthleteService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AthleteService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AthleteService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log bodyweight entry', () => {
    const mockEntry: BodyweightEntry = {
      id: 1,
      entryDate: '2026-09-21',
      weightKg: 82.5,
      bodyFatPercentage: 14.2,
      notes: 'Morning weigh-in'
    };

    service.logBodyweight({
      entryDate: '2026-09-21',
      weightKg: 82.5,
      bodyFatPercentage: 14.2,
      notes: 'Morning weigh-in'
    }).subscribe(res => {
      expect(res.id).toBe(1);
      expect(res.weightKg).toBe(82.5);
    });

    const req = httpTesting.expectOne('/api/athlete/bodyweight');
    expect(req.request.method).toBe('POST');
    req.flush(mockEntry);
  });

  it('should get bodyweight history', () => {
    const mockList: BodyweightEntry[] = [
      { id: 1, entryDate: '2026-09-15', weightKg: 83.0 },
      { id: 2, entryDate: '2026-09-20', weightKg: 82.2 }
    ];

    service.getBodyweightHistory().subscribe(list => {
      expect(list.length).toBe(2);
      expect(list[1].weightKg).toBe(82.2);
    });

    const req = httpTesting.expectOne('/api/athlete/bodyweight/history');
    expect(req.request.method).toBe('GET');
    req.flush(mockList);
  });

  it('should get latest bodyweight', () => {
    const mockLatest: BodyweightEntry = { id: 2, entryDate: '2026-09-20', weightKg: 82.2 };

    service.getLatestBodyweight().subscribe(entry => {
      expect(entry.id).toBe(2);
      expect(entry.weightKg).toBe(82.2);
    });

    const req = httpTesting.expectOne('/api/athlete/bodyweight/latest');
    expect(req.request.method).toBe('GET');
    req.flush(mockLatest);
  });

  it('should delete bodyweight entry', () => {
    service.deleteBodyweight(1).subscribe();

    const req = httpTesting.expectOne('/api/athlete/bodyweight/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should calculate relative strength', () => {
    const mockScore: RelativeStrengthResponse = {
      totalWeightKg: 500,
      bodyweightKg: 80,
      gender: 'MALE',
      ratio: 6.25,
      dots: 356.18,
      wilks: 342.91,
      classification: 'Proficient'
    };

    service.calculateRelativeStrength({
      totalWeightKg: 500,
      bodyweightKg: 80,
      gender: 'MALE'
    }).subscribe(res => {
      expect(res.ratio).toBe(6.25);
      expect(res.dots).toBe(356.18);
      expect(res.classification).toBe('Proficient');
    });

    const req = httpTesting.expectOne('/api/athlete/relative-strength');
    expect(req.request.method).toBe('POST');
    req.flush(mockScore);
  });
});
