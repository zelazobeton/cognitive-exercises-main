import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {UserService} from './service/user.service';
import {PortfolioService} from './service/portfolio.service';
import {NotificationModule} from './shared/notification/notification.module';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import {LoginDropdownComponent} from './navbar/login-dropdown/login-dropdown.component';
import { RegisterComponent } from './register/register.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LoginPageComponent} from './login-page/login-page.component';
import {AuthModule} from './auth/auth.module';
import { ProfileComponent } from './profile/profile.component';
import { ChangePasswordComponent } from './profile/change-password/change-password.component';
import { PersonalDataComponent } from './profile/personal-data/personal-data.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import {GamesModule} from './games/games.module';

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
