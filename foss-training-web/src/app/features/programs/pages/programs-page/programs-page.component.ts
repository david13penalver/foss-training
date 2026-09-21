import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { ProgramService } from '../../../../core/services/program.service';
import type { TrainingProgram, TrainingProgramRequest } from '../../../../core/api/models';
import { ProgramCardComponent } from '../../components/program-card/program-card.component';
import { ProgramDetailModalComponent } from '../../components/program-detail-modal/program-detail-modal.component';
import { ProgramBuilderModalComponent } from '../../components/program-builder-modal/program-builder-modal.component';
import { GenerateScheduleModalComponent } from '../../components/generate-schedule-modal/generate-schedule-modal.component';
import { ProgramAdherenceModalComponent } from '../../components/program-adherence-modal/program-adherence-modal.component';
import { ConfirmDialogComponent } from '../../../../shared/ui/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-programs-page',
  standalone: true,
  imports: [
    ProgramCardComponent,
    ProgramDetailModalComponent,
    ProgramBuilderModalComponent,
    GenerateScheduleModalComponent,
    ProgramAdherenceModalComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './programs-page.component.html',
  styleUrl: './programs-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgramsPageComponent {
  readonly programService = inject(ProgramService);
  private readonly router = inject(Router);

  readonly searchQuery = signal('');
  readonly selectedLevel = signal<string>('ALL');

  // Modals state
  readonly selectedProgramForDetail = signal<TrainingProgram | null>(null);
  readonly selectedProgramForAdherence = signal<TrainingProgram | null>(null);
  readonly programForBuilder = signal<TrainingProgram | null>(null);
  readonly isBuilderOpen = signal(false);
  readonly programForSchedule = signal<TrainingProgram | null>(null);
  readonly programToDelete = signal<TrainingProgram | null>(null);

  readonly notificationMessage = signal<string | null>(null);

  readonly filteredPrograms = computed(() => {
    const raw = this.programService.programsResource.value() || [];
    const query = this.searchQuery().toLowerCase().trim();
    const lvl = this.selectedLevel();

    return raw.filter(p => {
      const matchesQuery = !query || p.name?.toLowerCase().includes(query) || p.description?.toLowerCase().includes(query);
      const matchesLevel = lvl === 'ALL' || p.level === lvl;
      return matchesQuery && matchesLevel;
    });
  });

  openCreateModal() {
    this.programForBuilder.set(null);
    this.isBuilderOpen.set(true);
  }

  openEditModal(program: TrainingProgram) {
    this.programForBuilder.set(program);
    this.isBuilderOpen.set(true);
  }

  saveProgram(request: TrainingProgramRequest) {
    if (request.id) {
      this.programService.updateProgram(request.id, request).subscribe({
        next: () => {
          this.isBuilderOpen.set(false);
          this.programService.programsResource.reload();
          this.showNotification('Program updated successfully.');
        }
      });
    } else {
      this.programService.createProgram(request).subscribe({
        next: () => {
          this.isBuilderOpen.set(false);
          this.programService.programsResource.reload();
          this.showNotification('Program created successfully.');
        }
      });
    }
  }

  confirmDelete(program: TrainingProgram) {
    this.programToDelete.set(program);
  }

  handleCloneProgram(program: TrainingProgram) {
    if (!program.id) return;
    this.programService.cloneProgram(program.id).subscribe({
      next: (cloned) => {
        this.programService.programsResource.reload();
        this.showNotification(`📋 Program "${program.name}" duplicated as "${cloned.name}"!`);
      },
      error: () => {
        this.showNotification('Failed to duplicate training program.');
      }
    });
  }

  openAdherenceModal(program: TrainingProgram) {
    this.selectedProgramForAdherence.set(program);
  }

  executeDelete() {
    const p = this.programToDelete();
    if (!p?.id) return;

    this.programService.deleteProgram(p.id).subscribe({
      next: () => {
        this.programToDelete.set(null);
        this.programService.programsResource.reload();
        this.showNotification('Program deleted.');
      }
    });
  }

  onScheduleGenerated() {
    this.programForSchedule.set(null);
    this.showNotification('Workouts successfully scheduled to calendar! Redirecting to Trainings...');
    setTimeout(() => {
      this.router.navigate(['/trainings']);
    }, 1200);
  }

  private showNotification(msg: string) {
    this.notificationMessage.set(msg);
    setTimeout(() => this.notificationMessage.set(null), 4000);
  }
}
