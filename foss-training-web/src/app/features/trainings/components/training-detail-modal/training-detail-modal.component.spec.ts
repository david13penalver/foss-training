import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrainingDetailModalComponent } from './training-detail-modal.component';
import type { Training } from '../../../../core/api/models';

describe('TrainingDetailModalComponent', () => {
  let component: TrainingDetailModalComponent;
  let fixture: ComponentFixture<TrainingDetailModalComponent>;

  const mockTraining: Training = {
    id: 10,
    name: 'Leg Day Volume',
    description: 'Heavy squats & quad hypertrophy',
    trainingDate: '2026-09-08',
    status: 'Completed',
    totalVolume: 8500,
    session: {
      name: 'Leg Routine',
      sessionExercises: [
        {
          id: 50,
          exerciseType: 'ResistanceSessionExercise',
          orderIndex: 1,
          exercise: { name: 'Barbell Back Squat' },
          sets: [
            { setNumber: 1, setType: 'WORKING', repetitions: 5, weight: { value: 140, unit: 'KG' } },
            { setNumber: 2, setType: 'WORKING', repetitions: 5, weight: { value: 140, unit: 'KG' } }
          ]
        }
      ]
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrainingDetailModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TrainingDetailModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('training', mockTraining);
    fixture.detectChanges();
  });

  it('should create the training detail modal', () => {
    expect(component).toBeTruthy();
  });

  it('should display training metadata and volume', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.modal-title')?.textContent).toContain('Leg Day Volume');
    expect(compiled.querySelector('.modal-desc')?.textContent).toContain('Heavy squats');
    expect(compiled.textContent).toContain('8,500 kg');
    expect(compiled.textContent).toContain('Barbell Back Squat');
  });

  it('should emit close when close button is clicked', () => {
    let closed = false;
    component.close.subscribe(() => (closed = true));

    const closeBtn = fixture.nativeElement.querySelector('.btn-close') as HTMLButtonElement;
    closeBtn.click();

    expect(closed).toBe(true);
  });
});
