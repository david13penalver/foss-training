import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal, WritableSignal } from '@angular/core';
import { of } from 'rxjs';
import { SessionsPageComponent } from './sessions-page.component';
import { SessionService } from '../../../../core/services/session.service';
import { ExerciseService } from '../../../../core/services/exercise.service';
import type { Session, SessionRequest, Exercise, Training } from '../../../../core/api/models';

describe('SessionsPageComponent', () => {
  let component: SessionsPageComponent;
  let fixture: ComponentFixture<SessionsPageComponent>;
  let mockSessionService: any;
  let mockExerciseService: any;
  let sessionsSignal: WritableSignal<Session[]>;

  const mockSessions: Session[] = [
    {
      id: 1,
      name: 'Push Strength Focus',
      description: 'Heavy chest and shoulder routine',
      sessionStatus: 'Ready to Start',
      sessionExercises: [
        {
          id: 10,
          exerciseType: 'ResistanceSessionExercise',
          orderIndex: 1,
          exercise: { id: 1, name: 'Barbell Bench Press', primaryCategory: 'RESISTANCE' }
        }
      ]
    },
    {
      id: 2,
      name: 'Pull Hypertrophy Routine',
      description: 'Back, lats, and biceps volume',
      sessionStatus: 'Planned',
      sessionExercises: []
    }
  ];

  const mockExercises: Exercise[] = [
    { id: 1, name: 'Barbell Bench Press', primaryCategory: 'RESISTANCE' }
  ];

  beforeEach(async () => {
    sessionsSignal = signal<Session[]>([...mockSessions]);

    mockSessionService = {
      sessionsResource: {
        value: sessionsSignal,
        isLoading: signal(false),
        error: signal(undefined),
        reload: vi.fn()
      },
      createSession: vi.fn((req: SessionRequest) => of({ ...req, id: 99 })),
      updateSession: vi.fn((id: number, req: SessionRequest) => of({ ...req, id })),
      deleteSession: vi.fn((id: number) => of(void 0)),
      cloneSession: vi.fn((id: number) => of({ id: 99, name: 'Push Strength Focus (Copy)' } as Session)),
      startTrainingFromSession: vi.fn((id: number) => of({ id: 55, status: 'In Progress' } as Training))
    };

    mockExerciseService = {
      exercisesResource: {
        value: signal(mockExercises),
        isLoading: signal(false),
        error: signal(undefined),
        reload: vi.fn()
      }
    };

    await TestBed.configureTestingModule({
      imports: [SessionsPageComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: ExerciseService, useValue: mockExerciseService },
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SessionsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the sessions page', () => {
    expect(component).toBeTruthy();
  });

  it('should render all sessions initially', () => {
    expect(component.filteredSessions().length).toBe(2);
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelectorAll('app-session-card').length).toBe(2);
  });

  it('should filter sessions by search query', () => {
    component.searchQuery.set('push');
    fixture.detectChanges();

    expect(component.filteredSessions().length).toBe(1);
    expect(component.filteredSessions()[0].name).toBe('Push Strength Focus');
  });

  it('should filter sessions by status', () => {
    component.selectedStatus.set('Planned');
    fixture.detectChanges();

    expect(component.filteredSessions().length).toBe(1);
    expect(component.filteredSessions()[0].name).toBe('Pull Hypertrophy Routine');
  });

  it('should open builder modal for creating a new session', () => {
    component.openCreateModal();
    fixture.detectChanges();

    expect(component.isBuilderOpen()).toBe(true);
    expect(component.editingSession()).toBeNull();
  });

  it('should open builder modal in edit mode', () => {
    component.openEditModal(mockSessions[0]);
    fixture.detectChanges();

    expect(component.isBuilderOpen()).toBe(true);
    expect(component.editingSession()?.id).toBe(1);
  });

  it('should open detail modal', () => {
    component.openDetailModal(mockSessions[0]);
    fixture.detectChanges();

    expect(component.isDetailOpen()).toBe(true);
    expect(component.activeSession()?.id).toBe(1);
  });

  it('should open delete confirm and call deleteSession', () => {
    component.openDeleteConfirm(mockSessions[0]);
    fixture.detectChanges();

    expect(component.isDeleteConfirmOpen()).toBe(true);
    expect(component.sessionToDelete()?.id).toBe(1);

    component.confirmDelete();
    expect(mockSessionService.deleteSession).toHaveBeenCalledWith(1);
  });

  it('should call startTrainingFromSession when start workout is triggered', () => {
    component.handleStartTraining(mockSessions[0]);
    expect(mockSessionService.startTrainingFromSession).toHaveBeenCalledWith(1);
    expect(component.notificationMessage()).toContain('Push Strength Focus');
  });

  it('should call cloneSession when duplicating a session', () => {
    component.handleClone(mockSessions[0]);
    expect(mockSessionService.cloneSession).toHaveBeenCalledWith(1);
    expect(mockSessionService.sessionsResource.reload).toHaveBeenCalled();
    expect(component.notificationMessage()).toContain('duplicated as "Push Strength Focus (Copy)"');
  });
});
