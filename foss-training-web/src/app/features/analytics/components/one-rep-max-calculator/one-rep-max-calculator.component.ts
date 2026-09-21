import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import type { OneRepMaxFormula, OneRepMaxResponse, WeightUnit } from '../../../../core/api/models';

import { DecimalPipe } from '@angular/common';

interface PercentageItem {
  percent: number;
  weight: number;
  unit: string;
}

@Component({
  selector: 'app-one-rep-max-calculator',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: './one-rep-max-calculator.component.html',
  styleUrl: './one-rep-max-calculator.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OneRepMaxCalculatorComponent {
  private readonly analyticsService = inject(AnalyticsService);

  readonly weight = signal<number>(100);
  readonly reps = signal<number>(5);
  readonly unit = signal<WeightUnit>('KG');
  readonly formula = signal<OneRepMaxFormula>('EPLEY');

  readonly isCalculating = signal(false);
  readonly error = signal<string | null>(null);
  readonly result = signal<OneRepMaxResponse | null>(null);

  readonly formulas: OneRepMaxFormula[] = [
    'EPLEY',
    'BRZYCKI',
    'LANDER',
    'LOMBARDI',
    'MAYHEW',
    'OCONNER',
    'WATHEN'
  ];

  calculate() {
    const w = this.weight();
    const r = this.reps();

    if (!w || w <= 0 || !r || r <= 0) {
      this.error.set('Please enter valid positive numbers for weight and reps.');
      return;
    }

    this.isCalculating.set(true);
    this.error.set(null);

    this.analyticsService.calculate1Rm({
      weight: w,
      reps: r,
      unit: this.unit(),
      formula: this.formula()
    }).subscribe({
      next: res => {
        this.result.set(res);
        this.isCalculating.set(false);
      },
      error: err => {
        this.error.set('Failed to calculate 1RM. Please verify your inputs.');
        this.isCalculating.set(false);
      }
    });
  }

  getPercentageList(): PercentageItem[] {
    const res = this.result();
    if (!res || !res.percentages) return [];

    const unit = res.unit || 'KG';
    return Object.entries(res.percentages)
      .map(([pctStr, weightVal]) => ({
        percent: Number(pctStr),
        weight: Number(weightVal) || 0,
        unit
      }))
      .sort((a, b) => b.percent - a.percent);
  }
}
