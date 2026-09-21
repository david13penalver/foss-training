import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProgramCardComponent } from './program-card.component';
import type { TrainingProgram } from '../../../../core/api/models';

describe('ProgramCardComponent', () => {
  let component: ProgramCardComponent;
  let fixture: ComponentFixture<ProgramCardComponent>;

  const mockProgram: TrainingProgram = {
    id: 1,
    name: '12-Week Push Pull Legs',
    description: 'Hypertrophy program with linear periodization',
    durationWeeks: 12,
    periodizationType: 'LINEAR',
    level: 'INTERMEDIATE',
    workouts: [
      { dayOfWeek: 1, focus: 'Push' },
      { dayOfWeek: 3, focus: 'Pull' },
      { dayOfWeek: 5, focus: 'Legs' }
    ]
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgramCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('program', mockProgram);
    fixture.detectChanges();
  });

  it('should create the program card', () => {
    expect(component).toBeTruthy();
  });

  it('should render title, duration, and day names', () => {
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('12-Week Push Pull Legs');
    expect(el.textContent).toContain('12 Weeks');
    expect(el.textContent).toContain('Mon');
    expect(el.textContent).toContain('Wed');
    expect(el.textContent).toContain('Fri');
  });

  it('should emit schedule event on schedule button click', () => {
    let emitted: TrainingProgram | undefined;
    component.schedule.subscribe(p => (emitted = p));

    const btn = fixture.nativeElement.querySelector('.btn-schedule') as HTMLButtonElement;
    btn.click();

    expect(emitted?.id).toBe(1);
  });
});
