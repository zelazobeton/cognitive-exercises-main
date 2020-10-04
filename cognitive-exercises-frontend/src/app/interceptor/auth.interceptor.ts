import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthenticationService} from '../service/authentication.service';
import {NonAuthenticatedUrlService} from '../service/non-authenticated-url.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService, private nonAuthenticatedUrls: NonAuthenticatedUrlService) {
  }

  intercept(httpRequest: HttpRequest<any>, httpHandler: HttpHandler): Observable<HttpEvent<any>> {
    const url = httpRequest.url.substr(this.authenticationService.host.length);
    console.log('url: ' + url);
    if (this.nonAuthenticatedUrls.contain(url)) {
      return httpHandler.handle(httpRequest);
    }
    const token = this.authenticationService.getToken();
    const request = httpRequest.clone({setHeaders: {Authorization: `Bearer ${token}`}});
    return httpHandler.handle(request);
  }
}
