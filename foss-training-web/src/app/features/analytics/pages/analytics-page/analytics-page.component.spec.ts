import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AnalyticsPageComponent } from './analytics-page.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('AnalyticsPageComponent', () => {
  let component: AnalyticsPageComponent;
  let fixture: ComponentFixture<AnalyticsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnalyticsPageComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AnalyticsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the analytics page', () => {
    expect(component).toBeTruthy();
  });

  it('should default to 1rm tab and switch between all analytics tabs', () => {
    expect(component.activeTab()).toBe('1rm');

    component.setTab('records');
    expect(component.activeTab()).toBe('records');

    component.setTab('acwr');
    expect(component.activeTab()).toBe('acwr');

    component.setTab('muscle-volume');
    expect(component.activeTab()).toBe('muscle-volume');

    component.setTab('progression');
    expect(component.activeTab()).toBe('progression');

    component.setTab('cardio-zones');
    expect(component.activeTab()).toBe('cardio-zones');
  });
});
