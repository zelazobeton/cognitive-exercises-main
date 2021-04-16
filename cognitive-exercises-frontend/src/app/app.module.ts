import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {UserService} from './shared/service/user.service';
import {PortfolioService} from './shared/service/portfolio.service';
import {NotificationModule} from './shared/notification/notification.module';
import { HomeComponent } from './pages/home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import {LoginDropdownComponent} from './navbar/login-dropdown/login-dropdown.component';
import { RegisterComponent } from './pages/register/register.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LoginPageComponent} from './pages/login-page/login-page.component';
import {AuthModule} from './auth/auth.module';
import { ProfileComponent } from './pages/profile/profile.component';
import { ChangePasswordComponent } from './pages/profile/change-password/change-password.component';
import { PersonalDataComponent } from './pages/profile/personal-data/personal-data.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import {GamesModule} from './pages/games/games.module';
import { ScoreboardComponent } from './pages/home/scoreboard/scoreboard.component';
import {GamesService} from './shared/service/games.service';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslationService} from './shared/service/translation.service';

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
    NotificationModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    })
  ],
  providers: [UserService, PortfolioService, GamesService, TranslationService],
  bootstrap: [AppComponent]
})
export class AppModule {
}

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}
