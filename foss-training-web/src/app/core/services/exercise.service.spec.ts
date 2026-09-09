import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ExerciseService } from './exercise.service';
import type { Exercise } from '../api/models';

describe('ExerciseService', () => {
  let service: ExerciseService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ExerciseService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ExerciseService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch exercise by id', () => {
    const mockExercise: Exercise = {
      id: 1,
      name: 'Bench Press',
      primaryCategory: 'RESISTANCE',
      active: true
    };

    service.getExerciseById(1).subscribe(exercise => {
      expect(exercise).toEqual(mockExercise);
    });

    const req = httpTesting.expectOne('/api/exercises/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockExercise);
  });
});
