import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {UserService} from './service/user.service';
import {NotificationModule} from './shared/notification/notification.module';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import {LoginDropdownComponent} from './navbar/login-dropdown/login-dropdown.component';
import { RegisterComponent } from './register/register.component';
import {ReactiveFormsModule} from '@angular/forms';
import {LoginPageComponent} from './login-page/login-page.component';
import {AuthModule} from './auth/auth.module';

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
    AuthModule,
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    NotificationModule
  ],
  providers: [UserService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
