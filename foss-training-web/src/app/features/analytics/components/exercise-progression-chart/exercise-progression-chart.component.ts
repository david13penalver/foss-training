import { Component, ChangeDetectionStrategy, inject, signal, computed, OnInit, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DecimalPipe, DatePipe } from '@angular/common';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import { ExerciseService } from '../../../../core/services/exercise.service';
import type {
  ExerciseProgressionResponse,
  OneRepMaxFormula,
  ProgressionDataPoint
} from '../../../../core/api/models';

export interface ChartPoint {
  x: number;
  y: number;
  data: ProgressionDataPoint;
}

@Component({
  selector: 'app-exercise-progression-chart',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './exercise-progression-chart.component.html',
  styleUrl: './exercise-progression-chart.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExerciseProgressionChartComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);
  readonly exerciseService = inject(ExerciseService);

  readonly selectedExerciseId = signal<number | null>(null);
  readonly startDate = signal<string>('');
  readonly endDate = signal<string>('');
  readonly formula = signal<OneRepMaxFormula>('EPLEY');

  readonly isLoading = signal<boolean>(false);
  readonly error = signal<string | null>(null);
  readonly progressionData = signal<ExerciseProgressionResponse | null>(null);

  readonly formulas: OneRepMaxFormula[] = [
    'EPLEY',
    'BRZYCKI',
    'LANDER',
    'LOMBARDI',
    'MAYHEW',
    'OCONNER',
    'WATHEN'
  ];

  readonly exercises = computed(() => {
    return this.exerciseService.exercisesResource.value() || [];
  });

  readonly hoveredPoint = signal<ChartPoint | null>(null);

  constructor() {
    // Auto-select first exercise when available if none selected
    effect(() => {
      const exs = this.exercises();
      if (exs.length > 0 && this.selectedExerciseId() === null) {
        // Prefer resistance category exercise
        const resistanceEx = exs.find(e => e.primaryCategory === 'RESISTANCE') || exs[0];
        if (resistanceEx.id) {
          this.selectedExerciseId.set(resistanceEx.id);
          this.loadProgression();
        }
      }
    });
  }

  ngOnInit(): void {
    // default to empty dates for full history
  }

  onExerciseChange(idStr: string): void {
    const id = Number(idStr);
    if (!isNaN(id)) {
      this.selectedExerciseId.set(id);
      this.loadProgression();
    }
  }

  loadProgression(): void {
    const exId = this.selectedExerciseId();
    if (!exId) return;

    this.isLoading.set(true);
    this.error.set(null);
    this.hoveredPoint.set(null);

    this.analyticsService.getExerciseProgression({
      exerciseId: exId,
      startDate: this.startDate() || undefined,
      endDate: this.endDate() || undefined,
      formula: this.formula()
    }).subscribe({
      next: (data) => {
        this.progressionData.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Failed to load strength progression data for this exercise.');
        this.isLoading.set(false);
      }
    });
  }

  getTrendClass(trend?: string): string {
    switch (trend) {
      case 'IMPROVING':
        return 'trend-improving';
      case 'STAGNANT':
        return 'trend-stagnant';
      case 'DECLINING':
        return 'trend-declining';
      case 'INSUFFICIENT_DATA':
      default:
        return 'trend-insufficient';
    }
  }

  getChartPoints(): ChartPoint[] {
    const points = this.progressionData()?.dataPoints;
    if (!points || points.length === 0) return [];

    const validPoints = points.filter(p => (p.estimated1RmKg || 0) > 0);
    if (validPoints.length === 0) return [];

    const values = validPoints.map(p => p.estimated1RmKg || 0);
    const minVal = Math.min(...values) * 0.95;
    const maxVal = Math.max(...values) * 1.05;
    const range = maxVal === minVal ? 1 : maxVal - minVal;

    const width = 640;
    const height = 180;
    const paddingX = 40;
    const paddingY = 25;

    return validPoints.map((point, index) => {
      const x = validPoints.length === 1
        ? paddingX + width / 2
        : paddingX + (index / (validPoints.length - 1)) * width;
      const y = (paddingY + height) - (((point.estimated1RmKg || 0) - minVal) / range) * height;
      return { x, y, data: point };
    });
  }

  getSvgPath(): string {
    const chartPoints = this.getChartPoints();
    if (chartPoints.length === 0) return '';
    if (chartPoints.length === 1) {
      return `M ${chartPoints[0].x - 10} ${chartPoints[0].y} L ${chartPoints[0].x + 10} ${chartPoints[0].y}`;
    }

    return chartPoints.reduce((path, pt, idx) => {
      return idx === 0 ? `M ${pt.x} ${pt.y}` : `${path} L ${pt.x} ${pt.y}`;
    }, '');
  }

  getSvgAreaPath(): string {
    const chartPoints = this.getChartPoints();
    if (chartPoints.length === 0) return '';
    const linePath = this.getSvgPath();
    const bottomY = 205;
    const firstX = chartPoints[0].x;
    const lastX = chartPoints[chartPoints.length - 1].x;
    return `${linePath} L ${lastX} ${bottomY} L ${firstX} ${bottomY} Z`;
  }
}
