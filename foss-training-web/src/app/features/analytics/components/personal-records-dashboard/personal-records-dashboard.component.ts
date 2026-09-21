import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { AnalyticsService } from '../../../../core/services/analytics.service';
import type { PersonalRecordResponse } from '../../../../core/api/models';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-personal-records-dashboard',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './personal-records-dashboard.component.html',
  styleUrl: './personal-records-dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PersonalRecordsDashboardComponent {
  readonly analyticsService = inject(AnalyticsService);

  readonly searchQuery = signal('');

  readonly records = computed(() => {
    const raw = this.analyticsService.personalRecordsResource.value() || [];
    const query = this.searchQuery().toLowerCase().trim();
    if (!query) return raw;
    return raw.filter(r => r.exerciseName?.toLowerCase().includes(query));
  });

  reload() {
    this.analyticsService.personalRecordsResource.reload();
  }
}
