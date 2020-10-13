import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {AuthenticationService} from './service/authentication.service';
import {UserService} from './service/user.service';
import {NonAuthenticatedUrlService} from './service/non-authenticated-url.service';
import {AuthInterceptor} from './interceptor/auth.interceptor';
import {AuthenticationGuard} from './guard/authentication.guard';
import {NotificationService} from '../notification/notification.service';
import {NotificationModule} from '../notification/notification.module';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import {LoginDropdownComponent} from './navbar/login-dropdown/login-dropdown.component';
import { RegisterComponent } from './register/register.component';
import {ReactiveFormsModule} from '@angular/forms';
import {LoginPageComponent} from './login-page/login-page.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginPageComponent,
    NavbarComponent,
    LoginDropdownComponent,
    RegisterComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    NotificationModule,
  ],
  providers: [AuthenticationGuard, AuthenticationService, NonAuthenticatedUrlService, UserService, NotificationService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }],
  bootstrap: [AppComponent]
})
export class AppModule {
}
