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
import {Router} from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private authenticationService: AuthenticationService,
              private nonAuthenticatedUrls: NonAuthenticatedUrlService,
              private router: Router) {
  }

  intercept(httpRequest: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // DOES THIS IF WORK?
    const url = httpRequest.url.substr(this.authenticationService.versionedHost.length);
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
        catchError(error => {
          this.isRefreshing = false;
          this.authenticationService.deleteRefreshToken();
          this.router.navigateByUrl('/login');
          return throwError(error);
        }),
        switchMap(token => {
          this.refreshTokenSubject.next(token);
          this.isRefreshing = false;
          return next.handle(this.createRequestWithTokenHeader(request, token));
        }),
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
