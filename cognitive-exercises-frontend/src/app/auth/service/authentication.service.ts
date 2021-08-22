import {Injectable} from '@angular/core';
import {Observable, Subject, throwError} from 'rxjs';
import {environment} from '../../../environments/environment';
import {UserDto} from '../../shared/model/user-dto';
import {HttpClient, HttpErrorResponse, HttpEvent, HttpHeaders, HttpResponse} from '@angular/common/http';
import {AuthForm, RegisterForm} from '../../shared/model/input-forms';
import {catchError, map, tap} from 'rxjs/operators';
import {NotificationType} from '../../shared/notification/notification-type.enum';
import {NotificationService} from '../../shared/notification/notification.service';
import {Router} from '@angular/router';
import {CustomHttpResponse} from '../../shared/model/custom-http-response';
import {TranslateService} from '@ngx-translate/core';
import {HttpEncodingType} from '../../shared/http.enum';
import {CustomHeaders} from '../enum/custom-headers.enum';

@Injectable({providedIn: 'root'})
export class AuthenticationService {
  private readonly tokenKey = environment.storageTokenKey;
  private readonly refreshTokenKey = environment.storageRefreshTokenKey;
  readonly versionedHost = environment.versionedApiUrl;
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
    return this.http.post<UserDto>(
      `${this.versionedHost}/user/login`, loginForm, {observe: `response`})
      .pipe(
        catchError(errorRes => {
          this.sendErrorNotification(NotificationType.ERROR, errorRes.error.message);
          return throwError(errorRes);
        }),
        tap((response: HttpResponse<UserDto>) => {
          const token = response.headers.get(CustomHeaders.JWT_TOKEN);
          this.saveToken(token);
          const refreshToken = response.headers.get(CustomHeaders.JWT_REFRESH_TOKEN);
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
      this.notificationService.notify(notificationType, this.translate.instant('notifications.server error try again'));
    }
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
          }
        ));
  }

  public logout(): Observable<CustomHttpResponse> {
    return this.http.get<CustomHttpResponse>(`${this.versionedHost}/user/logout`).pipe(
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

  public deleteRefreshToken(): void {
    const refreshToken = localStorage.getItem(this.refreshTokenKey);
    this.http.post<HttpResponse<any>>(
      `${this.versionedHost}/token/delete`, refreshToken).subscribe(
      () => {
      },
      (errorResponse: HttpErrorResponse) => {
        console.error(errorResponse);
      });
    this.removeUserDataFromApp();
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

  private getUserFromLocalStorage(): UserDto {
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
      `${this.versionedHost}/token/refresh`, refreshToken,
      {headers: new HttpHeaders().set(CustomHeaders.CONTENT_ENCODING, HttpEncodingType.NONE), observe: `response`})
      .pipe(
        map(response => {
          const token = response.headers.get(CustomHeaders.JWT_TOKEN);
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
