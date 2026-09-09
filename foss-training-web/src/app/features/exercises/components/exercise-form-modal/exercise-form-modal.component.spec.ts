import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExerciseFormModalComponent } from './exercise-form-modal.component';
import type { Exercise, ExerciseRequest } from '../../../../core/api/models';

describe('ExerciseFormModalComponent', () => {
  let component: ExerciseFormModalComponent;
  let fixture: ComponentFixture<ExerciseFormModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseFormModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ExerciseFormModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the form modal', () => {
    expect(component).toBeTruthy();
  });

  it('should render Create Exercise header by default', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.form-title')?.textContent).toContain('Create New Exercise');
  });

  it('should populate fields in Edit mode', async () => {
    const existingExercise: Exercise = {
      id: 42,
      name: 'Pull-up',
      description: 'Bodyweight vertical pulling exercise.',
      primaryCategory: 'CALISTHENICS',
      difficultyLevel: 'INTERMEDIATE',
      equipmentRequired: ['PULL_UP_BAR'],
      active: true
    };

    fixture.componentRef.setInput('exercise', existingExercise);
    fixture.detectChanges();
    await fixture.whenStable();

    const nameInput = fixture.nativeElement.querySelector('#exercise-name') as HTMLInputElement;
    expect(nameInput.value).toBe('Pull-up');
    expect(fixture.nativeElement.querySelector('.form-title')?.textContent).toContain('Edit Exercise');
  });

  it('should emit save event with valid ExerciseRequest when form is submitted', async () => {
    let savedRequest: ExerciseRequest | undefined;
    component.save.subscribe(req => (savedRequest = req));

    // Fill name input
    const nameInput = fixture.nativeElement.querySelector('#exercise-name') as HTMLInputElement;
    nameInput.value = 'Overhead Press';
    nameInput.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const submitBtn = fixture.nativeElement.querySelector('.btn-submit') as HTMLButtonElement;
    submitBtn.click();
    fixture.detectChanges();

    expect(savedRequest).toBeDefined();
    expect(savedRequest?.name).toBe('Overhead Press');
    expect(savedRequest?.primaryCategory).toBe('RESISTANCE');
  });

  it('should emit closed event when cancel button is clicked', () => {
    let closed = false;
    component.closed.subscribe(() => (closed = true));

    const cancelBtn = fixture.nativeElement.querySelector('.btn-cancel') as HTMLButtonElement;
    cancelBtn.click();

    expect(closed).toBe(true);
  });
});
