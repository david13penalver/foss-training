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
});
