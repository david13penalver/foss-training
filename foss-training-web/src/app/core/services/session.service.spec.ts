import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { SessionService } from './session.service';
import type { Session, SessionRequest } from '../api/models';

describe('SessionService', () => {
  let service: SessionService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SessionService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(SessionService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get session by id', () => {
    const mockSession: Session = {
      id: 10,
      name: 'Push Routine A',
      sessionStatus: 'Draft'
    };

    service.getSessionById(10).subscribe(session => {
      expect(session).toEqual(mockSession);
    });

    const req = httpTesting.expectOne('/api/sessions/10');
    expect(req.request.method).toBe('GET');
    req.flush(mockSession);
  });

  it('should create a new session', () => {
    const request: SessionRequest = {
      name: 'Leg Day Volume',
      sessionStatus: 'Planned'
    };
    const createdSession: Session = {
      id: 11,
      name: 'Leg Day Volume',
      sessionStatus: 'Planned'
    };

    service.createSession(request).subscribe(res => {
      expect(res.id).toBe(11);
    });

    const req = httpTesting.expectOne('/api/sessions');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(createdSession);
  });

  it('should update an existing session', () => {
    const request: SessionRequest = {
      id: 11,
      name: 'Leg Day Volume Updated',
      sessionStatus: 'Completed'
    };

    service.updateSession(11, request).subscribe(res => {
      expect(res.name).toBe('Leg Day Volume Updated');
    });

    const req = httpTesting.expectOne('/api/sessions/11');
    expect(req.request.method).toBe('PUT');
    req.flush(request);
  });

  it('should delete a session', () => {
    service.deleteSession(11).subscribe();

    const req = httpTesting.expectOne('/api/sessions/11');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should trigger start training from session', () => {
    service.startTrainingFromSession(10).subscribe();

    const req = httpTesting.expectOne('/api/trainings/from-session/10');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 101, status: 'In Progress' });
  });
});
