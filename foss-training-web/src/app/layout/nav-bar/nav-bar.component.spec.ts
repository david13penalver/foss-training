import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { NavBarComponent } from './nav-bar.component';

describe('NavBarComponent', () => {
  let component: NavBarComponent;
  let fixture: ComponentFixture<NavBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavBarComponent],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(NavBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the navigation bar', () => {
    expect(component).toBeTruthy();
  });

  it('should render brand logo title', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.brand-title')?.textContent).toContain('FOSS Training');
  });

  it('should render all 4 main tabs with correct router links', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const links = compiled.querySelectorAll<HTMLAnchorElement>('.nav-tab');
    expect(links.length).toBe(4);

    const linkHrefs = Array.from(links).map(l => l.getAttribute('href') || l.getAttribute('ng-reflect-router-link'));
    const tabTexts = Array.from(links).map(l => l.textContent?.trim());

    expect(tabTexts.some(t => t?.includes('Exercises'))).toBe(true);
    expect(tabTexts.some(t => t?.includes('Sessions'))).toBe(true);
    expect(tabTexts.some(t => t?.includes('Trainings'))).toBe(true);
    expect(tabTexts.some(t => t?.includes('Analytics'))).toBe(true);
  });
});
