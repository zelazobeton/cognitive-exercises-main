import {Injectable} from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import {BehaviorSubject, Observable, throwError} from 'rxjs';
import {AuthenticationService} from '../service/authentication.service';
import {NonAuthenticatedUrlService} from '../service/non-authenticated-url.service';
import {catchError, filter, switchMap, take} from 'rxjs/operators';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private authenticationService: AuthenticationService,
              private nonAuthenticatedUrls: NonAuthenticatedUrlService) {
  }

  intercept(httpRequest: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const url = httpRequest.url.substr(this.authenticationService.host.length);
    if (this.nonAuthenticatedUrls.contain(url)) {
      return next.handle(httpRequest);
    }
    const token = this.authenticationService.getToken();
    const request = this.createRequestWithTokenHeader(httpRequest, token);
    return next.handle(request).pipe(
      catchError(error => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          return this.handle401Error(request, next);
        } else {
          return throwError(error);
        }
      }));
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.authenticationService.refreshAccessToken().pipe(
        switchMap(token => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(token);
          return next.handle(this.createRequestWithTokenHeader(request, token));
        }),
        catchError(error => {
          this.authenticationService.logout();
          return throwError(error);
        })
      );
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(jwt => {
          return next.handle(this.createRequestWithTokenHeader(request, jwt));
        }));
    }
  }

  private createRequestWithTokenHeader(httpRequest: HttpRequest<any>, token: string): HttpRequest<any> {
    return httpRequest.clone({setHeaders: {Authorization: `Bearer ${token}`}});
  }
}
