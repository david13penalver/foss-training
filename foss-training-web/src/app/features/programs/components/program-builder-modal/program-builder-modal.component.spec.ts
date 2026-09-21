import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProgramBuilderModalComponent } from './program-builder-modal.component';
import { SessionService } from '../../../../core/services/session.service';
import { signal } from '@angular/core';
import type { TrainingProgramRequest } from '../../../../core/api/models';

describe('ProgramBuilderModalComponent', () => {
  let component: ProgramBuilderModalComponent;
  let fixture: ComponentFixture<ProgramBuilderModalComponent>;
  let mockSessionService: any;

  beforeEach(async () => {
    mockSessionService = {
      sessionsResource: {
        value: signal([
          { id: 10, name: 'Push Strength' },
          { id: 20, name: 'Pull Hypertrophy' }
        ])
      }
    };

    await TestBed.configureTestingModule({
      imports: [ProgramBuilderModalComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramBuilderModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the builder modal', () => {
    expect(component).toBeTruthy();
  });

  it('should add and remove workout days', () => {
    expect(component.workouts().length).toBe(0);

    component.addWorkoutDay();
    expect(component.workouts().length).toBe(1);

    component.removeWorkoutDay(0);
    expect(component.workouts().length).toBe(0);
  });

  it('should emit save on valid submission', () => {
    let savedRequest: TrainingProgramRequest | undefined;
    component.save.subscribe(r => (savedRequest = r));

    component.name.set('8-Week Upper Lower');
    component.durationWeeks.set(8);
    component.addWorkoutDay();
    component.updateWorkoutDay(0, 'sessionId', 10);
    component.updateWorkoutDay(0, 'focus', 'Upper Body');

    component.submit();

    expect(savedRequest?.name).toBe('8-Week Upper Lower');
    expect(savedRequest?.durationWeeks).toBe(8);
    expect(savedRequest?.workouts?.length).toBe(1);
    expect(savedRequest?.workouts![0].focus).toBe('Upper Body');
  });

  it('should display error if name is empty', () => {
    component.name.set('');
    component.submit();

    expect(component.errorMessage()).toContain('Program name is required');
  });
});
