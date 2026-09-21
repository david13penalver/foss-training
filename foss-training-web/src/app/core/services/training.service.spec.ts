import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TrainingService } from './training.service';
import type { Training, TrainingRequest } from '../api/models';

describe('TrainingService', () => {
  let service: TrainingService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TrainingService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(TrainingService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get training by id', () => {
    const mockTraining: Training = {
      id: 5,
      name: 'Evening Hypertrophy',
      trainingDate: '2026-09-10',
      status: 'Planned'
    };

    service.getTrainingById(5).subscribe(training => {
      expect(training).toEqual(mockTraining);
    });

    const req = httpTesting.expectOne('/api/trainings/5');
    expect(req.request.method).toBe('GET');
    req.flush(mockTraining);
  });

  it('should create a new training', () => {
    const request: TrainingRequest = {
      name: 'Morning Push',
      trainingDate: '2026-09-11',
      session: {
        name: 'Morning Push Template',
        sessionStatus: 'Planned'
      },
      status: 'Planned'
    };
    const createdTraining: Training = {
      id: 6,
      name: 'Morning Push',
      trainingDate: '2026-09-11',
      status: 'Planned'
    };

    service.createTraining(request).subscribe(res => {
      expect(res.id).toBe(6);
    });

    const req = httpTesting.expectOne('/api/trainings');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(createdTraining);
  });

  it('should update an existing training', () => {
    const request: TrainingRequest = {
      id: 6,
      name: 'Morning Push Updated',
      trainingDate: '2026-09-11',
      session: {
        name: 'Morning Push Template',
        sessionStatus: 'Planned'
      },
      status: 'In Progress'
    };

    service.updateTraining(6, request).subscribe(res => {
      expect(res.name).toBe('Morning Push Updated');
    });

    const req = httpTesting.expectOne('/api/trainings/6');
    expect(req.request.method).toBe('PUT');
    req.flush(request);
  });

  it('should delete a training', () => {
    service.deleteTraining(6).subscribe();

    const req = httpTesting.expectOne('/api/trainings/6');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should start a training', () => {
    service.startTraining(6).subscribe(res => {
      expect(res.status).toBe('In Progress');
    });

    const req = httpTesting.expectOne('/api/trainings/6/start');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 6, status: 'In Progress' });
  });

  it('should complete a training', () => {
    service.completeTraining(6).subscribe(res => {
      expect(res.status).toBe('Completed');
    });

    const req = httpTesting.expectOne('/api/trainings/6/complete');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 6, status: 'Completed' });
  });

  it('should cancel a training', () => {
    service.cancelTraining(6).subscribe(res => {
      expect(res.status).toBe('Cancelled');
    });

    const req = httpTesting.expectOne('/api/trainings/6/cancel');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 6, status: 'Cancelled' });
  });

  it('should check training exists', () => {
    service.checkTrainingExists(6).subscribe(exists => {
      expect(exists).toBe(true);
    });

    const req = httpTesting.expectOne('/api/trainings/6/exists');
    expect(req.request.method).toBe('GET');
    req.flush(true);
  });

  it('should create training from session', () => {
    service.createTrainingFromSession(10, '2026-09-22', 'Session Run').subscribe(res => {
      expect(res.id).toBe(99);
    });

    const req = httpTesting.expectOne('/api/trainings/from-session/10?date=2026-09-22&customName=Session%20Run');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 99, name: 'Session Run' });
  });

  it('should pause a training', () => {
    service.pauseTraining(6).subscribe(res => {
      expect(res.status).toBe('Paused');
    });

    const req = httpTesting.expectOne('/api/trainings/6/pause');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 6, status: 'Paused' });
  });

  it('should resume a training', () => {
    service.resumeTraining(6).subscribe(res => {
      expect(res.status).toBe('In Progress');
    });

    const req = httpTesting.expectOne('/api/trainings/6/resume');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 6, status: 'In Progress' });
  });

  it('should complete a training with RPE and notes', () => {
    const payload = { rpe: { value: 8.5 }, notes: 'Great pump' };
    service.completeTraining(6, payload).subscribe(res => {
      expect(res.status).toBe('Completed');
    });

    const req = httpTesting.expectOne('/api/trainings/6/complete');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ id: 6, status: 'Completed', notes: 'Great pump' });
  });

  it('should log a resistance set', () => {
    const logReq = { weight: { value: 100, unit: 'KG' as const }, repetitions: 8, completed: true };
    service.logSet(6, 101, logReq).subscribe(res => {
      expect(res.id).toBe(6);
    });

    const req = httpTesting.expectOne('/api/trainings/6/exercises/101/sets');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(logReq);
    req.flush({ id: 6 });
  });

  it('should update a resistance set', () => {
    const updateReq = { setNumber: 1, weight: { value: 105, unit: 'KG' as const }, repetitions: 8, completed: true };
    service.updateSet(6, 101, 1, updateReq).subscribe(res => {
      expect(res.id).toBe(6);
    });

    const req = httpTesting.expectOne('/api/trainings/6/exercises/101/sets/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updateReq);
    req.flush({ id: 6 });
  });

  it('should delete a resistance set', () => {
    service.deleteSet(6, 101, 1).subscribe(res => {
      expect(res.id).toBe(6);
    });

    const req = httpTesting.expectOne('/api/trainings/6/exercises/101/sets/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ id: 6 });
  });

  it('should get workout summary', () => {
    const mockSummary = {
      trainingId: 6,
      trainingName: 'Hypertrophy A',
      status: 'COMPLETED' as const,
      totalVolumeKg: 12000,
      totalWorkingSets: 12,
      totalReps: 96
    };

    service.getWorkoutSummary(6).subscribe(res => {
      expect(res.trainingId).toBe(6);
      expect(res.totalVolumeKg).toBe(12000);
    });

    const req = httpTesting.expectOne('/api/trainings/6/summary');
    expect(req.request.method).toBe('GET');
    req.flush(mockSummary);
  });

  it('should search trainings with query parameters', () => {
    const mockPage = {
      content: [{ id: 1, name: 'Morning Push', status: 'Planned' }],
      page: 0,
      size: 10,
      totalElements: 1,
      totalPages: 1
    };

    service.searchTrainings({ search: 'push', status: 'Planned', page: 0, size: 10 }).subscribe(res => {
      expect(res.totalElements).toBe(1);
      expect(res.content?.[0].name).toBe('Morning Push');
    });

    const req = httpTesting.expectOne('/api/trainings/search?status=Planned&search=push&page=0&size=10');
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });

  it('should get trainings with date range parameters', () => {
    const mockList = [{ id: 1, name: 'Mid Workout', trainingDate: '2026-09-10' }];

    service.getTrainings({ startDate: '2026-09-01', endDate: '2026-09-15' }).subscribe(res => {
      expect(res.length).toBe(1);
      expect(res[0].name).toBe('Mid Workout');
    });

    const req = httpTesting.expectOne('/api/trainings?startDate=2026-09-01&endDate=2026-09-15');
    expect(req.request.method).toBe('GET');
    req.flush(mockList);
  });
});
