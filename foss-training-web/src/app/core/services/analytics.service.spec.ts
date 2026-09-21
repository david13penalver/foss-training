import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AnalyticsService } from './analytics.service';
import type { OneRepMaxResponse, PersonalRecordResponse } from '../api/models';

describe('AnalyticsService', () => {
  let service: AnalyticsService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AnalyticsService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AnalyticsService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should calculate 1RM with parameters', () => {
    const mockResponse: OneRepMaxResponse = {
      formula: 'EPLEY',
      weight: 100,
      unit: 'KG',
      repetitions: 5,
      estimated1Rm: 116.7,
      percentages: {
        '100': 116.7,
        '90': 105.0
      }
    };

    service.calculate1Rm({ weight: 100, reps: 5, unit: 'KG', formula: 'EPLEY' }).subscribe(res => {
      expect(res.estimated1Rm).toBe(116.7);
    });

    const req = httpTesting.expectOne('/api/analytics/1rm?weight=100&reps=5&unit=KG&formula=EPLEY');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get all personal records', () => {
    const mockRecords: PersonalRecordResponse[] = [
      {
        exerciseId: 1,
        exerciseName: 'Bench Press',
        maxWeight: { value: 120, unit: 'KG', repetitions: 1 }
      }
    ];

    service.getPersonalRecords().subscribe(records => {
      expect(records.length).toBe(1);
      expect(records[0].exerciseName).toBe('Bench Press');
    });

    const req = httpTesting.expectOne('/api/analytics/personal-records');
    expect(req.request.method).toBe('GET');
    req.flush(mockRecords);
  });

  it('should get personal records for exercise', () => {
    const mockRecord: PersonalRecordResponse = {
      exerciseId: 1,
      exerciseName: 'Bench Press',
      bestEstimated1Rm: { estimated1Rm: 130, unit: 'KG', formula: 'EPLEY' }
    };

    service.getPersonalRecordsByExercise(1).subscribe(record => {
      expect(record.exerciseId).toBe(1);
      expect(record.exerciseName).toBe('Bench Press');
    });

    const req = httpTesting.expectOne('/api/analytics/personal-records/exercise/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockRecord);
  });
});
