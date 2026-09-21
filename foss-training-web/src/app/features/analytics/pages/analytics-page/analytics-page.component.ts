import { Component, ChangeDetectionStrategy, signal } from '@angular/core';
import { OneRepMaxCalculatorComponent } from '../../components/one-rep-max-calculator/one-rep-max-calculator.component';
import { PersonalRecordsDashboardComponent } from '../../components/personal-records-dashboard/personal-records-dashboard.component';
import { AcwrGaugeComponent } from '../../components/acwr-gauge/acwr-gauge.component';
import { MuscleVolumeBreakdownComponent } from '../../components/muscle-volume-breakdown/muscle-volume-breakdown.component';
import { ExerciseProgressionChartComponent } from '../../components/exercise-progression-chart/exercise-progression-chart.component';
import { HeartRateZonesCalculatorComponent } from '../../components/heart-rate-zones-calculator/heart-rate-zones-calculator.component';

export type AnalyticsTab = '1rm' | 'records' | 'acwr' | 'muscle-volume' | 'progression' | 'cardio-zones';

@Component({
  selector: 'app-analytics-page',
  standalone: true,
  imports: [
    OneRepMaxCalculatorComponent,
    PersonalRecordsDashboardComponent,
    AcwrGaugeComponent,
    MuscleVolumeBreakdownComponent,
    ExerciseProgressionChartComponent,
    HeartRateZonesCalculatorComponent
  ],
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

