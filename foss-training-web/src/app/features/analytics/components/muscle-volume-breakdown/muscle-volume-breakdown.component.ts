import { Component, ChangeDetectionStrategy, inject, signal, computed, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import type { MuscleGroupVolume, WeeklyMuscleVolumeResponse } from '../../../../core/api/models';

@Component({
  selector: 'app-muscle-volume-breakdown',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: './muscle-volume-breakdown.component.html',
  styleUrl: './muscle-volume-breakdown.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MuscleVolumeBreakdownComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly startDate = signal<string>('');
  readonly endDate = signal<string>('');
  readonly isLoading = signal<boolean>(false);
  readonly error = signal<string | null>(null);
  readonly volumeData = signal<WeeklyMuscleVolumeResponse | null>(null);
  readonly categoryFilter = signal<string>('ALL');

  readonly filteredMuscleVolumes = computed(() => {
    const data = this.volumeData();
    if (!data?.muscleVolumes) return [];
    const filter = this.categoryFilter();
    if (filter === 'ALL') return data.muscleVolumes;
    return data.muscleVolumes.filter(m => m.category === filter);
  });

  ngOnInit(): void {
    const end = new Date();
    const start = new Date();
    start.setDate(end.getDate() - 7);

    this.endDate.set(end.toISOString().split('T')[0]);
    this.startDate.set(start.toISOString().split('T')[0]);

    this.loadVolume();
  }

  loadVolume(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.analyticsService.getWeeklyMuscleVolume(this.startDate(), this.endDate()).subscribe({
      next: (data) => {
        this.volumeData.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Failed to load muscle volume analytics. Please try again.');
        this.isLoading.set(false);
      }
    });
  }

  setCategoryFilter(category: string): void {
    this.categoryFilter.set(category);
  }

  getStatusClass(status?: string): string {
    switch (status) {
      case 'OPTIMAL':
        return 'status-optimal';
      case 'MAINTENANCE':
        return 'status-maintenance';
      case 'UNDERTRAINED':
        return 'status-undertrained';
      case 'OVERTRAINED':
        return 'status-overtrained';
      default:
        return 'status-neutral';
    }
  }

  getProgressWidth(effectiveSets?: number): number {
    if (!effectiveSets || effectiveSets <= 0) return 0;
    // Scale 0 to 25 sets
    return Math.min(100, (effectiveSets / 25) * 100);
  }

  getCategoryVolumes(): { category: string; sets: number }[] {
    const cats = this.volumeData()?.categoryVolumes;
    if (!cats) return [];
    return Object.entries(cats).map(([category, sets]) => ({
      category: category.replace('_', ' '),
      sets
    }));
  }
}
