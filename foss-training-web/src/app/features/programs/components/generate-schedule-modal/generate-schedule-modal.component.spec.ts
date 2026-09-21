import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GenerateScheduleModalComponent } from './generate-schedule-modal.component';
import { ProgramService } from '../../../../core/services/program.service';
import { of } from 'rxjs';
import type { TrainingProgram, Training } from '../../../../core/api/models';

describe('GenerateScheduleModalComponent', () => {
  let component: GenerateScheduleModalComponent;
  let fixture: ComponentFixture<GenerateScheduleModalComponent>;
  let mockProgramService: Partial<ProgramService>;

  const mockProgram: TrainingProgram = {
    id: 1,
    name: '12-Week Push Pull Legs',
    durationWeeks: 12
  };

  const mockTrainings: Training[] = [
    { id: 101, name: 'Push 1', status: 'Planned' }
  ];

  beforeEach(async () => {
    mockProgramService = {
      generateSchedule: () => of(mockTrainings)
    };

    await TestBed.configureTestingModule({
      imports: [GenerateScheduleModalComponent],
      providers: [
        { provide: ProgramService, useValue: mockProgramService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GenerateScheduleModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('program', mockProgram);
    fixture.detectChanges();
  });

  it('should create the schedule modal', () => {
    expect(component).toBeTruthy();
  });

  it('should emit scheduleGenerated on successful generation', () => {
    let generated: Training[] | undefined;
    component.scheduleGenerated.subscribe(t => (generated = t));

    component.startDate.set('2026-10-01');
    component.generate();

    expect(generated?.length).toBe(1);
    expect(generated![0].id).toBe(101);
  });
});
