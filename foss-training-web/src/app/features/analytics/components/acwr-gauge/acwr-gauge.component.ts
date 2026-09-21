import { Component, ChangeDetectionStrategy, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DecimalPipe, DatePipe } from '@angular/common';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import type { AcwrResponse, AcwrRiskZone } from '../../../../core/api/models';

@Component({
  selector: 'app-acwr-gauge',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './acwr-gauge.component.html',
  styleUrl: './acwr-gauge.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AcwrGaugeComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly targetDate = signal<string>(new Date().toISOString().split('T')[0]);
  readonly isLoading = signal<boolean>(false);
  readonly error = signal<string | null>(null);
  readonly acwrData = signal<AcwrResponse | null>(null);

  ngOnInit(): void {
    this.loadAcwr();
  }

  loadAcwr(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.analyticsService.getAcwr(this.targetDate()).subscribe({
      next: (data) => {
        this.acwrData.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Failed to calculate ACWR metrics. Please try again.');
        this.isLoading.set(false);
      }
    });
  }

  getRiskZoneClass(riskZone?: string): string {
    switch (riskZone) {
      case 'OPTIMAL':
        return 'zone-optimal';
      case 'UNDERTRAINING':
        return 'zone-undertraining';
      case 'OVERREACHING':
        return 'zone-overreaching';
      case 'HIGH_RISK':
        return 'zone-high-risk';
      default:
        return 'zone-neutral';
    }
  }

  getGaugePercentage(acwr?: number): number {
    if (acwr === undefined || acwr === null) return 0;
    // Cap representation between 0 and 2.2 (100%)
    const maxScale = 2.2;
    const clamped = Math.max(0, Math.min(acwr, maxScale));
    return (clamped / maxScale) * 100;
  }
}
