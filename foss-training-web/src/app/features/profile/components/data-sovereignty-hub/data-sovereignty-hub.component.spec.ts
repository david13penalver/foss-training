import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { DataSovereigntyHubComponent } from './data-sovereignty-hub.component';
import { DataPortabilityService } from '../../../../core/services/data-portability.service';
import type { FullBackupData, ImportSummary } from '../../../../core/api/models';

describe('DataSovereigntyHubComponent', () => {
  let component: DataSovereigntyHubComponent;
  let fixture: ComponentFixture<DataSovereigntyHubComponent>;
  let mockDataService: any;

  const mockSummary: ImportSummary = {
    exercisesImported: 5,
    sessionsImported: 2,
    programsImported: 1,
    trainingsImported: 4,
    bodyweightImported: 10,
    totalImported: 22
  };

  beforeEach(async () => {
    mockDataService = {
      downloadJsonBackup: vi.fn().mockReturnValue(of({ exportVersion: '1.0' } as FullBackupData)),
      downloadWorkoutsCsv: vi.fn().mockReturnValue(of('training_id,training_date\n1,2026-09-21')),
      importBackup: vi.fn().mockReturnValue(of(mockSummary)),
      importWorkoutsCsv: vi.fn().mockReturnValue(of(mockSummary))
    };

    await TestBed.configureTestingModule({
      imports: [DataSovereigntyHubComponent],
      providers: [
        { provide: DataPortabilityService, useValue: mockDataService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DataSovereigntyHubComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call download backup and set success message', () => {
    component.downloadBackup();
    expect(mockDataService.downloadJsonBackup).toHaveBeenCalled();
    expect(component.successMessage()).toContain('backup downloaded');
  });

  it('should call download workouts CSV and set success message', () => {
    component.downloadWorkoutsCsv();
    expect(mockDataService.downloadWorkoutsCsv).toHaveBeenCalled();
    expect(component.successMessage()).toContain('CSV downloaded');
  });
});
