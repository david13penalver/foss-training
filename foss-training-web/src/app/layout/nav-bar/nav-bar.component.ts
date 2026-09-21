import { Component, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface NavItem {
  readonly label: string;
  readonly path: string;
  readonly icon: string;
}

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavBarComponent {
  readonly navItems: readonly NavItem[] = [
    { label: 'Exercises', path: '/exercises', icon: '🏋️' },
    { label: 'Sessions', path: '/sessions', icon: '📋' },
    { label: 'Trainings', path: '/trainings', icon: '⚡' },
    { label: 'Programs', path: '/programs', icon: '🗓️' },
    { label: 'Analytics', path: '/analytics', icon: '📊' }
  ];
}
