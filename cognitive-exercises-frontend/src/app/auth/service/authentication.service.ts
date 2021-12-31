import {Injectable} from '@angular/core';
import {Observable, Subject, throwError} from 'rxjs';
import {authServerUris, environment} from '../../../environments/environment';
import {UserDto} from '../../shared/model/user-dto';
import {HttpClient, HttpErrorResponse, HttpEvent, HttpHeaders, HttpResponse} from '@angular/common/http';
import {AuthForm, RegisterForm} from '../../shared/model/input-forms';
import {catchError, map, tap} from 'rxjs/operators';
import {NotificationType} from '../../shared/notification/notification-type.enum';
import {NotificationService} from '../../shared/notification/notification.service';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {AuthServerTokenForm} from './auth-server-token-form';

@Injectable({providedIn: 'root'})
export class AuthenticationService {
  private readonly tokenKey = environment.storageTokenKey;
  private readonly refreshTokenKey = environment.storageRefreshTokenKey;
  readonly versionedHost = environment.versionedApiUrl;
  readonly authorizationServerTokenUrl = authServerUris.authorizationServerTokenUrl;
  readonly authorizationServerLogoutUrl = authServerUris.authorizationServerLogoutUrl;
  readonly authorizationServerClientId = environment.authorizationServerClientId;
  private token: string;
  public loggedInUser: Subject<UserDto>;

  constructor(private http: HttpClient,
              private notificationService: NotificationService,
              private router: Router,
              private translate: TranslateService) {
    this.loggedInUser = new Subject<UserDto>();
    this.loggedInUser.next(this.getUserFromLocalStorage());
  }

  public login(loginForm: AuthForm): Observable<HttpEvent<any>> {
    const body = new URLSearchParams();
    body.set('grant_type', 'password');
    body.set('client_id', this.authorizationServerClientId);
    body.set('username', loginForm.username);
    body.set('password', loginForm.password);

    return this.http.post<AuthServerTokenForm>(
      this.authorizationServerTokenUrl, body.toString(), {
        headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded'),
        observe: `response`})
      .pipe(
        catchError(errorRes => {
          this.sendErrorNotification(NotificationType.ERROR, errorRes.error.message);
          return throwError(errorRes);
        }),
        tap((response: HttpResponse<AuthServerTokenForm>) => {
          this.saveToken(response.body.access_token);
          this.saveRefreshToken(response.body.refresh_token);
          const loggedUser = new UserDto();
          loggedUser.username = loginForm.username;
          this.addUserToLocalStorage(loggedUser);
          this.loggedInUser.next(loggedUser);
        })
      );
  }

  public register(registerForm: RegisterForm): Observable<UserDto | HttpErrorResponse> {
    return this.http.post<UserDto | HttpErrorResponse>(
      `${this.versionedHost}/user/register`, registerForm, {observe: 'body'})
      .pipe(
        catchError(errorRes => {
          return throwError(errorRes);
        }),
        tap((response: UserDto) => {
          this.notificationService.notify(NotificationType.SUCCESS,
            this.translate.instant('notifications.A new account was created for', {username: response.username})
          )
        })
      );
  }

  public logout(): Observable<HttpResponse<void>> {
    const body = new URLSearchParams();
    const refreshToken = localStorage.getItem(this.refreshTokenKey);
    body.set('refresh_token', refreshToken);
    body.set('client_id', this.authorizationServerClientId);

    return this.http.post<void>(
      this.authorizationServerLogoutUrl, body.toString(), {
        headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded'),
        observe: `response`})
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR,
            this.translate.instant('notifications.server error try again'));
          return throwError(errorRes);
        }),
        tap(() => {
          this.removeUserDataFromApp();
        })
    );
  }

  public removeUserDataFromApp() {
    this.token = null;
    this.loggedInUser.next(null);
    localStorage.removeItem('user');
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshTokenKey);
  }

  public getLoggedUsernameFromLocalStorage(): string {
    const user = this.getUserFromLocalStorage();
    return user == null ? null : user.username;
  }

  public getToken(): string {
    if (this.token == null) {
      this.loadToken();
    }
    return this.token;
  }

  public refreshAccessToken(): Observable<string> {
    const refreshToken = localStorage.getItem(this.refreshTokenKey);
    const body = new URLSearchParams();
    body.set('grant_type', 'refresh_token');
    body.set('client_id', this.authorizationServerClientId);
    body.set('refresh_token', refreshToken);

    return this.http.post<AuthServerTokenForm>(
      this.authorizationServerTokenUrl, body.toString(), {
        headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded'),
        observe: `response`})
      .pipe(
        catchError(errorRes => {
          this.sendErrorNotification(NotificationType.ERROR, errorRes.error.message);
          return throwError(errorRes);
        }),
        map(response => {
          this.saveToken(response.body.access_token);
          this.saveRefreshToken(response.body.refresh_token);
          return response.body.access_token;
        })
      );
  }

  public isUserLoggedIn(): boolean {
    if (this.token == null) {
      this.loadToken();
    }
    return this.token !== null;
  }

  private sendErrorNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, this.translate.instant('notifications.server error try again'));
    }
  }

  private saveRefreshToken(refreshToken: string): void {
    localStorage.setItem(this.refreshTokenKey, refreshToken);
  }

  private saveToken(token: string): void {
    this.token = token;
    localStorage.setItem(this.tokenKey, token);
  }

  private addUserToLocalStorage(user: UserDto): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  private getUserFromLocalStorage(): UserDto {
    return JSON.parse(localStorage.getItem('user'));
  }

  private loadToken(): void {
    this.token = localStorage.getItem(this.tokenKey);
  }
}
