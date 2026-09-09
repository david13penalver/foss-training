import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal, WritableSignal } from '@angular/core';
import { of } from 'rxjs';
import { ExercisesPageComponent } from './exercises-page.component';
import { ExerciseService } from '../../../../core/services/exercise.service';
import { ReferenceDataService } from '../../../../core/services/reference-data.service';
import type { Exercise, ExerciseRequest } from '../../../../core/api/models';

describe('ExercisesPageComponent', () => {
  let component: ExercisesPageComponent;
  let fixture: ComponentFixture<ExercisesPageComponent>;
  let mockExerciseService: any;
  let mockRefService: any;
  let exercisesSignal: WritableSignal<Exercise[]>;

  const mockExercises: Exercise[] = [
    {
      id: 1,
      name: 'Bench Press',
      description: 'Chest compound movement',
      primaryCategory: 'RESISTANCE',
      difficultyLevel: 'INTERMEDIATE',
      resistanceMetrics: { primaryMuscles: ['CHEST'], movementPattern: 'PUSH' },
      equipmentRequired: ['BARBELL', 'BENCH'],
      active: true
    },
    {
      id: 2,
      name: 'Running Intervals',
      description: 'Endurance track work',
      primaryCategory: 'ENDURANCE',
      difficultyLevel: 'ADVANCED',
      enduranceMetrics: { enduranceType: 'HIIT' },
      active: true
    },
    {
      id: 3,
      name: 'Hamstring Dynamic Stretch',
      description: 'Leg mobility drill',
      primaryCategory: 'MOBILITY',
      difficultyLevel: 'BEGINNER',
      mobilityMetrics: { mobilityType: 'DYNAMIC_MOBILITY' },
      active: true
    }
  ];

  beforeEach(async () => {
    exercisesSignal = signal<Exercise[]>([...mockExercises]);

    mockExerciseService = {
      exercisesResource: {
        value: exercisesSignal,
        isLoading: signal(false),
        error: signal(undefined),
        reload: vi.fn()
      },
      createExercise: vi.fn((req: ExerciseRequest) => of({ ...req, id: 99, active: true })),
      updateExercise: vi.fn((id: number, req: ExerciseRequest) => of({ ...req, id, active: true })),
      deleteExercise: vi.fn((id: number) => of(void 0))
    };

    mockRefService = {
      getEquipment: vi.fn(() => of(['BARBELL', 'DUMBBELL', 'BENCH'])),
      getMuscleGroups: vi.fn(() => of(['CHEST', 'LATS', 'QUADRICEPS'])),
      getMovementPatterns: vi.fn(() => of(['PUSH', 'PULL', 'SQUAT'])),
      equipmentResource: { value: signal(['BARBELL', 'DUMBBELL', 'BENCH']) },
      muscleGroupsResource: { value: signal(['CHEST', 'LATS', 'QUADRICEPS']) }
    };

    await TestBed.configureTestingModule({
      imports: [ExercisesPageComponent],
      providers: [
        { provide: ExerciseService, useValue: mockExerciseService },
        { provide: ReferenceDataService, useValue: mockRefService },
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ExercisesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the Exercises page', () => {
    expect(component).toBeTruthy();
  });

  it('should render all exercises initially', () => {
    expect(component.filteredExercises().length).toBe(3);
    const compiled = fixture.nativeElement as HTMLElement;
    const cards = compiled.querySelectorAll('app-exercise-card');
    expect(cards.length).toBe(3);
  });

  it('should filter exercises by search query', () => {
    component.searchQuery.set('bench');
    fixture.detectChanges();

    expect(component.filteredExercises().length).toBe(1);
    expect(component.filteredExercises()[0].name).toBe('Bench Press');
  });

  it('should filter exercises by primary category', () => {
    component.selectedCategory.set('ENDURANCE');
    fixture.detectChanges();

    expect(component.filteredExercises().length).toBe(1);
    expect(component.filteredExercises()[0].name).toBe('Running Intervals');
  });

  it('should filter exercises by difficulty level', () => {
    component.selectedDifficulty.set('BEGINNER');
    fixture.detectChanges();

    expect(component.filteredExercises().length).toBe(1);
    expect(component.filteredExercises()[0].name).toBe('Hamstring Dynamic Stretch');
  });

  it('should open create modal when "+ New Exercise" is triggered', () => {
    component.openCreateModal();
    fixture.detectChanges();

    expect(component.isFormOpen()).toBe(true);
    expect(component.editingExercise()).toBeNull();
  });

  it('should open edit modal with selected exercise', () => {
    component.openEditModal(mockExercises[0]);
    fixture.detectChanges();

    expect(component.isFormOpen()).toBe(true);
    expect(component.editingExercise()?.id).toBe(1);
  });

  it('should open detail modal with selected exercise', () => {
    component.openDetailModal(mockExercises[1]);
    fixture.detectChanges();

    expect(component.isDetailOpen()).toBe(true);
    expect(component.activeExercise()?.id).toBe(2);
  });

  it('should open delete confirm dialog and delete on confirmation', () => {
    component.openDeleteConfirm(mockExercises[0]);
    fixture.detectChanges();

    expect(component.isDeleteConfirmOpen()).toBe(true);
    expect(component.exerciseToDelete()?.id).toBe(1);

    component.confirmDelete();
    expect(mockExerciseService.deleteExercise).toHaveBeenCalledWith(1);
  });
});
