import {Injectable} from '@angular/core';
import {Observable, Subject, throwError, of} from 'rxjs';
import {environment} from '../../../environments/environment';
import {UserDto} from '../../shared/model/user-dto';
import {JwtHelperService} from '@auth0/angular-jwt';
import {HttpClient, HttpErrorResponse, HttpEvent, HttpResponse} from '@angular/common/http';
import {AuthForm, RegisterForm} from '../../shared/model/input-forms';
import {catchError, map, tap} from 'rxjs/operators';
import {Headers} from '../enum/header-type.enum';
import {NotificationType} from '../../shared/notification/notification-type.enum';
import {NotificationService} from '../../shared/notification/notification.service';
import {Router} from '@angular/router';
import {CustomHttpResponse} from '../../shared/model/custom-http-response';

@Injectable({providedIn: 'root'})
export class AuthenticationService {
  private readonly tokenKey = environment.storageTokenKey;
  private readonly refreshTokenKey = environment.storageTokenKey;
  readonly host = environment.apiUrl;
  private token: string;
  public loggedInUser: Subject<UserDto>;

  constructor(private http: HttpClient, private notificationService: NotificationService, private router: Router) {
    this.loggedInUser = new Subject<UserDto>();
    this.loggedInUser.next(this.getUserFromLocalStorage());
  }

  public login(loginForm: AuthForm): Observable<HttpEvent<any>> {
    return this.http.post<UserDto>(
      `${this.host}/user/login`, loginForm, {observe: `response`})
      .pipe(
        catchError(errorRes => {
          this.sendErrorNotification(NotificationType.ERROR, errorRes.error.message);
          return throwError(errorRes);
        }),
        tap((response: HttpResponse<UserDto>) => {
          const token = response.headers.get(Headers.JWT_TOKEN);
          this.saveToken(token);
          const refreshToken = response.headers.get(Headers.JWT_REFRESH_TOKEN);
          this.saveRefreshToken(refreshToken);
          this.addUserToLocalStorage(response.body);
          this.loggedInUser.next(response.body);
        })
      );
  }

  private sendErrorNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }

  public register(registerForm: RegisterForm): Observable<UserDto | HttpErrorResponse> {
    return this.http.post<UserDto | HttpErrorResponse>(
      `${this.host}/user/register`, registerForm, {observe: 'body'})
      .pipe(
        catchError(errorRes => {
          return throwError(errorRes);
        }),
        tap((response: UserDto) => {
          this.notificationService.notify(NotificationType.SUCCESS, `A new account was created for ${response.username}.
          Please check your email for password to log in.`);
        })
      );
  }

  public logout(): void {
    const refreshToken = localStorage.getItem(this.refreshTokenKey);
    this.http.post<HttpResponse<any>>(
      `${this.host}/token/delete`, refreshToken).subscribe(
      () => {
      },
      (errorResponse: HttpErrorResponse) => {
        console.error(errorResponse);
      });
    this.removeUserDataFromApp();
    this.router.navigateByUrl('/');
  }

  private removeUserDataFromApp() {
    this.token = null;
    this.loggedInUser.next(null);
    localStorage.removeItem('user');
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshTokenKey);
  }

  public saveRefreshToken(refreshToken: string): void {
    localStorage.setItem(this.refreshTokenKey, refreshToken);
  }

  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem(this.tokenKey, token);
  }

  public addUserToLocalStorage(user: UserDto): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getUserFromLocalStorage(): UserDto {
    return JSON.parse(localStorage.getItem('user'));
  }

  public getLoggedUsernameFromLocalStorage(): string {
    const user = this.getUserFromLocalStorage();
    return user == null ? null : user.username;
  }

  private loadToken(): void {
    this.token = localStorage.getItem(this.tokenKey);
  }

  public getToken(): string {
    if (this.token == null) {
      this.loadToken();
    }
    return this.token;
  }

  public refreshAccessToken(): Observable<string> {
    const refreshToken = localStorage.getItem(this.refreshTokenKey);
    return this.http.post<CustomHttpResponse | HttpErrorResponse>(
      `${this.host}/token/refresh`, refreshToken, {observe: `response`})
      .pipe(
        map(response => {
          const token = response.headers.get(Headers.JWT_TOKEN);
          this.saveToken(token);
          return this.token;
        })
      );
  }

  public isUserLoggedIn(): boolean {
    if (this.token == null) {
      this.loadToken();
    }
    return this.token !== null;
  }
}
