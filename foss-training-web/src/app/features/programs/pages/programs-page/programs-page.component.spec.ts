import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProgramsPageComponent } from './programs-page.component';
import { ProgramService } from '../../../../core/services/program.service';
import { SessionService } from '../../../../core/services/session.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import type { TrainingProgram } from '../../../../core/api/models';

describe('ProgramsPageComponent', () => {
  let component: ProgramsPageComponent;
  let fixture: ComponentFixture<ProgramsPageComponent>;
  let mockProgramService: any;
  let mockSessionService: any;

  const mockPrograms: TrainingProgram[] = [
    {
      id: 1,
      name: '12-Week Push Pull Legs',
      durationWeeks: 12,
      level: 'INTERMEDIATE',
      periodizationType: 'LINEAR',
      workouts: []
    },
    {
      id: 2,
      name: 'Starting Strength',
      durationWeeks: 8,
      level: 'BEGINNER',
      periodizationType: 'LINEAR',
      workouts: []
    }
  ];

  beforeEach(async () => {
    mockProgramService = {
      programsResource: {
        value: signal(mockPrograms),
        isLoading: signal(false),
        reload: vi.fn()
      },
      deleteProgram: vi.fn(() => of(void 0)),
      cloneProgram: vi.fn((id: number) => of({ ...mockPrograms[0], id: 99, name: '12-Week Push Pull Legs (Copy)' })),
      getProgramAdherence: vi.fn((id: number) => of({
        programId: id,
        overallCompletionRate: 50.0,
        status: 'ON_TRACK',
        weeklyBreakdowns: [],
        workoutDetails: []
      }))
    };

    mockSessionService = {
      sessionsResource: {
        value: signal([])
      }
    };

    await TestBed.configureTestingModule({
      imports: [ProgramsPageComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ProgramService, useValue: mockProgramService },
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the programs page', () => {
    expect(component).toBeTruthy();
  });

  it('should list all programs initially', () => {
    expect(component.filteredPrograms().length).toBe(2);
  });

  it('should filter by level', () => {
    component.selectedLevel.set('BEGINNER');
    expect(component.filteredPrograms().length).toBe(1);
    expect(component.filteredPrograms()[0].name).toBe('Starting Strength');
  });

  it('should filter by search query', () => {
    component.searchQuery.set('push');
    expect(component.filteredPrograms().length).toBe(1);
    expect(component.filteredPrograms()[0].name).toBe('12-Week Push Pull Legs');
  });

  it('should open and close builder modal', () => {
    component.openCreateModal();
    expect(component.isBuilderOpen()).toBe(true);
    expect(component.programForBuilder()).toBeNull();
  });

  it('should clone program when handleCloneProgram is called', () => {
    component.handleCloneProgram(mockPrograms[0]);
    expect(mockProgramService.cloneProgram).toHaveBeenCalledWith(1);
    expect(mockProgramService.programsResource.reload).toHaveBeenCalled();
    expect(component.notificationMessage()).toContain('duplicated as "12-Week Push Pull Legs (Copy)"');
  });

  it('should open adherence modal when openAdherenceModal is called', () => {
    component.openAdherenceModal(mockPrograms[0]);
    expect(component.selectedProgramForAdherence()?.id).toBe(1);

    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('app-program-adherence-modal')).toBeTruthy();
  });
});
