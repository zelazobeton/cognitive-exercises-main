import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {UserService} from './shared/service/user.service';
import {PortfolioService} from './shared/service/portfolio.service';
import {NotificationModule} from './shared/notification/notification.module';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './main-page-components/navbar/navbar.component';
import {LoginDropdownComponent} from './main-page-components/navbar/login-dropdown/login-dropdown.component';
import { RegisterComponent } from './main-page-components/register/register.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LoginPageComponent} from './main-page-components/login-page/login-page.component';
import {AuthModule} from './auth/auth.module';
import { ProfileComponent } from './main-page-components/profile/profile.component';
import { ChangePasswordComponent } from './main-page-components/profile/change-password/change-password.component';
import { PersonalDataComponent } from './main-page-components/profile/personal-data/personal-data.component';
import { ResetPasswordComponent } from './main-page-components/reset-password/reset-password.component';
import {GamesModule} from './games/games.module';
import { ScoreboardComponent } from './home/scoreboard/scoreboard.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginPageComponent,
    NavbarComponent,
    LoginDropdownComponent,
    RegisterComponent,
    ProfileComponent,
    ChangePasswordComponent,
    PersonalDataComponent,
    ResetPasswordComponent,
    ScoreboardComponent,
  ],
  imports: [
    AppRoutingModule,
    AuthModule,
    BrowserModule,
    FormsModule,
    GamesModule,
    HttpClientModule,
    ReactiveFormsModule,
    NotificationModule
  ],
  providers: [UserService, PortfolioService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
