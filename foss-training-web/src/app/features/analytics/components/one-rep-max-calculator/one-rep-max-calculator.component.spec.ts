import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OneRepMaxCalculatorComponent } from './one-rep-max-calculator.component';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import { of } from 'rxjs';
import type { OneRepMaxResponse } from '../../../../core/api/models';

describe('OneRepMaxCalculatorComponent', () => {
  let component: OneRepMaxCalculatorComponent;
  let fixture: ComponentFixture<OneRepMaxCalculatorComponent>;
  let mockAnalyticsService: Partial<AnalyticsService>;

  const mock1RmResponse: OneRepMaxResponse = {
    formula: 'EPLEY',
    weight: 100,
    unit: 'KG',
    repetitions: 5,
    estimated1Rm: 116.7,
    percentages: {
      '100': 116.7,
      '90': 105.0,
      '80': 93.3
    }
  };

  beforeEach(async () => {
    mockAnalyticsService = {
      calculate1Rm: () => of(mock1RmResponse)
    };

    await TestBed.configureTestingModule({
      imports: [OneRepMaxCalculatorComponent],
      providers: [
        { provide: AnalyticsService, useValue: mockAnalyticsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(OneRepMaxCalculatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the calculator component', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate 1RM on form submit', () => {
    component.weight.set(100);
    component.reps.set(5);
    component.calculate();

    expect(component.result()).toEqual(mock1RmResponse);
    expect(component.error()).toBeNull();
  });

  it('should set error message for invalid inputs', () => {
    component.weight.set(0);
    component.calculate();

    expect(component.error()).toContain('Please enter valid positive numbers');
    expect(component.result()).toBeNull();
  });

  it('should return sorted percentage list', () => {
    component.result.set(mock1RmResponse);
    const list = component.getPercentageList();

    expect(list.length).toBe(3);
    expect(list[0].percent).toBe(100);
    expect(list[1].percent).toBe(90);
    expect(list[2].percent).toBe(80);
  });
});
