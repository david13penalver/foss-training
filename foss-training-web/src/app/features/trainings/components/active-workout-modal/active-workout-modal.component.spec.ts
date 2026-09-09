import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActiveWorkoutModalComponent } from './active-workout-modal.component';
import type { Training } from '../../../../core/api/models';

describe('ActiveWorkoutModalComponent', () => {
  let component: ActiveWorkoutModalComponent;
  let fixture: ComponentFixture<ActiveWorkoutModalComponent>;

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
    await TestBed.configureTestingModule({
      imports: [ActiveWorkoutModalComponent]
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

  it('should emit finishWorkout when Finish Workout button is clicked', () => {
    let finishedTraining: Training | undefined;
    component.finishWorkout.subscribe((t: Training) => (finishedTraining = t));

    const finishBtn = fixture.nativeElement.querySelector('.btn-finish') as HTMLButtonElement;
    finishBtn.click();

    expect(finishedTraining?.id).toBe(1);
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
