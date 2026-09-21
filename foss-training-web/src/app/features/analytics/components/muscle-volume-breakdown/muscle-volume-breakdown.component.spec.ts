import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MuscleVolumeBreakdownComponent } from './muscle-volume-breakdown.component';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import { of, throwError } from 'rxjs';
import type { WeeklyMuscleVolumeResponse } from '../../../../core/api/models';

describe('MuscleVolumeBreakdownComponent', () => {
  let component: MuscleVolumeBreakdownComponent;
  let fixture: ComponentFixture<MuscleVolumeBreakdownComponent>;
  let analyticsService: { getWeeklyMuscleVolume: ReturnType<typeof vi.fn> };

  const mockVolumeResponse: WeeklyMuscleVolumeResponse = {
    startDate: '2026-09-14',
    endDate: '2026-09-21',
    totalWorkingSets: 42,
    totalVolumeKg: 28500,
    pushPullRatio: 1.15,
    upperLowerRatio: 1.35,
    categoryVolumes: {
      UPPER_BODY: 28,
      LOWER_BODY: 14
    },
    muscleVolumes: [
      {
        muscleGroup: 'CHEST',
        muscleGroupName: 'Chest',
        category: 'UPPER_BODY',
        directSets: 12,
        indirectSets: 4,
        effectiveSets: 14,
        totalVolumeKg: 10200,
        status: 'OPTIMAL',
        statusDisplayName: 'Optimal Hypertrophy',
        statusDescription: 'Within optimal volume landmarks.'
      },
      {
        muscleGroup: 'QUADRICEPS',
        muscleGroupName: 'Quadriceps',
        category: 'LOWER_BODY',
        directSets: 10,
        indirectSets: 0,
        effectiveSets: 10,
        totalVolumeKg: 9500,
        status: 'OPTIMAL',
        statusDisplayName: 'Optimal Hypertrophy',
        statusDescription: 'Sufficient quad stimulus.'
      }
    ],
    recommendations: ['Maintain current upper/lower balance.']
  };

  beforeEach(async () => {
    analyticsService = {
      getWeeklyMuscleVolume: vi.fn().mockReturnValue(of(mockVolumeResponse))
    };

    await TestBed.configureTestingModule({
      imports: [MuscleVolumeBreakdownComponent],
      providers: [
        { provide: AnalyticsService, useValue: analyticsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MuscleVolumeBreakdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load initial weekly muscle volume', () => {
    expect(component).toBeTruthy();
    expect(analyticsService.getWeeklyMuscleVolume).toHaveBeenCalled();
    expect(component.volumeData()?.totalWorkingSets).toBe(42);
    expect(component.filteredMuscleVolumes().length).toBe(2);
  });

  it('should filter muscle volumes by category', () => {
    component.setCategoryFilter('UPPER_BODY');
    expect(component.categoryFilter()).toBe('UPPER_BODY');
    expect(component.filteredMuscleVolumes().length).toBe(1);
    expect(component.filteredMuscleVolumes()[0].muscleGroup).toBe('CHEST');

    component.setCategoryFilter('ALL');
    expect(component.filteredMuscleVolumes().length).toBe(2);
  });

  it('should compute category volumes list correctly', () => {
    const cats = component.getCategoryVolumes();
    expect(cats.length).toBe(2);
    expect(cats[0].category).toBe('UPPER BODY');
    expect(cats[0].sets).toBe(28);
  });

  it('should calculate status class and progress width', () => {
    expect(component.getStatusClass('OPTIMAL')).toBe('status-optimal');
    expect(component.getStatusClass('MAINTENANCE')).toBe('status-maintenance');
    expect(component.getStatusClass('UNDERTRAINED')).toBe('status-undertrained');
    expect(component.getStatusClass('OVERTRAINED')).toBe('status-overtrained');
    expect(component.getStatusClass(undefined)).toBe('status-neutral');

    expect(component.getProgressWidth(0)).toBe(0);
    expect(component.getProgressWidth(12.5)).toBe(50);
    expect(component.getProgressWidth(30)).toBe(100);
  });

  it('should handle service error gracefully', () => {
    analyticsService.getWeeklyMuscleVolume.mockReturnValue(throwError(() => new Error('API failure')));
    component.loadVolume();

    expect(component.isLoading()).toBe(false);
    expect(component.error()).toBe('Failed to load muscle volume analytics. Please try again.');
  });
});
