import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ProgramAdherenceModalComponent } from './program-adherence-modal.component';
import { ProgramService } from '../../../../core/services/program.service';
import type { ProgramAdherenceResponse, TrainingProgram } from '../../../../core/api/models';

describe('ProgramAdherenceModalComponent', () => {
  let component: ProgramAdherenceModalComponent;
  let fixture: ComponentFixture<ProgramAdherenceModalComponent>;
  let mockProgramService: any;

  const mockProgram: TrainingProgram = {
    id: 1,
    name: '12-Week Push Pull Legs',
    durationWeeks: 12,
    periodizationType: 'LINEAR',
    level: 'INTERMEDIATE',
    workouts: []
  };

  const mockAdherence: ProgramAdherenceResponse = {
    programId: 1,
    programName: '12-Week Push Pull Legs',
    durationWeeks: 12,
    totalScheduledWorkouts: 36,
    completedWorkouts: 18,
    inProgressWorkouts: 0,
    plannedWorkouts: 18,
    missedWorkouts: 0,
    cancelledWorkouts: 0,
    overallCompletionRate: 50.0,
    currentAdherenceRate: 100.0,
    currentStreak: 6,
    longestStreak: 6,
    status: 'ON_TRACK',
    statusDescription: 'Great job! You are currently on track with your periodization plan.',
    weeklyBreakdowns: [
      {
        weekNumber: 1,
        scheduledWorkouts: 3,
        completedWorkouts: 3,
        adherenceRate: 100.0,
        completed: true
      },
      {
        weekNumber: 2,
        scheduledWorkouts: 3,
        completedWorkouts: 3,
        adherenceRate: 100.0,
        completed: true
      }
    ],
    workoutDetails: [
      {
        trainingId: 101,
        workoutName: '12-Week Push Pull Legs - W1D1: Push',
        scheduledDate: '2026-09-01',
        completedDate: '2026-09-01',
        status: 'Completed',
        sessionRpe: 8,
        volumeKg: 4500,
        onTime: true
      }
    ]
  };

  beforeEach(async () => {
    mockProgramService = {
      getProgramAdherence: vi.fn((id: number) => of(mockAdherence))
    };

    await TestBed.configureTestingModule({
      imports: [ProgramAdherenceModalComponent],
      providers: [
        { provide: ProgramService, useValue: mockProgramService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramAdherenceModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('program', mockProgram);
    fixture.detectChanges();
  });

  it('should create and load adherence on init', () => {
    expect(component).toBeTruthy();
    expect(mockProgramService.getProgramAdherence).toHaveBeenCalledWith(1);
    expect(component.adherence()).toEqual(mockAdherence);
    expect(component.isLoading()).toBe(false);
  });

  it('should render adherence status banner and description', () => {
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('.status-banner')?.textContent).toContain('ON TRACK');
    expect(el.querySelector('.status-desc')?.textContent).toContain('Great job! You are currently on track');
    expect(el.querySelector('.status-pill')?.textContent).toContain('18 of 36 Completed');
  });

  it('should render KPI metrics', () => {
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('50%');
    expect(el.textContent).toContain('100%');
    expect(el.textContent).toContain('Current Streak');
    expect(el.textContent).toContain('Peak Streak');
  });

  it('should switch between tabs', () => {
    expect(component.activeTab()).toBe('overview');

    component.activeTab.set('weeks');
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('.weeks-pane')).toBeTruthy();
    expect(el.textContent).toContain('Week 1');
    expect(el.textContent).toContain('100% Compliance');

    component.activeTab.set('workouts');
    fixture.detectChanges();
    expect(el.querySelector('.workouts-pane')).toBeTruthy();
    expect(el.textContent).toContain('12-Week Push Pull Legs - W1D1: Push');
    expect(el.textContent).toContain('✓ On Time');
  });

  it('should handle error when loading adherence fails', () => {
    mockProgramService.getProgramAdherence.mockReturnValueOnce(throwError(() => new Error('Server error')));
    component.loadAdherence();
    fixture.detectChanges();

    expect(component.isLoading()).toBe(false);
    expect(component.errorMessage()).toBe('Failed to load program adherence metrics.');
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('.error-banner')?.textContent).toContain('Failed to load program adherence');
  });

  it('should emit closed when close button is clicked', () => {
    let closedTriggered = false;
    component.closed.subscribe(() => (closedTriggered = true));

    const closeBtn = fixture.nativeElement.querySelector('.btn-close') as HTMLButtonElement;
    closeBtn.click();

    expect(closedTriggered).toBe(true);
  });
});
