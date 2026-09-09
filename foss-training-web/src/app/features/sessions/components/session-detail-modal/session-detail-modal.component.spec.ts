import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionDetailModalComponent } from './session-detail-modal.component';
import type { Session } from '../../../../core/api/models';

describe('SessionDetailModalComponent', () => {
  let component: SessionDetailModalComponent;
  let fixture: ComponentFixture<SessionDetailModalComponent>;

  const mockSession: Session = {
    id: 1,
    name: 'Push Day Hypertrophy',
    description: 'High volume chest, shoulder, and tricep session.',
    sessionStatus: 'Planned',
    notes: 'Warm up shoulders thoroughly before working sets.',
    sessionExercises: [
      {
        id: 10,
        exerciseType: 'ResistanceSessionExercise',
        orderIndex: 1,
        exercise: { id: 101, name: 'Incline Dumbbell Press', primaryCategory: 'RESISTANCE' },
        sets: [
          { setNumber: 1, setType: 'WARMUP', repetitions: 12, weight: { value: 24, unit: 'KG' }, rpe: { value: 6 } },
          { setNumber: 2, setType: 'WORKING', repetitions: 10, weight: { value: 32, unit: 'KG' }, rpe: { value: 8 } }
        ]
      }
    ]
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionDetailModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(SessionDetailModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('session', mockSession);
    fixture.detectChanges();
  });

  it('should create the session detail modal', () => {
    expect(component).toBeTruthy();
  });

  it('should render session title, notes, and exercises list', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.detail-title')?.textContent).toContain('Push Day Hypertrophy');
    expect(compiled.querySelector('.session-notes')?.textContent).toContain('Warm up shoulders thoroughly');
    expect(compiled.textContent).toContain('Incline Dumbbell Press');
  });

  it('should render set rows with repetitions and weight', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('12 reps');
    expect(compiled.textContent).toContain('24 KG');
    expect(compiled.textContent).toContain('10 reps');
    expect(compiled.textContent).toContain('32 KG');
  });

  it('should emit closed event when close button is clicked', () => {
    let closed = false;
    component.closed.subscribe(() => (closed = true));

    const closeBtn = fixture.nativeElement.querySelector('.btn-close') as HTMLButtonElement;
    closeBtn.click();

    expect(closed).toBe(true);
  });

  it('should emit startTraining event when start button is clicked', () => {
    let started: Session | undefined;
    component.startTraining.subscribe(s => (started = s));

    const startBtn = fixture.nativeElement.querySelector('.btn-start-action') as HTMLButtonElement;
    startBtn.click();

    expect(started?.id).toBe(1);
  });
});
