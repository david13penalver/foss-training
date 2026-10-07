---
name: angular-best-practices
description: Conventions and guidelines for frontend development with Angular 22, standalone components, Signal-based reactivity, Vitest, and Tailwind CSS in foss-training-web.
---

# Angular 22 Best Practices & Conventions

Standard operating guide for the frontend application in `foss-training-web`.

---

## 1. Environment & Common Commands

```bash
cd foss-training-web

# Run unit tests with Vitest (single run)
npm test -- --watch=false

# Run tests in watch mode
npm test

# Build application
npm run build

# Start local development server (port 4200)
npm start

# Regenerate TypeScript API types from backend OpenAPI JSON
npm run codegen:api
```

---

## 2. Architecture & Reactive Model

### Standalone Components & Signals First
- All components, directives, and pipes must be standalone: `standalone: true`.
- Prefer Angular Signals (`signal()`, `computed()`, `effect()`) for reactive state over raw RxJS subjects whenever possible.
- Use signal-based inputs and outputs:
  ```typescript
  readonly exercise = input.required<Exercise>();
  readonly onSelect = output<string>();
  ```
- Use modern control flow syntax:
  - `@if (condition) { ... } @else { ... }`
  - `@for (item of items(); track item.id) { ... } @empty { ... }`
  - `@switch (category()) { @case ('RESISTANCE') { ... } }`

### State Management & Service Layer
- Domain services (`ExerciseService`, `SessionService`, `TrainingService`, `AnalyticsService`, `AthleteService`, `DataPortabilityService`):
  - Expose read-only signals to consumers: `readonly items = this._items.asReadonly();`
  - Maintain private writable signals internally: `private readonly _items = signal<Item[]>([]);`
  - Leverage `computed()` for derived state (e.g. filtered exercise lists, volume totals).
  - Use RxJS `Observable` for HTTP calls and bridge to signals via `.subscribe()` updating signals or `toSignal()`.

---

## 3. UI Design System: Glassmorphic & Dark Theme

The application adheres to an immersive dark-themed glassmorphic design system:
- **Surfaces**: Translucent panels with background blur (`backdrop-blur-md`, `bg-slate-900/80`, `border-slate-800/60`).
- **Accent Palettes**: Neon cyan (`cyan-400`), emerald green for completed states (`emerald-400`), amber for warnings (`amber-400`), rose for destructive actions (`rose-500`).
- **Interactive States**: Smooth hover transitions, active scale transforms, accessible focus rings (`focus-visible:ring-2 focus-visible:ring-cyan-400`).
- **Form Controls & Modals**: Modals must trap focus, handle Escape key, and provide accessible backdrop dismissals.

---

## 4. API Integration & Type Parity

- **Code Generation**: The API types are defined in `src/app/core/api/api-types.d.ts` and wrapped in `src/app/core/api/models.ts`.
- When backend DTOs change, run:
  ```bash
  npm run codegen:api
  ```
- Keep model definitions in sync between frontend and backend.

---

## 5. Testing with Vitest & JSDOM

- Unit tests reside in `*.spec.ts` files alongside components, services, and pipes.
- Running tests non-interactively:
  ```bash
  npm test -- --watch=false
  ```
- Conventions:
  - Test signal reactivity: verify component UI updates when signal values change.
  - Test service HTTP calls using `HttpTestingController`.
  - Ensure zero unhandled promise rejections or leaked timers in test teardown.
