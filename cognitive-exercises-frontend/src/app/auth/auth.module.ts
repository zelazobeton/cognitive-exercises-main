import {NgModule} from '@angular/core';
import {AuthenticationGuard} from './guard/authentication.guard';
import {AuthenticationService} from './service/authentication.service';
import {NonAuthenticatedUrlService} from './service/non-authenticated-url.service';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthInterceptor} from './interceptor/auth.interceptor';

@NgModule({
  providers: [AuthenticationGuard, AuthenticationService, NonAuthenticatedUrlService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  exports: []
})
export class AuthModule {
}