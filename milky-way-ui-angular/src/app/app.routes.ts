import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { TestPlansComponent } from './pages/test-plans/test-plans.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'test-plans', component: TestPlansComponent },
];
