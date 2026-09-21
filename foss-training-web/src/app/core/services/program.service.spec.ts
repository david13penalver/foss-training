import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ProgramService } from './program.service';
import type { Training, TrainingProgram, TrainingProgramRequest } from '../api/models';

describe('ProgramService', () => {
  let service: ProgramService;
  let httpTesting: HttpTestingController;

  const mockProgram: TrainingProgram = {
    id: 1,
    name: '12-Week Push Pull Legs',
    description: 'Hypertrophy focused periodization template',
    durationWeeks: 12,
    periodizationType: 'LINEAR',
    level: 'INTERMEDIATE',
    isActive: true,
    workouts: []
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ProgramService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ProgramService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all programs', () => {
    service.getAllPrograms().subscribe(programs => {
      expect(programs).toEqual([mockProgram]);
    });

    const req = httpTesting.expectOne('/api/programs');
    expect(req.request.method).toBe('GET');
    req.flush([mockProgram]);
  });

  it('should fetch program by id', () => {
    service.getProgramById(1).subscribe(prog => {
      expect(prog.id).toBe(1);
    });

    const req = httpTesting.expectOne('/api/programs/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockProgram);
  });

  it('should create program', () => {
    const request: TrainingProgramRequest = {
      name: '12-Week Push Pull Legs',
      durationWeeks: 12,
      periodizationType: 'LINEAR',
      level: 'INTERMEDIATE'
    };

    service.createProgram(request).subscribe(prog => {
      expect(prog.id).toBe(1);
    });

    const req = httpTesting.expectOne('/api/programs');
    expect(req.request.method).toBe('POST');
    req.flush(mockProgram);
  });

  it('should update program', () => {
    const request: TrainingProgramRequest = {
      id: 1,
      name: 'Updated PPL',
      durationWeeks: 16,
      periodizationType: 'UNDULATING',
      level: 'ADVANCED'
    };

    service.updateProgram(1, request).subscribe(prog => {
      expect(prog.name).toBe('Updated PPL');
    });

    const req = httpTesting.expectOne('/api/programs/1');
    expect(req.request.method).toBe('PUT');
    req.flush({ ...mockProgram, name: 'Updated PPL' });
  });

  it('should delete program', () => {
    service.deleteProgram(1).subscribe();

    const req = httpTesting.expectOne('/api/programs/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should check program exists', () => {
    service.checkProgramExists(1).subscribe(exists => {
      expect(exists).toBe(true);
    });

    const req = httpTesting.expectOne('/api/programs/1/exists');
    expect(req.request.method).toBe('GET');
    req.flush(true);
  });

  it('should generate schedule', () => {
    const mockTrainings: Training[] = [
      { id: 101, name: 'Day 1 - Push', status: 'Planned' }
    ];

    service.generateSchedule(1, '2026-10-05').subscribe(trainings => {
      expect(trainings.length).toBe(1);
      expect(trainings[0].id).toBe(101);
    });

    const req = httpTesting.expectOne('/api/programs/1/generate-schedule?startDate=2026-10-05');
    expect(req.request.method).toBe('POST');
    req.flush(mockTrainings);
  });
});
