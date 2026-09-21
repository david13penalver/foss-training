import { Component, ChangeDetectionStrategy, signal } from '@angular/core';
import { BodyweightTrackerComponent } from '../../components/bodyweight-tracker/bodyweight-tracker.component';
import { RelativeStrengthCalculatorComponent } from '../../components/relative-strength-calculator/relative-strength-calculator.component';
import { DataSovereigntyHubComponent } from '../../components/data-sovereignty-hub/data-sovereignty-hub.component';

export type ProfileTab = 'bodyweight' | 'relative-strength' | 'data-sovereignty';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [
    BodyweightTrackerComponent,
    RelativeStrengthCalculatorComponent,
    DataSovereigntyHubComponent
  ],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProfilePageComponent {
  readonly activeTab = signal<ProfileTab>('bodyweight');

  setTab(tab: ProfileTab): void {
    this.activeTab.set(tab);
  }
}
