import { Component, ChangeDetectionStrategy, signal } from '@angular/core';
import { OneRepMaxCalculatorComponent } from '../../components/one-rep-max-calculator/one-rep-max-calculator.component';
import { PersonalRecordsDashboardComponent } from '../../components/personal-records-dashboard/personal-records-dashboard.component';

type AnalyticsTab = '1rm' | 'records';

@Component({
  selector: 'app-analytics-page',
  standalone: true,
  imports: [OneRepMaxCalculatorComponent, PersonalRecordsDashboardComponent],
  templateUrl: './analytics-page.component.html',
  styleUrl: './analytics-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnalyticsPageComponent {
  readonly activeTab = signal<AnalyticsTab>('1rm');

  setTab(tab: AnalyticsTab) {
    this.activeTab.set(tab);
  }
}
