import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrainingScheduleModalComponent, ScheduleTrainingPayload } from './training-schedule-modal.component';
import { SessionService } from '../../../../core/services/session.service';
import type { Session } from '../../../../core/api/models';

describe('TrainingScheduleModalComponent', () => {
  let component: TrainingScheduleModalComponent;
  let fixture: ComponentFixture<TrainingScheduleModalComponent>;

  const mockSessions: Session[] = [
    {
      id: 1,
      name: 'Push Day A',
      sessionStatus: 'Ready to Start',
      sessionExercises: []
    },
    {
      id: 2,
      name: 'Pull Day B',
      sessionStatus: 'Ready to Start',
      sessionExercises: []
    }
  ];

  const mockSessionService = {
    sessionsResource: {
      value: () => mockSessions,
      isLoading: () => false,
      error: () => null,
      reload: () => {}
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrainingScheduleModalComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TrainingScheduleModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the schedule modal', () => {
    expect(component).toBeTruthy();
  });

  it('should list available sessions in the selector', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const options = compiled.querySelectorAll('select.session-select option');
    expect(options.length).toBe(3); // default empty option + 2 sessions
    expect(options[1].textContent).toContain('Push Day A');
    expect(options[2].textContent).toContain('Pull Day B');
  });

  it('should emit scheduleTraining when form is submitted with valid data', () => {
    let emittedReq: ScheduleTrainingPayload | undefined;
    component.scheduleTraining.subscribe((req: ScheduleTrainingPayload) => (emittedReq = req));

    component.selectedSessionId.set(1);
    component.trainingDate.set('2026-09-12');
    component.customName.set('Saturday Power Push');
    fixture.detectChanges();

    const submitBtn = fixture.nativeElement.querySelector('.btn-submit') as HTMLButtonElement;
    submitBtn.click();

    expect(emittedReq).toEqual({
      sessionId: 1,
      date: '2026-09-12',
      customName: 'Saturday Power Push'
    });
  });

  it('should not emit scheduleTraining if session is not selected', () => {
    let emitted = false;
    component.scheduleTraining.subscribe(() => (emitted = true));

    component.selectedSessionId.set(null);
    fixture.detectChanges();

    const submitBtn = fixture.nativeElement.querySelector('.btn-submit') as HTMLButtonElement;
    submitBtn.click();

    expect(emitted).toBe(false);
  });

  it('should emit close when close button is clicked', () => {
    let closed = false;
    component.close.subscribe(() => (closed = true));

    const closeBtn = fixture.nativeElement.querySelector('.btn-close') as HTMLButtonElement;
    closeBtn.click();

    expect(closed).toBe(true);
  });
});
