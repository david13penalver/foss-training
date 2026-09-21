import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeartRateZonesCalculatorComponent } from './heart-rate-zones-calculator.component';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import { of, throwError } from 'rxjs';
import type { HeartRateZonesResponse } from '../../../../core/api/models';

describe('HeartRateZonesCalculatorComponent', () => {
  let component: HeartRateZonesCalculatorComponent;
  let fixture: ComponentFixture<HeartRateZonesCalculatorComponent>;
  let analyticsService: { calculateHeartRateZones: ReturnType<typeof vi.fn> };

  const mockZonesResponse: HeartRateZonesResponse = {
    maxHr: 187,
    restingHr: 60,
    age: 30,
    method: 'KARVONEN',
    methodDisplayName: 'Karvonen Heart Rate Reserve',
    methodDescription: 'Calculates target training zones using Heart Rate Reserve (HRR = HRmax - HRrest)',
    heartRateReserve: 127,
    zones: [
      {
        zone: 'ZONE_1',
        displayName: 'Zone 1: Active Recovery',
        minPercentage: 50,
        maxPercentage: 60,
        minBpm: 124,
        maxBpm: 136,
        description: 'Active recovery and warm-up.',
        trainingBenefit: 'Enhances blood flow and speeds recovery.'
      },
      {
        zone: 'ZONE_2',
        displayName: 'Zone 2: Aerobic Base',
        minPercentage: 60,
        maxPercentage: 70,
        minBpm: 136,
        maxBpm: 149,
        description: 'Endurance base and mitochondrial biogenesis.',
        trainingBenefit: 'Builds cellular capillary density and fat oxidation.'
      },
      {
        zone: 'ZONE_3',
        displayName: 'Zone 3: Tempo',
        minPercentage: 70,
        maxPercentage: 80,
        minBpm: 149,
        maxBpm: 162,
        description: 'Aerobic fitness and carbohydrate utilization.',
        trainingBenefit: 'Improves efficiency at moderate-high paces.'
      },
      {
        zone: 'ZONE_4',
        displayName: 'Zone 4: Lactate Threshold',
        minPercentage: 80,
        maxPercentage: 90,
        minBpm: 162,
        maxBpm: 174,
        description: 'Lactate clearance and anaerobic threshold.',
        trainingBenefit: 'Increases high-intensity sustained endurance.'
      },
      {
        zone: 'ZONE_5',
        displayName: 'Zone 5: VO2 Max / Anaerobic',
        minPercentage: 90,
        maxPercentage: 100,
        minBpm: 174,
        maxBpm: 187,
        description: 'Maximal oxygen uptake and neuromuscular speed.',
        trainingBenefit: 'Maximizes VO2 peak and power output.'
      }
    ]
  };

  beforeEach(async () => {
    analyticsService = {
      calculateHeartRateZones: vi.fn().mockReturnValue(of(mockZonesResponse))
    };

    await TestBed.configureTestingModule({
      imports: [HeartRateZonesCalculatorComponent],
      providers: [
        { provide: AnalyticsService, useValue: analyticsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HeartRateZonesCalculatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load initial heart rate zones', () => {
    expect(component).toBeTruthy();
    expect(analyticsService.calculateHeartRateZones).toHaveBeenCalled();
    expect(component.zonesData()?.maxHr).toBe(187);
    expect(component.zonesData()?.zones?.length).toBe(5);
    expect(component.selectedZone()?.zone).toBe('ZONE_2');
  });

  it('should select zone on user click', () => {
    const zone4 = mockZonesResponse.zones![3];
    component.selectZone(zone4);
    expect(component.selectedZone()?.zone).toBe('ZONE_4');
  });

  it('should map zone color classes accurately', () => {
    expect(component.getZoneColorClass('ZONE_1')).toBe('zone-1');
    expect(component.getZoneColorClass('ZONE_2')).toBe('zone-2');
    expect(component.getZoneColorClass('ZONE_3')).toBe('zone-3');
    expect(component.getZoneColorClass('ZONE_4')).toBe('zone-4');
    expect(component.getZoneColorClass('ZONE_5')).toBe('zone-5');
    expect(component.getZoneColorClass(undefined)).toBe('zone-default');
  });

  it('should handle service errors gracefully', () => {
    analyticsService.calculateHeartRateZones.mockReturnValue(throwError(() => new Error('API failure')));
    component.calculateZones();

    expect(component.isLoading()).toBe(false);
    expect(component.error()).toBe('Failed to calculate heart rate zones. Please check your inputs.');
  });
});
