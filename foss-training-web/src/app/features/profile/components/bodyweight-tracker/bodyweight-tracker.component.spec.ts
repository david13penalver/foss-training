import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { BodyweightTrackerComponent } from './bodyweight-tracker.component';
import { AthleteService } from '../../../../core/services/athlete.service';
import type { BodyweightEntry } from '../../../../core/api/models';

describe('BodyweightTrackerComponent', () => {
  let component: BodyweightTrackerComponent;
  let fixture: ComponentFixture<BodyweightTrackerComponent>;
  let mockAthleteService: any;

  const mockEntries: BodyweightEntry[] = [
    { id: 1, entryDate: '2026-09-15', weightKg: 82.0 },
    { id: 2, entryDate: '2026-09-20', weightKg: 81.2 }
  ];

  beforeEach(async () => {
    mockAthleteService = {
      bodyweightHistoryResource: {
        value: signal(mockEntries),
        reload: vi.fn()
      },
      logBodyweight: vi.fn().mockReturnValue(of({ id: 3, entryDate: '2026-09-21', weightKg: 80.5 })),
      deleteBodyweight: vi.fn().mockReturnValue(of(undefined))
    };

    await TestBed.configureTestingModule({
      imports: [BodyweightTrackerComponent],
      providers: [
        { provide: AthleteService, useValue: mockAthleteService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(BodyweightTrackerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component and compute latest weight', () => {
    expect(component).toBeTruthy();
    expect(component.latestEntry()?.weightKg).toBe(81.2);
    expect(component.weightDelta()).toBe(-0.8);
  });

  it('should validate inputs before logging', () => {
    component.weightKg.set(null);
    component.logWeight();
    expect(component.errorMessage()).toContain('valid positive weight');
    expect(mockAthleteService.logBodyweight).not.toHaveBeenCalled();
  });

  it('should log bodyweight when valid', () => {
    component.weightKg.set(80.5);
    component.logWeight();

    expect(mockAthleteService.logBodyweight).toHaveBeenCalled();
    expect(mockAthleteService.bodyweightHistoryResource.reload).toHaveBeenCalled();
  });

  it('should delete bodyweight entry', () => {
    component.deleteEntry(1);
    expect(mockAthleteService.deleteBodyweight).toHaveBeenCalledWith(1);
    expect(mockAthleteService.bodyweightHistoryResource.reload).toHaveBeenCalled();
  });
});
