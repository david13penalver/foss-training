import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfirmDialogComponent } from './confirm-dialog.component';

describe('ConfirmDialogComponent', () => {
  let component: ConfirmDialogComponent;
  let fixture: ComponentFixture<ConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmDialogComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmDialogComponent);
    component = fixture.componentInstance;
  });

  it('should create the confirm dialog', () => {
    expect(component).toBeTruthy();
  });

  it('should render title and message inputs', () => {
    fixture.componentRef.setInput('title', 'Delete Exercise');
    fixture.componentRef.setInput('message', 'Are you sure you want to delete this exercise?');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.dialog-title')?.textContent).toContain('Delete Exercise');
    expect(compiled.querySelector('.dialog-message')?.textContent).toContain('Are you sure');
  });

  it('should emit confirmed event when confirm button clicked', () => {
    let emitted = false;
    component.confirmed.subscribe(() => (emitted = true));

    fixture.detectChanges();
    const confirmBtn = fixture.nativeElement.querySelector('.btn-confirm') as HTMLButtonElement;
    confirmBtn.click();

    expect(emitted).toBe(true);
  });

  it('should emit cancelled event when cancel button or backdrop clicked', () => {
    let cancelled = false;
    component.cancelled.subscribe(() => (cancelled = true));

    fixture.detectChanges();
    const cancelBtn = fixture.nativeElement.querySelector('.btn-cancel') as HTMLButtonElement;
    cancelBtn.click();

    expect(cancelled).toBe(true);
  });
});
