import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrainingCardComponent } from './training-card.component';
import type { Training } from '../../../../core/api/models';

describe('TrainingCardComponent', () => {
  let component: TrainingCardComponent;
  let fixture: ComponentFixture<TrainingCardComponent>;

  const mockTraining: Training = {
    id: 1,
    name: 'Evening Hypertrophy A',
    description: 'Chest & Triceps focus',
    trainingDate: '2026-09-10',
    status: 'Planned',
    totalVolume: 3200,
    session: {
      name: 'Chest & Tri Routine',
      sessionExercises: [
        {
          id: 10,
          exerciseType: 'ResistanceSessionExercise',
          exercise: { name: 'Bench Press' }
        },
        {
          id: 11,
          exerciseType: 'ResistanceSessionExercise',
          exercise: { name: 'Tricep Pushdown' }
        }
      ]
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrainingCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TrainingCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('training', mockTraining);
    fixture.detectChanges();
  });

  it('should create the training card', () => {
    expect(component).toBeTruthy();
  });

  it('should render training title, date, volume and status badge', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.card-title')?.textContent).toContain('Evening Hypertrophy A');
    expect(compiled.querySelector('.status-badge')?.textContent).toContain('Planned');
    expect(compiled.querySelector('.training-date')?.textContent).toContain('2026-09-10');
    expect(compiled.querySelector('.volume-stat')?.textContent).toContain('3,200 kg');
  });

  it('should list exercise preview chips', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Bench Press');
    expect(compiled.textContent).toContain('Tricep Pushdown');
  });

  it('should show "Start Workout" when status is Planned', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const startBtn = compiled.querySelector('.btn-start') as HTMLButtonElement;
    expect(startBtn).toBeTruthy();
    expect(startBtn.textContent).toContain('Start Workout');
  });

  it('should emit start event when start workout button is clicked', () => {
    let startedTraining: Training | undefined;
    component.start.subscribe((t: Training) => (startedTraining = t));

    const startBtn = fixture.nativeElement.querySelector('.btn-start') as HTMLButtonElement;
    startBtn.click();

    expect(startedTraining?.id).toBe(1);
  });

  it('should show "Resume Workout" and "Complete" when status is In Progress', () => {
    fixture.componentRef.setInput('training', {
      ...mockTraining,
      status: 'In Progress'
    });
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const resumeBtn = compiled.querySelector('.btn-resume') as HTMLButtonElement;
    const completeBtn = compiled.querySelector('.btn-complete') as HTMLButtonElement;

    expect(resumeBtn).toBeTruthy();
    expect(resumeBtn.textContent).toContain('Resume');
    expect(completeBtn).toBeTruthy();
    expect(completeBtn.textContent).toContain('Finish');
  });

  it('should emit viewDetails when card or view button is clicked', () => {
    let viewedTraining: Training | undefined;
    component.viewDetails.subscribe((t: Training) => (viewedTraining = t));

    const viewBtn = fixture.nativeElement.querySelector('.btn-view') as HTMLButtonElement;
    viewBtn.click();

    expect(viewedTraining?.id).toBe(1);
  });

  it('should emit delete when delete button is clicked', () => {
    let deletedTraining: Training | undefined;
    component.delete.subscribe((t: Training) => (deletedTraining = t));

    const deleteBtn = fixture.nativeElement.querySelector('.btn-delete') as HTMLButtonElement;
    deleteBtn.click();

    expect(deletedTraining?.id).toBe(1);
  });
});
