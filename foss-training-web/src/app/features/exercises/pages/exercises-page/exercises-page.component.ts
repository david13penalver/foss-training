import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExerciseService } from '../../../../core/services/exercise.service';
import { ReferenceDataService } from '../../../../core/services/reference-data.service';
import { ExerciseCardComponent } from '../../components/exercise-card/exercise-card.component';
import { ExerciseDetailModalComponent } from '../../components/exercise-detail-modal/exercise-detail-modal.component';
import { ExerciseFormModalComponent } from '../../components/exercise-form-modal/exercise-form-modal.component';
import { ConfirmDialogComponent } from '../../../../shared/ui/confirm-dialog/confirm-dialog.component';
import type { Exercise, ExerciseRequest } from '../../../../core/api/models';

@Component({
  selector: 'app-exercises-page',
  standalone: true,
  imports: [
    FormsModule,
    ExerciseCardComponent,
    ExerciseDetailModalComponent,
    ExerciseFormModalComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './exercises-page.component.html',
  styleUrl: './exercises-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExercisesPageComponent {
  private readonly exerciseService = inject(ExerciseService);
  private readonly refService = inject(ReferenceDataService);
  readonly Math = Math;

  // Raw Data from resources
  readonly exercises = this.exerciseService.exercisesResource.value;
  readonly isLoading = this.exerciseService.exercisesResource.isLoading;
  readonly error = this.exerciseService.exercisesResource.error;

  readonly availableEquipment = this.refService.equipmentResource.value;
  readonly availableMuscles = this.refService.muscleGroupsResource.value;

  // Filter Signals (ViewModel State)
  readonly searchQuery = signal('');
  readonly selectedCategory = signal('ALL');
  readonly selectedDifficulty = signal('ALL');

  // Modal Signals (UI State)
  readonly isFormOpen = signal(false);
  readonly editingExercise = signal<Exercise | null>(null);

  readonly isDetailOpen = signal(false);
  readonly activeExercise = signal<Exercise | null>(null);

  readonly isDeleteConfirmOpen = signal(false);
  readonly exerciseToDelete = signal<Exercise | null>(null);

  readonly categories = [
    'ALL',
    'RESISTANCE',
    'ENDURANCE',
    'MOBILITY',
    'CALISTHENICS',
    'PLYOMETRICS',
    'CORE'
  ];

  readonly difficulties = ['ALL', 'BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT'];

  // Pagination Signals
  readonly currentPage = signal(0);
  readonly pageSize = signal(12);

  // Derived Computed Signal
  readonly filteredExercises = computed(() => {
    const list = this.exercises() ?? [];
    const query = this.searchQuery().trim().toLowerCase();
    const cat = this.selectedCategory();
    const diff = this.selectedDifficulty();

    return list.filter(item => {
      // Search filter
      const matchesSearch =
        !query ||
        (item.name && item.name.toLowerCase().includes(query)) ||
        (item.description && item.description.toLowerCase().includes(query)) ||
        (item.resistanceMetrics?.primaryMuscles?.some(m => m.toLowerCase().includes(query))) ||
        (item.equipmentRequired?.some(e => e.toLowerCase().includes(query)));

      // Category filter
      const matchesCategory = cat === 'ALL' || item.primaryCategory === cat;

      // Difficulty filter
      const matchesDifficulty = diff === 'ALL' || item.difficultyLevel === diff;

      return matchesSearch && matchesCategory && matchesDifficulty;
    });
  });

  readonly totalPages = computed(() => {
    const count = this.filteredExercises().length;
    return Math.max(1, Math.ceil(count / this.pageSize()));
  });

  readonly paginatedExercises = computed(() => {
    const list = this.filteredExercises();
    const page = this.currentPage();
    const size = this.pageSize();
    const start = page * size;
    return list.slice(start, start + size);
  });

  // Filter change handlers that reset page
  onSearchChange(query: string) {
    this.searchQuery.set(query);
    this.currentPage.set(0);
  }

  onCategoryChange(cat: string) {
    this.selectedCategory.set(cat);
    this.currentPage.set(0);
  }

  onDifficultyChange(diff: string) {
    this.selectedDifficulty.set(diff);
    this.currentPage.set(0);
  }

  onPageSizeChange(size: number) {
    this.pageSize.set(size);
    this.currentPage.set(0);
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
    }
  }

  nextPage() {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(p => p + 1);
    }
  }

  prevPage() {
    if (this.currentPage() > 0) {
      this.currentPage.update(p => p - 1);
    }
  }

  // Action Handlers
  openCreateModal() {
    this.editingExercise.set(null);
    this.isFormOpen.set(true);
  }

  openEditModal(exercise: Exercise) {
    this.editingExercise.set(exercise);
    this.isDetailOpen.set(false);
    this.isFormOpen.set(true);
  }

  openDetailModal(exercise: Exercise) {
    this.activeExercise.set(exercise);
    this.isDetailOpen.set(true);
  }

  openDeleteConfirm(exercise: Exercise) {
    this.exerciseToDelete.set(exercise);
    this.isDeleteConfirmOpen.set(true);
  }

  closeModals() {
    this.isFormOpen.set(false);
    this.editingExercise.set(null);
    this.isDetailOpen.set(false);
    this.activeExercise.set(null);
    this.isDeleteConfirmOpen.set(false);
    this.exerciseToDelete.set(null);
  }

  confirmDelete() {
    const target = this.exerciseToDelete();
    if (target?.id) {
      this.exerciseService.deleteExercise(target.id).subscribe({
        next: () => {
          this.exerciseService.exercisesResource.reload();
          this.closeModals();
        }
      });
    }
  }

  handleSave(request: ExerciseRequest) {
    const isEdit = !!request.id;
    const action$ = isEdit
      ? this.exerciseService.updateExercise(request.id!, request)
      : this.exerciseService.createExercise(request);

    action$.subscribe({
      next: () => {
        this.exerciseService.exercisesResource.reload();
        this.closeModals();
      }
    });
  }
}
