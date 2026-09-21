import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { RelativeStrengthCalculatorComponent } from './relative-strength-calculator.component';
import { AthleteService } from '../../../../core/services/athlete.service';
import type { RelativeStrengthResponse } from '../../../../core/api/models';

describe('RelativeStrengthCalculatorComponent', () => {
  let component: RelativeStrengthCalculatorComponent;
  let fixture: ComponentFixture<RelativeStrengthCalculatorComponent>;
  let mockAthleteService: any;

  const mockResponse: RelativeStrengthResponse = {
    totalWeightKg: 500,
    bodyweightKg: 80,
    gender: 'MALE',
    ratio: 6.25,
    dots: 356.18,
    wilks: 342.91,
    classification: 'Proficient'
  };

  beforeEach(async () => {
    mockAthleteService = {
      latestBodyweightResource: {
        value: signal({ id: 1, entryDate: '2026-09-20', weightKg: 80.0 })
      },
      calculateRelativeStrength: vi.fn().mockReturnValue(of(mockResponse))
    };

    await TestBed.configureTestingModule({
      imports: [RelativeStrengthCalculatorComponent],
      providers: [
        { provide: AthleteService, useValue: mockAthleteService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RelativeStrengthCalculatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
    expect(component.latestWeight()).toBe(80.0);
  });

  it('should populate latest bodyweight on click', () => {
    component.useLatestBodyweight();
    expect(component.bodyweightKg()).toBe(80.0);
  });

  it('should validate inputs before calculating', () => {
    component.totalWeightKg.set(null);
    component.calculate();
    expect(component.errorMessage()).toContain('lifted total');
    expect(mockAthleteService.calculateRelativeStrength).not.toHaveBeenCalled();
  });

  it('should calculate relative strength and display result', () => {
    component.totalWeightKg.set(500);
    component.bodyweightKg.set(80);
    component.gender.set('MALE');
    component.calculate();

    expect(mockAthleteService.calculateRelativeStrength).toHaveBeenCalledWith({
      totalWeightKg: 500,
      bodyweightKg: 80,
      gender: 'MALE'
    });

    expect(component.result()?.dots).toBe(356.18);
    expect(component.result()?.classification).toBe('Proficient');
  });
});
