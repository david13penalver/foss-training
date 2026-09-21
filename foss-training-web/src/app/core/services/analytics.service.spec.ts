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

  it('should fetch ACWR without parameters', () => {
    service.getAcwr().subscribe(res => {
      expect(res.acuteWorkload).toBe(1500);
      expect(res.riskZone).toBe('OPTIMAL');
    });

    const req = httpTesting.expectOne('/api/analytics/acwr');
    expect(req.request.method).toBe('GET');
    req.flush({
      targetDate: '2026-09-21',
      acuteDays: 7,
      chronicDays: 28,
      acuteWorkload: 1500,
      chronicWorkload: 1400,
      ratio: 1.07,
      riskZone: 'OPTIMAL',
      deloadRecommended: false
    });
  });

  it('should fetch ACWR with targetDate parameter', () => {
    service.getAcwr('2026-09-20').subscribe(res => {
      expect(res.acwr).toBe(1.6);
      expect(res.riskZone).toBe('HIGH_RISK');
    });

    const req = httpTesting.expectOne('/api/analytics/acwr?targetDate=2026-09-20');
    expect(req.request.method).toBe('GET');
    req.flush({
      targetDate: '2026-09-20',
      acwr: 1.6,
      riskZone: 'HIGH_RISK',
      deloadRecommended: true
    });
  });

  it('should fetch weekly muscle volume without parameters', () => {
    service.getWeeklyMuscleVolume().subscribe(res => {
      expect(res.muscleVolumes?.length).toBe(1);
    });

    const req = httpTesting.expectOne('/api/analytics/muscle-volume');
    expect(req.request.method).toBe('GET');
    req.flush({
      startDate: '2026-09-14',
      endDate: '2026-09-21',
      muscleVolumes: [
        {
          muscleGroup: 'CHEST',
          directSets: 12,
          fractionalSets: 0,
          effectiveSets: 12,
          status: 'OPTIMAL_ADAPTIVE'
        }
      ]
    });
  });

  it('should fetch weekly muscle volume with date range', () => {
    service.getWeeklyMuscleVolume('2026-09-01', '2026-09-07').subscribe(res => {
      expect(res.totalWorkingSets).toBe(40);
    });

    const req = httpTesting.expectOne('/api/analytics/muscle-volume?startDate=2026-09-01&endDate=2026-09-07');
    expect(req.request.method).toBe('GET');
    req.flush({
      startDate: '2026-09-01',
      endDate: '2026-09-07',
      totalWorkingSets: 40,
      muscleVolumes: []
    });
  });

  it('should fetch exercise progression with parameters', () => {
    service.getExerciseProgression({
      exerciseId: 1,
      startDate: '2026-09-01',
      endDate: '2026-09-20',
      formula: 'EPLEY'
    }).subscribe(res => {
      expect(res.exerciseId).toBe(1);
      expect(res.trend).toBe('IMPROVING');
    });

    const req = httpTesting.expectOne('/api/analytics/progression/1?startDate=2026-09-01&endDate=2026-09-20&formula=EPLEY');
    expect(req.request.method).toBe('GET');
    req.flush({
      exerciseId: 1,
      exerciseName: 'Bench Press',
      trend: 'IMPROVING',
      dataPoints: []
    });
  });

  it('should calculate heart rate zones with parameters', () => {
    service.calculateHeartRateZones({ maxHr: 190, restingHr: 60, age: 30 }).subscribe(res => {
      expect(res.maxHr).toBe(190);
      expect(res.method).toBe('KARVONEN');
    });

    const req = httpTesting.expectOne('/api/analytics/heart-rate-zones?maxHr=190&restingHr=60&age=30');
    expect(req.request.method).toBe('GET');
    req.flush({
      maxHr: 190,
      restingHr: 60,
      method: 'KARVONEN',
      zones: []
    });
  });
});
