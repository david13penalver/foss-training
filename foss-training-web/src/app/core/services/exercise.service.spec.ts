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

  it('should search exercises with query parameters', () => {
    const mockPage = {
      content: [{ id: 1, name: 'Squat', primaryCategory: 'RESISTANCE' }],
      page: 0,
      size: 10,
      totalElements: 1,
      totalPages: 1
    };

    service.searchExercises({ search: 'squat', primaryCategory: 'RESISTANCE', page: 0, size: 10 }).subscribe(res => {
      expect(res.totalElements).toBe(1);
      expect(res.content?.[0].name).toBe('Squat');
    });

    const req = httpTesting.expectOne('/api/exercises/search?search=squat&primaryCategory=RESISTANCE&page=0&size=10');
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });

  it('should fetch filtered exercises list with query parameters', () => {
    const mockList: Exercise[] = [{ id: 1, name: 'Deadlift', primaryCategory: 'RESISTANCE' }];

    service.getExercises({ primaryCategory: 'RESISTANCE' }).subscribe(res => {
      expect(res.length).toBe(1);
      expect(res[0].name).toBe('Deadlift');
    });

    const req = httpTesting.expectOne('/api/exercises?primaryCategory=RESISTANCE');
    expect(req.request.method).toBe('GET');
    req.flush(mockList);
  });
});
