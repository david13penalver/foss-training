import { Component, ChangeDetectionStrategy, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import type { CalculatedHeartRateZone, HeartRateZonesResponse } from '../../../../core/api/models';

@Component({
  selector: 'app-heart-rate-zones-calculator',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: './heart-rate-zones-calculator.component.html',
  styleUrl: './heart-rate-zones-calculator.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeartRateZonesCalculatorComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly age = signal<number | undefined>(30);
  readonly maxHr = signal<number | undefined>(undefined);
  readonly restingHr = signal<number | undefined>(60);

  readonly isLoading = signal<boolean>(false);
  readonly error = signal<string | null>(null);
  readonly zonesData = signal<HeartRateZonesResponse | null>(null);
  readonly selectedZone = signal<CalculatedHeartRateZone | null>(null);

  ngOnInit(): void {
    this.calculateZones();
  }

  calculateZones(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.analyticsService.calculateHeartRateZones({
      age: this.age(),
      maxHr: this.maxHr(),
      restingHr: this.restingHr()
    }).subscribe({
      next: (data) => {
        this.zonesData.set(data);
        if (data.zones && data.zones.length > 1) {
          // Select Zone 2 (Base Aerobic) by default
          this.selectedZone.set(data.zones[1]);
        } else if (data.zones && data.zones.length > 0) {
          this.selectedZone.set(data.zones[0]);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Failed to calculate heart rate zones. Please check your inputs.');
        this.isLoading.set(false);
      }
    });
  }

  selectZone(zone: CalculatedHeartRateZone): void {
    this.selectedZone.set(zone);
  }

  getZoneColorClass(zoneName?: string): string {
    if (!zoneName) return 'zone-default';
    if (zoneName.includes('ZONE_1') || zoneName.includes('1') || zoneName.includes('RECOVERY')) return 'zone-1';
    if (zoneName.includes('ZONE_2') || zoneName.includes('2') || zoneName.includes('AEROBIC')) return 'zone-2';
    if (zoneName.includes('ZONE_3') || zoneName.includes('3') || zoneName.includes('TEMPO')) return 'zone-3';
    if (zoneName.includes('ZONE_4') || zoneName.includes('4') || zoneName.includes('THRESHOLD')) return 'zone-4';
    if (zoneName.includes('ZONE_5') || zoneName.includes('5') || zoneName.includes('ANAEROBIC')) return 'zone-5';
    return 'zone-default';
  }
}
