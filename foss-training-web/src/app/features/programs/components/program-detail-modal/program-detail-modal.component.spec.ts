import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProgramDetailModalComponent } from './program-detail-modal.component';
import type { TrainingProgram } from '../../../../core/api/models';

describe('ProgramDetailModalComponent', () => {
  let component: ProgramDetailModalComponent;
  let fixture: ComponentFixture<ProgramDetailModalComponent>;

  const mockProgram: TrainingProgram = {
    id: 1,
    name: '12-Week Push Pull Legs',
    description: 'Hypertrophy program details',
    durationWeeks: 12,
    periodizationType: 'LINEAR',
    level: 'INTERMEDIATE',
    workouts: [
      {
        dayOfWeek: 1,
        focus: 'Chest and Triceps',
        session: { id: 10, name: 'Push Heavy' }
      }
    ]
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgramDetailModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramDetailModalComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('program', mockProgram);
    fixture.detectChanges();
  });

  it('should create the detail modal', () => {
    expect(component).toBeTruthy();
  });

  it('should display title, description and microcycle day', () => {
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('12-Week Push Pull Legs');
    expect(el.textContent).toContain('Hypertrophy program details');
    expect(el.textContent).toContain('Monday');
    expect(el.textContent).toContain('Push Heavy');
  });

  it('should emit closed event on close button click', () => {
    let closed = false;
    component.closed.subscribe(() => (closed = true));

    const btn = fixture.nativeElement.querySelector('.btn-close') as HTMLButtonElement;
    btn.click();

    expect(closed).toBe(true);
  });
});
