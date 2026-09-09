import { Routes } from '@angular/router';
import { SessionsPageComponent } from './pages/sessions-page/sessions-page.component';

export const SESSION_ROUTES: Routes = [
  {
    path: '',
    component: SessionsPageComponent
  }
];

export default SESSION_ROUTES;
