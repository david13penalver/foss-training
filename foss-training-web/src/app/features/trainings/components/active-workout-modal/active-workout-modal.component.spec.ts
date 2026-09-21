import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ActiveWorkoutModalComponent } from './active-workout-modal.component';
import { TrainingService } from '../../../../core/services/training.service';
import type { Training } from '../../../../core/api/models';

describe('ActiveWorkoutModalComponent', () => {
  let component: ActiveWorkoutModalComponent;
  let fixture: ComponentFixture<ActiveWorkoutModalComponent>;
  let mockTrainingService: any;

  const mockTraining: Training = {
    id: 1,
    name: 'Push Day Active',
    status: 'In Progress',
    trainingDate: '2026-09-10',
    session: {
      id: 10,
      name: 'Push Routine',
      sessionExercises: [
        {
          id: 101,
          exerciseType: 'ResistanceSessionExercise',
          orderIndex: 1,
          exercise: { id: 1, name: 'Barbell Bench Press', primaryCategory: 'RESISTANCE' },
          sets: [
            { setNumber: 1, setType: 'WARMUP', repetitions: 10, weight: { value: 60, unit: 'KG' } },
            { setNumber: 2, setType: 'WORKING', repetitions: 8, weight: { value: 100, unit: 'KG' } }
          ]
        }
      ]
    }
  };

  beforeEach(async () => {
    mockTrainingService = {
      pauseTraining: vi.fn((id: number) => of({ ...mockTraining, id, status: 'Paused' })),
      resumeTraining: vi.fn((id: number) => of({ ...mockTraining, id, status: 'In Progress' })),
      logSet: vi.fn(() => of(mockTraining)),
      deleteSet: vi.fn(() => of(mockTraining))
    };

    await TestBed.configureTestingModule({
      imports: [ActiveWorkoutModalComponent],
      providers: [
        { provide: TrainingService, useValue: mockTrainingService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ActiveWorkoutModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('training', mockTraining);
    fixture.detectChanges();
  });

  it('should create the active workout modal', () => {
    expect(component).toBeTruthy();
  });

  it('should display training title and exercises', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.modal-title')?.textContent).toContain('Push Day Active');
    expect(compiled.textContent).toContain('Barbell Bench Press');
  });

  it('should display set rows with reps and weight', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const repInputs = compiled.querySelectorAll('input.reps-input') as NodeListOf<HTMLInputElement>;
    expect(repInputs.length).toBe(2);
    expect(repInputs[0].value).toBe('10');
    expect(repInputs[1].value).toBe('8');
  });

  it('should allow toggling set completion checkbox', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const checkboxes = compiled.querySelectorAll('input[type="checkbox"].set-check') as NodeListOf<HTMLInputElement>;
    expect(checkboxes.length).toBe(2);

    expect(component.isSetCompleted(101, 1)).toBe(false);

    checkboxes[0].click();
    fixture.detectChanges();

    expect(component.isSetCompleted(101, 1)).toBe(true);
  });

  it('should emit finishWorkout and completeWithDetails when Finish Workout button is clicked', () => {
    let finishedTraining: Training | undefined;
    let finishedDetails: { training: Training; rpe?: number; notes?: string } | undefined;

    component.finishWorkout.subscribe((t: Training) => (finishedTraining = t));
    component.completeWithDetails.subscribe((d) => (finishedDetails = d));

    component.sessionRpe.set(9.0);
    component.sessionNotes.set('Pushed through last rep!');
    fixture.detectChanges();

    const finishBtn = fixture.nativeElement.querySelector('.btn-finish') as HTMLButtonElement;
    finishBtn.click();

    expect(finishedTraining?.id).toBe(1);
    expect(finishedTraining?.notes).toBe('Pushed through last rep!');
    expect(finishedTraining?.rpe?.value).toBe(9.0);

    expect(finishedDetails?.rpe).toBe(9.0);
    expect(finishedDetails?.notes).toBe('Pushed through last rep!');
  });

  it('should toggle pause and resume workout', () => {
    component.togglePause();
    expect(mockTrainingService.pauseTraining).toHaveBeenCalledWith(1);
    expect(component.isPaused()).toBe(true);

    component.togglePause();
    expect(mockTrainingService.resumeTraining).toHaveBeenCalledWith(1);
    expect(component.isPaused()).toBe(false);
  });

  it('should add set and sync to backend', () => {
    const initialSetsCount = component.editableExercises()[0].sets.length;
    component.addSet(0);

    expect(component.editableExercises()[0].sets.length).toBe(initialSetsCount + 1);
    expect(mockTrainingService.logSet).toHaveBeenCalled();
  });

  it('should delete set and sync to backend', () => {
    component.deleteSet(0, 0);

    expect(component.editableExercises()[0].sets.length).toBe(1);
    expect(mockTrainingService.deleteSet).toHaveBeenCalledWith(1, 1, 1);
  });

  it('should start rest timer when completing a set', () => {
    component.toggleSet(0, 0);
    expect(component.restTimerSeconds()).toBe(90);

    component.addRestTime(30);
    expect(component.restTimerSeconds()).toBe(120);

    component.stopRestTimer();
    expect(component.restTimerSeconds()).toBeNull();
  });

  it('should emit cancelWorkout when Cancel Workout button is clicked', () => {
    let cancelledTraining: Training | undefined;
    component.cancelWorkout.subscribe((t: Training) => (cancelledTraining = t));

    const cancelBtn = fixture.nativeElement.querySelector('.btn-cancel-workout') as HTMLButtonElement;
    cancelBtn.click();

    expect(cancelledTraining?.id).toBe(1);
  });

  it('should emit close when close button is clicked', () => {
    let closed = false;
    component.close.subscribe(() => (closed = true));

    const closeBtn = fixture.nativeElement.querySelector('.btn-close') as HTMLButtonElement;
    closeBtn.click();

    expect(closed).toBe(true);
  });
});
