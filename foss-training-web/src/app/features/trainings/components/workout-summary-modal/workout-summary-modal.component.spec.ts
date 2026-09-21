import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { WorkoutSummaryModalComponent } from './workout-summary-modal.component';
import { TrainingService } from '../../../../core/services/training.service';
import type { WorkoutSummaryResponse } from '../../../../core/api/models';

describe('WorkoutSummaryModalComponent', () => {
  let component: WorkoutSummaryModalComponent;
  let fixture: ComponentFixture<WorkoutSummaryModalComponent>;
  let mockTrainingService: any;

  const mockSummary: WorkoutSummaryResponse = {
    trainingId: 5,
    trainingName: 'Leg Day Volume',
    status: 'Completed',
    formattedDuration: '1h 10m 00s',
    totalVolumeKg: 12500,
    totalWorkingSets: 14,
    totalReps: 112,
    sessionRpe: 8.5,
    notes: 'Great energy today, achieved new squat PR!',
    exerciseSummaries: [
      {
        exerciseId: 1,
        exerciseName: 'Barbell Back Squat',
        category: 'RESISTANCE',
        completedSets: 4,
        totalReps: 32,
        topWeightKg: 140,
        volumeKg: 4480,
        estimated1RmKg: 163.3
      }
    ]
  };

  beforeEach(async () => {
    mockTrainingService = {
      getWorkoutSummary: vi.fn((id: number) => of(mockSummary))
    };

    await TestBed.configureTestingModule({
      imports: [WorkoutSummaryModalComponent],
      providers: [
        { provide: TrainingService, useValue: mockTrainingService },
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkoutSummaryModalComponent);
    component = fixture.componentInstance;
  });

  it('should create the workout summary modal', () => {
    fixture.componentRef.setInput('summary', mockSummary);
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should render workout metrics from input summary', () => {
    fixture.componentRef.setInput('summary', mockSummary);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.modal-title')?.textContent).toContain('Leg Day Volume');
    expect(compiled.textContent).toContain('1h 10m 00s');
    expect(compiled.textContent).toContain('12,500');
    expect(compiled.textContent).toContain('14');
    expect(compiled.textContent).toContain('112');
    expect(compiled.textContent).toContain('RPE 8.5');
    expect(compiled.textContent).toContain('Barbell Back Squat');
    expect(compiled.textContent).toContain('163.3 kg');
  });

  it('should fetch summary by trainingId if summary input is null', () => {
    fixture.componentRef.setInput('trainingId', 5);
    fixture.detectChanges();

    expect(mockTrainingService.getWorkoutSummary).toHaveBeenCalledWith(5);
    expect(component.loadedSummary()?.trainingName).toBe('Leg Day Volume');
  });

  it('should emit close when close button is clicked', () => {
    fixture.componentRef.setInput('summary', mockSummary);
    fixture.detectChanges();

    let closed = false;
    component.close.subscribe(() => (closed = true));

    const closeBtn = fixture.nativeElement.querySelector('.btn-close') as HTMLButtonElement;
    closeBtn.click();

    expect(closed).toBe(true);
  });
});
