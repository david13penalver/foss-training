import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PersonalRecordsDashboardComponent } from './personal-records-dashboard.component';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import { signal } from '@angular/core';
import type { PersonalRecordResponse } from '../../../../core/api/models';

describe('PersonalRecordsDashboardComponent', () => {
  let component: PersonalRecordsDashboardComponent;
  let fixture: ComponentFixture<PersonalRecordsDashboardComponent>;
  let mockAnalyticsService: any;

  const mockRecords: PersonalRecordResponse[] = [
    {
      exerciseId: 1,
      exerciseName: 'Bench Press',
      maxWeight: { value: 120, unit: 'KG', repetitions: 1 }
    },
    {
      exerciseId: 2,
      exerciseName: 'Squat',
      maxWeight: { value: 160, unit: 'KG', repetitions: 3 }
    }
  ];

  beforeEach(async () => {
    mockAnalyticsService = {
      personalRecordsResource: {
        value: signal(mockRecords),
        isLoading: signal(false),
        reload: () => {}
      }
    };

    await TestBed.configureTestingModule({
      imports: [PersonalRecordsDashboardComponent],
      providers: [
        { provide: AnalyticsService, useValue: mockAnalyticsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PersonalRecordsDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the dashboard component', () => {
    expect(component).toBeTruthy();
  });

  it('should render all records initially', () => {
    expect(component.records().length).toBe(2);
  });

  it('should filter records based on search query', () => {
    component.searchQuery.set('squat');
    expect(component.records().length).toBe(1);
    expect(component.records()[0].exerciseName).toBe('Squat');
  });
});
