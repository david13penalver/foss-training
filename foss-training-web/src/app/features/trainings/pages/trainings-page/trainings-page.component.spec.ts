import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal, WritableSignal } from '@angular/core';
import { of } from 'rxjs';
import { TrainingsPageComponent } from './trainings-page.component';
import { TrainingService } from '../../../../core/services/training.service';
import { SessionService } from '../../../../core/services/session.service';
import type { Training, TrainingRequest, Session } from '../../../../core/api/models';

describe('TrainingsPageComponent', () => {
  let component: TrainingsPageComponent;
  let fixture: ComponentFixture<TrainingsPageComponent>;
  let mockTrainingService: any;
  let mockSessionService: any;
  let trainingsSignal: WritableSignal<Training[]>;

  const mockTrainings: Training[] = [
    {
      id: 1,
      name: 'Push Strength Session',
      status: 'In Progress',
      trainingDate: '2026-09-10',
      totalVolume: 4200,
      session: {
        id: 10,
        name: 'Push Routine',
        sessionExercises: [
          {
            id: 101,
            exerciseType: 'ResistanceSessionExercise',
            exercise: { name: 'Bench Press' }
          }
        ]
      }
    },
    {
      id: 2,
      name: 'Leg Hypertrophy Session',
      status: 'Completed',
      trainingDate: '2026-09-08',
      totalVolume: 7800,
      session: {
        id: 20,
        name: 'Leg Routine',
        sessionExercises: []
      }
    },
    {
      id: 3,
      name: 'Pull Recovery Session',
      status: 'Planned',
      trainingDate: '2026-09-12',
      totalVolume: 0,
      session: {
        id: 30,
        name: 'Pull Routine',
        sessionExercises: []
      }
    }
  ];

  beforeEach(async () => {
    trainingsSignal = signal<Training[]>([...mockTrainings]);

    mockTrainingService = {
      trainingsResource: {
        value: trainingsSignal,
        isLoading: signal(false),
        error: signal(undefined),
        reload: vi.fn()
      },
      createTraining: vi.fn((req: TrainingRequest) => of({ ...req, id: 99 })),
      updateTraining: vi.fn((id: number, req: TrainingRequest) => of({ ...req, id })),
      deleteTraining: vi.fn((id: number) => of(void 0)),
      startTraining: vi.fn((id: number) => of({ id, status: 'In Progress' } as Training)),
      completeTraining: vi.fn((id: number) => of({ id, status: 'Completed' } as Training)),
      cancelTraining: vi.fn((id: number) => of({ id, status: 'Cancelled' } as Training)),
      getWorkoutSummary: vi.fn((id: number) =>
        of({
          trainingId: id,
          trainingName: 'Push Strength Session',
          status: 'Completed',
          durationMinutes: 52,
          totalVolumeKg: 4200,
          totalWorkingSets: 8,
          totalReps: 64,
          averageIntensityRpe: 8.0,
          exerciseSummaries: []
        })
      )
    };

    mockSessionService = {
      sessionsResource: {
        value: signal<Session[]>([]),
        isLoading: signal(false),
        error: signal(undefined),
        reload: vi.fn()
      },
      startTrainingFromSession: vi.fn((id: number, date?: string, name?: string) =>
        of({ id: 88, status: 'Planned' } as Training)
      )
    };

    await TestBed.configureTestingModule({
      imports: [TrainingsPageComponent],
      providers: [
        { provide: TrainingService, useValue: mockTrainingService },
        { provide: SessionService, useValue: mockSessionService },
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TrainingsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the trainings page', () => {
    expect(component).toBeTruthy();
  });

  it('should compute KPI metrics correctly', () => {
    expect(component.activeCount()).toBe(1);
    expect(component.completedCount()).toBe(1);
    expect(component.totalVolume()).toBe(12000);
  });

  it('should filter trainings by search query', () => {
    component.searchQuery.set('Leg');
    fixture.detectChanges();

    expect(component.filteredTrainings().length).toBe(1);
    expect(component.filteredTrainings()[0].name).toBe('Leg Hypertrophy Session');
  });

  it('should filter trainings by status', () => {
    component.selectedStatus.set('In Progress');
    fixture.detectChanges();

    expect(component.filteredTrainings().length).toBe(1);
    expect(component.filteredTrainings()[0].name).toBe('Push Strength Session');
  });

  it('should open schedule modal', () => {
    component.openScheduleModal();
    fixture.detectChanges();

    expect(component.isScheduleOpen()).toBe(true);
  });

  it('should open active tracker modal when starting/resuming workout', () => {
    component.handleStart(mockTrainings[0]);
    fixture.detectChanges();

    expect(component.isActiveTrackerOpen()).toBe(true);
    expect(component.activeWorkout()?.id).toBe(1);
  });

  it('should call completeTraining and open summary modal when finishWorkout is called', () => {
    component.handleComplete(mockTrainings[0]);
    expect(mockTrainingService.completeTraining).toHaveBeenCalledWith(1);
    expect(component.notificationMessage()).toContain('Completed');
    expect(component.isSummaryOpen()).toBe(true);
    expect(component.summaryWorkoutId()).toBe(1);
  });

  it('should pass RPE and notes payload to completeTraining when present', () => {
    const trainingWithFeedback: Training = {
      ...mockTrainings[0],
      rpe: { value: 8.5 },
      notes: 'Strong session with great chest pump'
    };

    component.handleComplete(trainingWithFeedback);
    expect(mockTrainingService.completeTraining).toHaveBeenCalledWith(1, {
      rpe: { value: 8.5 },
      notes: 'Strong session with great chest pump'
    });
    expect(component.isSummaryOpen()).toBe(true);
  });

  it('should open summary modal when handleViewSummary is called', () => {
    component.handleViewSummary(mockTrainings[1]);
    expect(component.isSummaryOpen()).toBe(true);
    expect(component.summaryWorkoutId()).toBe(2);
  });

  it('should filter trainings by Paused status and count them in activeCount', () => {
    const pausedTraining: Training = {
      id: 4,
      name: 'Paused Circuit',
      status: 'Paused',
      trainingDate: '2026-09-11',
      totalVolume: 1500
    };
    trainingsSignal.set([...mockTrainings, pausedTraining]);
    fixture.detectChanges();

    expect(component.activeCount()).toBe(2); // 1 In Progress + 1 Paused

    component.selectedStatus.set('Paused');
    fixture.detectChanges();

    expect(component.filteredTrainings().length).toBe(1);
    expect(component.filteredTrainings()[0].name).toBe('Paused Circuit');
  });

  it('should call cancelTraining when cancelWorkout is called', () => {
    component.handleCancel(mockTrainings[0]);
    expect(mockTrainingService.cancelTraining).toHaveBeenCalledWith(1);
  });

  it('should open delete confirm and call deleteTraining', () => {
    component.openDeleteConfirm(mockTrainings[0]);
    fixture.detectChanges();

    expect(component.isDeleteConfirmOpen()).toBe(true);
    component.confirmDelete();
    expect(mockTrainingService.deleteTraining).toHaveBeenCalledWith(1);
  });

  it('should filter trainings by date range', () => {
    component.onStartDateChange('2026-09-09');
    component.onEndDateChange('2026-09-11');
    fixture.detectChanges();

    expect(component.filteredTrainings().length).toBe(1);
    expect(component.filteredTrainings()[0].name).toBe('Push Strength Session');
  });

  it('should apply date presets correctly', () => {
    component.applyDatePreset('ALL');
    expect(component.startDate()).toBe('');
    expect(component.endDate()).toBe('');

    component.applyDatePreset('THIS_WEEK');
    expect(component.startDate()).toBeTruthy();
    expect(component.endDate()).toBeTruthy();

    component.applyDatePreset('THIS_MONTH');
    expect(component.startDate()).toContain('-01');
  });

  it('should paginate trainings and navigate pages', () => {
    component.onPageSizeChange(1);
    fixture.detectChanges();

    expect(component.totalPages()).toBe(3);
    expect(component.currentPage()).toBe(0);
    expect(component.paginatedTrainings().length).toBe(1);
    expect(component.paginatedTrainings()[0].name).toBe('Push Strength Session');

    component.nextPage();
    fixture.detectChanges();
    expect(component.currentPage()).toBe(1);
    expect(component.paginatedTrainings()[0].name).toBe('Leg Hypertrophy Session');

    component.prevPage();
    fixture.detectChanges();
    expect(component.currentPage()).toBe(0);

    component.goToPage(2);
    fixture.detectChanges();
    expect(component.currentPage()).toBe(2);
    expect(component.paginatedTrainings()[0].name).toBe('Pull Recovery Session');
  });
});
