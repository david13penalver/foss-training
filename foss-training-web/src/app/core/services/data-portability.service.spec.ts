import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { DataPortabilityService } from './data-portability.service';
import type { FullBackupData, ImportSummary } from '../api/models';

describe('DataPortabilityService', () => {
  let service: DataPortabilityService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        DataPortabilityService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(DataPortabilityService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should export full backup JSON', () => {
    const mockBackup: FullBackupData = {
      exportVersion: '1.0',
      exportedAt: '2026-09-21T18:00:00',
      exercises: [],
      sessions: [],
      programs: [],
      trainings: [],
      bodyweightEntries: []
    };

    service.exportBackup().subscribe(res => {
      expect(res.exportVersion).toBe('1.0');
    });

    const req = httpTesting.expectOne('/api/data/export/backup');
    expect(req.request.method).toBe('GET');
    req.flush(mockBackup);
  });

  it('should export workouts CSV', () => {
    const mockCsv = 'training_id,training_date,training_name\n1,2026-09-21,Leg Day';

    service.exportWorkoutsCsv().subscribe(csv => {
      expect(csv).toContain('training_id');
      expect(csv).toContain('Leg Day');
    });

    const req = httpTesting.expectOne('/api/data/export/workouts.csv');
    expect(req.request.method).toBe('GET');
    req.flush(mockCsv);
  });

  it('should import full backup JSON', () => {
    const mockSummary: ImportSummary = {
      exercisesImported: 5,
      sessionsImported: 2,
      programsImported: 1,
      trainingsImported: 4,
      bodyweightImported: 10,
      totalImported: 22
    };

    const backupData: FullBackupData = {
      exportVersion: '1.0',
      exportedAt: '2026-09-21T18:00:00'
    };

    service.importBackup(backupData).subscribe(summary => {
      expect(summary.totalImported).toBe(22);
      expect(summary.exercisesImported).toBe(5);
    });

    const req = httpTesting.expectOne('/api/data/import/backup');
    expect(req.request.method).toBe('POST');
    req.flush(mockSummary);
  });

  it('should import workouts CSV', () => {
    const mockSummary: ImportSummary = {
      exercisesImported: 0,
      sessionsImported: 0,
      programsImported: 0,
      trainingsImported: 3,
      bodyweightImported: 0,
      totalImported: 3
    };

    service.importWorkoutsCsv('header\nrow1\nrow2').subscribe(summary => {
      expect(summary.trainingsImported).toBe(3);
    });

    const req = httpTesting.expectOne('/api/data/import/workouts.csv');
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Content-Type')).toBe('text/csv');
    req.flush(mockSummary);
  });
});
