import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionCardComponent } from './session-card.component';
import type { Session } from '../../../../core/api/models';

describe('SessionCardComponent', () => {
  let component: SessionCardComponent;
  let fixture: ComponentFixture<SessionCardComponent>;

  const mockSession: Session = {
    id: 1,
    name: 'Upper Body Power A',
    description: 'Heavy bench and overhead pressing with accessory pull work.',
    sessionStatus: 'Ready to Start',
    sessionExercises: [
      {
        id: 101,
        exerciseType: 'ResistanceSessionExercise',
        orderIndex: 1,
        exercise: { id: 1, name: 'Barbell Bench Press', primaryCategory: 'RESISTANCE' },
        sets: [
          { setNumber: 1, setType: 'WORKING', repetitions: 5, weight: { value: 100, unit: 'KG' } },
          { setNumber: 2, setType: 'WORKING', repetitions: 5, weight: { value: 100, unit: 'KG' } }
        ]
      },
      {
        id: 102,
        exerciseType: 'ResistanceSessionExercise',
        orderIndex: 2,
        exercise: { id: 2, name: 'Barbell Row', primaryCategory: 'RESISTANCE' },
        sets: [
          { setNumber: 1, setType: 'WORKING', repetitions: 8, weight: { value: 80, unit: 'KG' } }
        ]
      }
    ],
    totalVolume: 1640
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(SessionCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('session', mockSession);
    fixture.detectChanges();
  });

  it('should create the session card', () => {
    expect(component).toBeTruthy();
  });

  it('should render session title, description, and status badge', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.card-title')?.textContent).toContain('Upper Body Power A');
    expect(compiled.querySelector('.status-badge')?.textContent).toContain('Ready to Start');
    expect(compiled.querySelector('.card-desc')?.textContent).toContain('Heavy bench and overhead');
  });

  it('should render exercise count and list of exercise chips', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('2 Exercises');
    expect(compiled.textContent).toContain('Barbell Bench Press');
    expect(compiled.textContent).toContain('Barbell Row');
  });

  it('should emit startTraining when start workout button is clicked', () => {
    let triggeredSession: Session | undefined;
    component.startTraining.subscribe(s => (triggeredSession = s));

    const startBtn = fixture.nativeElement.querySelector('.btn-start-workout') as HTMLButtonElement;
    startBtn.click();

    expect(triggeredSession?.id).toBe(1);
  });

  it('should emit viewDetails when card is clicked', () => {
    let viewedSession: Session | undefined;
    component.viewDetails.subscribe(s => (viewedSession = s));

    const viewBtn = fixture.nativeElement.querySelector('.btn-view') as HTMLButtonElement;
    viewBtn.click();

    expect(viewedSession?.id).toBe(1);
  });

  it('should emit edit when edit button is clicked', () => {
    let editedSession: Session | undefined;
    component.edit.subscribe(s => (editedSession = s));

    const editBtn = fixture.nativeElement.querySelector('.btn-edit') as HTMLButtonElement;
    editBtn.click();

    expect(editedSession?.id).toBe(1);
  });

  it('should emit delete when delete button is clicked', () => {
    let deletedSession: Session | undefined;
    component.delete.subscribe(s => (deletedSession = s));

    const deleteBtn = fixture.nativeElement.querySelector('.btn-delete') as HTMLButtonElement;
    deleteBtn.click();

    expect(deletedSession?.id).toBe(1);
  });
});
