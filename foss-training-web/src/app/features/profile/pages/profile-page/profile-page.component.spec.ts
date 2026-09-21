import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { ProfilePageComponent } from './profile-page.component';
import { AthleteService } from '../../../../core/services/athlete.service';
import { DataPortabilityService } from '../../../../core/services/data-portability.service';

describe('ProfilePageComponent', () => {
  let component: ProfilePageComponent;
  let fixture: ComponentFixture<ProfilePageComponent>;

  beforeEach(async () => {
    const mockAthleteService = {
      bodyweightHistoryResource: {
        value: signal([]),
        reload: vi.fn()
      },
      latestBodyweightResource: {
        value: signal(undefined)
      },
      logBodyweight: vi.fn().mockReturnValue(of({ id: 1, entryDate: '2026-09-21', weightKg: 80.0 })),
      deleteBodyweight: vi.fn().mockReturnValue(of(undefined)),
      calculateRelativeStrength: vi.fn().mockReturnValue(of({}))
    };

    const mockDataService = {
      downloadJsonBackup: vi.fn().mockReturnValue(of({})),
      downloadWorkoutsCsv: vi.fn().mockReturnValue(of('')),
      importBackup: vi.fn().mockReturnValue(of({})),
      importWorkoutsCsv: vi.fn().mockReturnValue(of({}))
    };

    await TestBed.configureTestingModule({
      imports: [ProfilePageComponent],
      providers: [
        { provide: AthleteService, useValue: mockAthleteService },
        { provide: DataPortabilityService, useValue: mockDataService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfilePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component and default to bodyweight tab', () => {
    expect(component).toBeTruthy();
    expect(component.activeTab()).toBe('bodyweight');
  });

  it('should switch tabs', () => {
    component.setTab('relative-strength');
    fixture.detectChanges();
    expect(component.activeTab()).toBe('relative-strength');

    component.setTab('data-sovereignty');
    fixture.detectChanges();
    expect(component.activeTab()).toBe('data-sovereignty');
  });
});
