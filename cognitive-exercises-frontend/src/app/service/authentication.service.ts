import {Injectable} from '@angular/core';
import {Observable, Subject, throwError} from 'rxjs';
import {environment} from '../../environments/environment';
import {UserDto} from '../model/user-dto';
import {JwtHelperService} from '@auth0/angular-jwt';
import {isDefined} from '@angular/compiler/src/util';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {AuthForm, RegisterForm} from '../model/auth-form';
import {catchError, tap} from 'rxjs/operators';
import {HeaderType} from '../enum/header-type.enum';
import {NotificationType} from '../../notification/notification-type.enum';
import {NotificationService} from '../../notification/notification.service';

@Injectable()
export class AuthenticationService {
  private readonly tokenKey = environment.storageTokenKey;
  readonly host = environment.apiUrl;
  private token: string;
  public loggedInUser: Subject<UserDto> = new Subject<UserDto>();
  private jwtHelperService = new JwtHelperService();

  constructor(private http: HttpClient, private notificationService: NotificationService) {
  }

  public login(loginForm: AuthForm): Observable<HttpResponse<any> | HttpErrorResponse> {
    return this.http.post<HttpResponse<UserDto> | HttpErrorResponse>(
      `${this.host}/user/login`, loginForm, {observe: `response`})
      .pipe(
        catchError(errorRes => {
          this.sendErrorNotification(NotificationType.ERROR, errorRes.error.message);
          return throwError(errorRes);
        }),
        tap(response => {
          const token = response.headers.get(HeaderType.JWT_TOKEN);
          this.saveToken(token);
          if (response instanceof HttpResponse) {
            this.loggedInUser.next(response.body);
            this.addUserToLocalStorage(response.body);
          }
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
    this.token = null;
    this.loggedInUser.next(null);
    localStorage.removeItem('user');
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('users');
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

  private loadToken(): void {
    this.token = localStorage.getItem(this.tokenKey);
  }

  public getToken(): string {
    this.loadToken();
    return this.token;
  }

  public isUserLoggedIn(): boolean {
    this.loadToken();
    if (!this.isTokenValid()) {
      this.logout();
      return false;
    }
    if (this.jwtHelperService.isTokenExpired(this.token)) {
      return false;
    }
    this.loggedInUser.next(this.jwtHelperService.decodeToken(this.token).sub);
    return true;
  }

  private isTokenValid(): boolean {
    if (isDefined(this.token) && this.token !== '') {
      const tokenSubject = this.jwtHelperService.decodeToken(this.token).sub;
      return this.isTokenSubjectValid(tokenSubject);
    }
    return false;
  }

  private isTokenSubjectValid(tokenSubject: string): boolean {
    return isDefined(tokenSubject) && tokenSubject !== '';
  }
}
