import {Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {environment} from '../../environments/environment';
import {UserDto} from '../model/user-dto';
import {JwtHelperService} from '@auth0/angular-jwt';
import {isDefined} from '@angular/compiler/src/util';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {LoginForm} from '../model/login-form';
import {catchError, tap} from 'rxjs/operators';
import {HeaderType} from '../enum/header-type.enum';

@Injectable()
export class AuthenticationService {
  private readonly tokenKey = environment.storageTokenKey;
  readonly host = environment.apiUrl;
  private token: string;
  private loggedInUsername: string;
  private jwtHelperService = new JwtHelperService();

  constructor(private http: HttpClient) {
  }

  public login(loginForm: LoginForm): Observable<HttpResponse<any> | HttpErrorResponse> {
    return this.http.post<HttpResponse<UserDto> | HttpErrorResponse>(
      `${this.host}/user/login`, loginForm, {observe: `response`})
      .pipe(
        catchError(errorRes => {
          return throwError(errorRes);
        }),
        tap(response => {
          const token = response.headers.get(HeaderType.JWT_TOKEN);
          this.saveToken(token);
          if (response instanceof HttpResponse) {
            this.addUserToLocalStorage(response.body.body);
          }
        })
      );
  }

  public register(user: UserDto): Observable<UserDto | HttpErrorResponse> {
    return this.http.post<UserDto | HttpErrorResponse>(
      `${this.host}/user/register`, user, {observe: 'body'});
  }

  public logout(): void {
    this.token = null;
    this.loggedInUsername = null;
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
    this.loggedInUsername = this.jwtHelperService.decodeToken(this.token).sub;
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
