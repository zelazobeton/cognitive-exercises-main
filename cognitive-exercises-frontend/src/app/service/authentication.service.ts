import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {UserDto} from '../model/user-dto';
import {JwtHelperService} from '@auth0/angular-jwt';
import {isDefined} from '@angular/compiler/src/util';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';

@Injectable()
export class AuthenticationService {
  private readonly tokenKey = environment.storageTokenKey;
  readonly host = environment.apiUrl;
  private token: string;
  private loggedInUsername: string;
  private jwtHelperService = new JwtHelperService();

  constructor(private http: HttpClient) {
  }

  public login(user: UserDto): Observable<HttpResponse<any> | HttpErrorResponse> {
    return this.http.post<HttpResponse<any> | HttpErrorResponse>(
      `${this.host}/user/login`, user, {observe: `response`});
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

  public getUserFromLocalStorage(user: UserDto): UserDto {
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
