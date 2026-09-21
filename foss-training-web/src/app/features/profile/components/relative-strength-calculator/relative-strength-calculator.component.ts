import { Component, ChangeDetectionStrategy, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AthleteService } from '../../../../core/services/athlete.service';
import type { AthleteGender, RelativeStrengthResponse } from '../../../../core/api/models';

@Component({
  selector: 'app-relative-strength-calculator',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './relative-strength-calculator.component.html',
  styleUrl: './relative-strength-calculator.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RelativeStrengthCalculatorComponent {
  private readonly athleteService = inject(AthleteService);

  readonly latestBwResource = this.athleteService.latestBodyweightResource;
  readonly latestWeight = computed<number | undefined>(() => this.latestBwResource.value()?.weightKg);

  readonly totalWeightKg = signal<number | null>(null);
  readonly bodyweightKg = signal<number | null>(null);
  readonly gender = signal<AthleteGender>('MALE');

  readonly result = signal<RelativeStrengthResponse | null>(null);
  readonly isLoading = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);

  useLatestBodyweight(): void {
    const w = this.latestWeight();
    if (w) {
      this.bodyweightKg.set(w);
    }
  }

  calculate(): void {
    const total = this.totalWeightKg();
    const bw = this.bodyweightKg();

    if (!total || total <= 0) {
      this.errorMessage.set('Please enter a lifted total greater than 0 kg.');
      return;
    }
    if (!bw || bw <= 0) {
      this.errorMessage.set('Please enter a bodyweight greater than 0 kg.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.athleteService.calculateRelativeStrength({
      totalWeightKg: total,
      bodyweightKg: bw,
      gender: this.gender()
    }).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.result.set(res);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err?.error?.detail || 'Failed to calculate strength metrics.');
      }
    });
  }
}
