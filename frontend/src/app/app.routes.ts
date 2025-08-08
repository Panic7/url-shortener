import { Routes } from '@angular/router';
import { LayoutComponent } from './component/layout-component/layout.component';
import { HomeComponent } from './component/home-component/home.component';
import { DashboardComponent } from './component/dashboard-component/dashboard.component';
import { LoginPageComponent } from './component/login-page/login-page.component';
import { RegisterPageComponent } from './component/register-page/register-page.component';
import { authenticatedGuard } from './guard/authenticated.guard';
import { unauthenticatedGuard } from './guard/unauthenticated.guard';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      {
        path: 'dashboard',
        component: DashboardComponent,
        canActivate: [authenticatedGuard],
      },
      {
        path: 'login',
        component: LoginPageComponent,
        canActivate: [unauthenticatedGuard],
      },
      {
        path: 'register',
        component: RegisterPageComponent,
        canActivate: [unauthenticatedGuard],
      },
    ],
  },
];
