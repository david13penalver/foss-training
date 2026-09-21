import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExerciseProgressionChartComponent } from './exercise-progression-chart.component';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import { ExerciseService } from '../../../../core/services/exercise.service';
import { of, throwError } from 'rxjs';
import { signal } from '@angular/core';
import type { ExerciseProgressionResponse } from '../../../../core/api/models';

describe('ExerciseProgressionChartComponent', () => {
  let component: ExerciseProgressionChartComponent;
  let fixture: ComponentFixture<ExerciseProgressionChartComponent>;
  let analyticsService: { getExerciseProgression: ReturnType<typeof vi.fn> };
  let exerciseService: { exercisesResource: { value: ReturnType<typeof signal> } };

  const mockProgressionResponse: ExerciseProgressionResponse = {
    exerciseId: 1,
    exerciseName: 'Bench Press',
    formula: 'EPLEY',
    startDate: '2026-08-01',
    endDate: '2026-09-21',
    totalSessions: 3,
    initial1RmKg: 100,
    latest1RmKg: 115,
    absolute1RmGainKg: 15,
    relative1RmGainPercentage: 15,
    allTimeBest1RmKg: 115,
    allTimeBestTopWeightKg: 105,
    allTimeMaxVolumeKg: 3500,
    trend: 'IMPROVING',
    trendDisplayName: 'Improving',
    trendDescription: 'Strength output is progressively increasing.',
    dataPoints: [
      {
        trainingId: 10,
        date: '2026-08-05',
        totalSets: 4,
        workingSets: 3,
        totalReps: 24,
        totalVolumeKg: 2400,
        topWeightKg: 85,
        topWeightReps: 6,
        topWeightRpe: 8,
        estimated1RmKg: 100,
        averageIntensityKg: 80
      },
      {
        trainingId: 15,
        date: '2026-08-20',
        totalSets: 4,
        workingSets: 3,
        totalReps: 24,
        totalVolumeKg: 2700,
        topWeightKg: 92.5,
        topWeightReps: 5,
        topWeightRpe: 8.5,
        estimated1RmKg: 107.5,
        averageIntensityKg: 85
      },
      {
        trainingId: 20,
        date: '2026-09-15',
        totalSets: 4,
        workingSets: 3,
        totalReps: 20,
        totalVolumeKg: 3100,
        topWeightKg: 100,
        topWeightReps: 5,
        topWeightRpe: 9,
        estimated1RmKg: 116.67,
        averageIntensityKg: 90
      }
    ]
  };

  beforeEach(async () => {
    analyticsService = {
      getExerciseProgression: vi.fn().mockReturnValue(of(mockProgressionResponse))
    };

    exerciseService = {
      exercisesResource: {
        value: signal([
          { id: 1, name: 'Bench Press', primaryCategory: 'RESISTANCE' },
          { id: 2, name: 'Running', primaryCategory: 'ENDURANCE' }
        ])
      }
    };

    await TestBed.configureTestingModule({
      imports: [ExerciseProgressionChartComponent],
      providers: [
        { provide: AnalyticsService, useValue: analyticsService },
        { provide: ExerciseService, useValue: exerciseService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ExerciseProgressionChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and auto-load progression for default resistance exercise', () => {
    expect(component).toBeTruthy();
    expect(component.selectedExerciseId()).toBe(1);
    expect(analyticsService.getExerciseProgression).toHaveBeenCalled();
    expect(component.progressionData()?.exerciseName).toBe('Bench Press');
  });

  it('should handle exercise selection change', () => {
    component.onExerciseChange('2');
    expect(component.selectedExerciseId()).toBe(2);
    expect(analyticsService.getExerciseProgression).toHaveBeenCalledWith({
      exerciseId: 2,
      startDate: undefined,
      endDate: undefined,
      formula: 'EPLEY'
    });
  });

  it('should map trend CSS classes properly', () => {
    expect(component.getTrendClass('IMPROVING')).toBe('trend-improving');
    expect(component.getTrendClass('STAGNANT')).toBe('trend-stagnant');
    expect(component.getTrendClass('DECLINING')).toBe('trend-declining');
    expect(component.getTrendClass('INSUFFICIENT_DATA')).toBe('trend-insufficient');
  });

  it('should generate valid SVG chart points and paths', () => {
    const points = component.getChartPoints();
    expect(points.length).toBe(3);

    const path = component.getSvgPath();
    expect(path).toContain('M');
    expect(path).toContain('L');

    const areaPath = component.getSvgAreaPath();
    expect(areaPath).toContain('Z');
  });

  it('should handle service error gracefully', () => {
    analyticsService.getExerciseProgression.mockReturnValue(throwError(() => new Error('API failure')));
    component.loadProgression();

    expect(component.isLoading()).toBe(false);
    expect(component.error()).toBe('Failed to load strength progression data for this exercise.');
  });
});
