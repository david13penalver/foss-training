import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionBuilderModalComponent } from './session-builder-modal.component';
import type { Session, SessionRequest, Exercise } from '../../../../core/api/models';

describe('SessionBuilderModalComponent', () => {
  let component: SessionBuilderModalComponent;
  let fixture: ComponentFixture<SessionBuilderModalComponent>;

  const mockAvailableExercises: Exercise[] = [
    { id: 1, name: 'Barbell Bench Press', primaryCategory: 'RESISTANCE' },
    { id: 2, name: 'Incline Dumbbell Press', primaryCategory: 'RESISTANCE' },
    { id: 3, name: 'Overhead Tricep Extension', primaryCategory: 'RESISTANCE' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionBuilderModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(SessionBuilderModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('availableExercises', mockAvailableExercises);
    fixture.detectChanges();
  });

  it('should create the session builder modal', () => {
    expect(component).toBeTruthy();
  });

  it('should render create header by default', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.builder-title')?.textContent).toContain('Create Routine Template');
  });

  it('should populate fields in Edit mode', async () => {
    const existingSession: Session = {
      id: 5,
      name: 'Push Routine Pro',
      description: 'Advanced push day',
      sessionStatus: 'Planned',
      sessionExercises: [
        {
          id: 50,
          exerciseType: 'ResistanceSessionExercise',
          orderIndex: 1,
          exercise: mockAvailableExercises[0],
          sets: [
            { setNumber: 1, setType: 'WORKING', repetitions: 6, weight: { value: 90, unit: 'KG' } }
          ]
        }
      ]
    };

    fixture.componentRef.setInput('session', existingSession);
    fixture.detectChanges();
    await fixture.whenStable();

    const nameInput = fixture.nativeElement.querySelector('#session-name') as HTMLInputElement;
    expect(nameInput.value).toBe('Push Routine Pro');
    expect(fixture.nativeElement.querySelector('.builder-title')?.textContent).toContain('Edit Routine Template');
  });

  it('should allow adding an exercise to the routine', () => {
    component.addExercise(mockAvailableExercises[1]);
    fixture.detectChanges();

    expect(component.selectedExercises().length).toBe(1);
    expect(component.selectedExercises()[0].exercise?.name).toBe('Incline Dumbbell Press');
  });

  it('should allow adding and removing sets', () => {
    component.addExercise(mockAvailableExercises[0]);
    fixture.detectChanges();

    // Default 1 set added with exercise
    expect(component.selectedExercises()[0].sets?.length).toBe(1);

    component.addSet(0);
    expect(component.selectedExercises()[0].sets?.length).toBe(2);

    component.removeSet(0, 1);
    expect(component.selectedExercises()[0].sets?.length).toBe(1);
  });

  it('should emit save with valid SessionRequest', async () => {
    let savedRequest: SessionRequest | undefined;
    component.save.subscribe(req => (savedRequest = req));

    // Fill name input
    const nameInput = fixture.nativeElement.querySelector('#session-name') as HTMLInputElement;
    nameInput.value = 'Chest & Arms Blast';
    nameInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    component.addExercise(mockAvailableExercises[0]);
    fixture.detectChanges();

    const submitBtn = fixture.nativeElement.querySelector('.btn-save-routine') as HTMLButtonElement;
    submitBtn.click();
    fixture.detectChanges();

    expect(savedRequest).toBeDefined();
    expect(savedRequest?.name).toBe('Chest & Arms Blast');
    expect(savedRequest?.sessionExercises?.length).toBe(1);
  });

  it('should emit closed event when cancel is clicked', () => {
    let closed = false;
    component.closed.subscribe(() => (closed = true));

    const cancelBtn = fixture.nativeElement.querySelector('.btn-cancel') as HTMLButtonElement;
    cancelBtn.click();

    expect(closed).toBe(true);
  });
});
