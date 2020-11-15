import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from './home/home.component';
import {RegisterComponent} from './main-page-components/register/register.component';
import {LoginPageComponent} from './main-page-components/login-page/login-page.component';
import {ProfileComponent} from './main-page-components/profile/profile.component';
import {AuthenticationGuard} from './auth/guard/authentication.guard';
import {ResetPasswordComponent} from './main-page-components/reset-password/reset-password.component';


const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    pathMatch: 'full'
  },
  {
    path: 'register',
    component: RegisterComponent,
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginPageComponent,
    pathMatch: 'full'
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    pathMatch: 'full'
  },
  {
    path: 'profile',
    component: ProfileComponent,
    pathMatch: 'full',
    canActivate: [AuthenticationGuard]
  },
  {
    path: 'g',
    loadChildren: () => import('./games/games.module').then(m => m.GamesModule),
    canActivate: [AuthenticationGuard]
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
