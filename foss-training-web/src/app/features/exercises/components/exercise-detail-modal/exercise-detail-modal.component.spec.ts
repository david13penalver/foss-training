import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExerciseDetailModalComponent } from './exercise-detail-modal.component';
import type { Exercise } from '../../../../core/api/models';

describe('ExerciseDetailModalComponent', () => {
  let component: ExerciseDetailModalComponent;
  let fixture: ComponentFixture<ExerciseDetailModalComponent>;

  const mockExercise: Exercise = {
    id: 1,
    name: 'Barbell Back Squat',
    description: 'Fundamental lower body compound exercise targeting quads and glutes.',
    primaryCategory: 'RESISTANCE',
    difficultyLevel: 'ADVANCED',
    equipmentRequired: ['BARBELL', 'POWER_RACK'],
    resistanceMetrics: {
      primaryMuscles: ['QUADRICEPS', 'GLUTES'],
      secondaryMuscles: ['HAMSTRINGS', 'CORE'],
      movementPattern: 'SQUAT',
      recommendedSets: 4,
      recommendedRepsMin: 6,
      recommendedRepsMax: 10,
      recommendedRestSeconds: 180
    },
    stepByStepInstructions: [
      'Set the bar height to mid-chest level.',
      'Step under the bar and place it across your upper back.',
      'Squat down until thighs are at least parallel to floor.'
    ],
    safetyTips: ['Keep your spine neutral throughout the movement.'],
    commonMistakes: ['Knees caving inwards on the ascent.'],
    active: true
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseDetailModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ExerciseDetailModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('exercise', mockExercise);
    fixture.detectChanges();
  });

  it('should create the detail modal', () => {
    expect(component).toBeTruthy();
  });

  it('should render exercise title, description, and instructions', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.detail-title')?.textContent).toContain('Barbell Back Squat');
    expect(compiled.querySelector('.detail-description')?.textContent).toContain('Fundamental lower body');

    const instructions = compiled.querySelectorAll('.instruction-item');
    expect(instructions.length).toBe(3);
    expect(instructions[0].textContent).toContain('Set the bar height');
  });

  it('should render safety tips and common mistakes', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Keep your spine neutral');
    expect(compiled.textContent).toContain('Knees caving inwards');
  });

  it('should emit closed event when close button is clicked', () => {
    let closed = false;
    component.closed.subscribe(() => (closed = true));

    const closeBtn = fixture.nativeElement.querySelector('.btn-close') as HTMLButtonElement;
    closeBtn.click();

    expect(closed).toBe(true);
  });

  it('should emit edit event when edit action clicked', () => {
    let edited: Exercise | undefined;
    component.edit.subscribe(e => (edited = e));

    const editBtn = fixture.nativeElement.querySelector('.btn-edit-action') as HTMLButtonElement;
    editBtn.click();

    expect(edited?.id).toBe(1);
  });
});
