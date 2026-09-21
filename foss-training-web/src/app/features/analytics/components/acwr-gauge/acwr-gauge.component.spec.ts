import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AcwrGaugeComponent } from './acwr-gauge.component';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import { of, throwError } from 'rxjs';
import type { AcwrResponse } from '../../../../core/api/models';

describe('AcwrGaugeComponent', () => {
  let component: AcwrGaugeComponent;
  let fixture: ComponentFixture<AcwrGaugeComponent>;
  let analyticsService: { getAcwr: ReturnType<typeof vi.fn> };

  const mockAcwrResponse: AcwrResponse = {
    targetDate: '2026-09-21',
    acuteWorkload: 1450,
    acuteDailyAverage: 207.1,
    chronicWorkload: 1300,
    chronicWeeklyAverage: 975,
    chronicDailyAverage: 139.3,
    acwr: 1.12,
    riskZone: 'OPTIMAL',
    riskZoneDisplayName: 'Optimal Zone',
    statusDescription: 'Fatigue is well balanced with fitness.',
    recommendation: 'Maintain current training progression.',
    deloadRecommended: false,
    dailyWorkloads: [
      {
        date: '2026-09-21',
        workloadAu: 220,
        totalVolumeKg: 1500,
        completedSessions: 1
      }
    ]
  };

  beforeEach(async () => {
    analyticsService = {
      getAcwr: vi.fn().mockReturnValue(of(mockAcwrResponse))
    };

    await TestBed.configureTestingModule({
      imports: [AcwrGaugeComponent],
      providers: [
        { provide: AnalyticsService, useValue: analyticsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AcwrGaugeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component and load initial ACWR', () => {
    expect(component).toBeTruthy();
    expect(analyticsService.getAcwr).toHaveBeenCalled();
    expect(component.acwrData()?.acwr).toBe(1.12);
    expect(component.acwrData()?.riskZone).toBe('OPTIMAL');
  });

  it('should map risk zone CSS classes properly', () => {
    expect(component.getRiskZoneClass('OPTIMAL')).toBe('zone-optimal');
    expect(component.getRiskZoneClass('UNDERTRAINING')).toBe('zone-undertraining');
    expect(component.getRiskZoneClass('OVERREACHING')).toBe('zone-overreaching');
    expect(component.getRiskZoneClass('HIGH_RISK')).toBe('zone-high-risk');
    expect(component.getRiskZoneClass(undefined)).toBe('zone-neutral');
  });

  it('should calculate gauge percentage clamped properly', () => {
    expect(component.getGaugePercentage(undefined)).toBe(0);
    expect(component.getGaugePercentage(1.1)).toBeCloseTo(50, 0);
    expect(component.getGaugePercentage(3.0)).toBe(100);
  });

  it('should handle service error gracefully', () => {
    analyticsService.getAcwr.mockReturnValue(throwError(() => new Error('API error')));
    component.loadAcwr();

    expect(component.isLoading()).toBe(false);
    expect(component.error()).toBe('Failed to calculate ACWR metrics. Please try again.');
  });
});
