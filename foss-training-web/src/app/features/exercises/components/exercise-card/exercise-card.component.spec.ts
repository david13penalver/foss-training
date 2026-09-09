import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExerciseCardComponent } from './exercise-card.component';
import type { Exercise } from '../../../../core/api/models';

describe('ExerciseCardComponent', () => {
  let component: ExerciseCardComponent;
  let fixture: ComponentFixture<ExerciseCardComponent>;

  const mockExercise: Exercise = {
    id: 1,
    name: 'Barbell Bench Press',
    description: 'Compound horizontal push movement for chest and triceps.',
    primaryCategory: 'RESISTANCE',
    difficultyLevel: 'INTERMEDIATE',
    equipmentRequired: ['BARBELL', 'BENCH'],
    resistanceMetrics: {
      primaryMuscles: ['CHEST'],
      secondaryMuscles: ['TRICEPS', 'SHOULDERS'],
      movementPattern: 'PUSH'
    },
    active: true
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ExerciseCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('exercise', mockExercise);
    fixture.detectChanges();
  });

  it('should create the exercise card', () => {
    expect(component).toBeTruthy();
  });

  it('should display exercise name, category, and difficulty', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.card-title')?.textContent).toContain('Barbell Bench Press');
    expect(compiled.querySelector('.category-badge')?.textContent).toContain('RESISTANCE');
    expect(compiled.querySelector('.difficulty-pill')?.textContent).toContain('INTERMEDIATE');
  });

  it('should render equipment and primary muscle tags', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('BARBELL');
    expect(compiled.textContent).toContain('CHEST');
  });

  it('should emit viewDetails event when card view button is clicked', () => {
    let selected: Exercise | undefined;
    component.viewDetails.subscribe(e => (selected = e));

    const viewBtn = fixture.nativeElement.querySelector('.btn-view') as HTMLButtonElement;
    viewBtn.click();

    expect(selected?.id).toBe(1);
  });

  it('should emit edit event when edit button is clicked', () => {
    let edited: Exercise | undefined;
    component.edit.subscribe(e => (edited = e));

    const editBtn = fixture.nativeElement.querySelector('.btn-edit') as HTMLButtonElement;
    editBtn.click();

    expect(edited?.id).toBe(1);
  });

  it('should emit delete event when delete button is clicked', () => {
    let deleted: Exercise | undefined;
    component.delete.subscribe(e => (deleted = e));

    const deleteBtn = fixture.nativeElement.querySelector('.btn-delete') as HTMLButtonElement;
    deleteBtn.click();

    expect(deleted?.id).toBe(1);
  });
});
