import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {User} from '../model/user';
import {JwtHelperService} from '@auth0/angular-jwt';
import {isDefined} from "@angular/compiler/src/util";
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private readonly host = environment.apiUrl;
  private readonly tokenKey = environment.storageTokenKey;
  private token: string;
  private loggedInUsername: string;
  private jwtHelperService = new JwtHelperService();

  constructor(private http: HttpClient) {
  }

  public login(user: User): Observable<HttpResponse<any> | HttpErrorResponse> {
    return this.http.post<HttpResponse<any> | HttpErrorResponse>(
      `${this.host}/user/login`, user, {observe: `response`});
  }

  public register(user: User): Observable<User | HttpErrorResponse> {
    return this.http.post<User | HttpErrorResponse>(
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

  public addUserToLocalStorage(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getUserFromLocalStorage(user: User): User {
    return JSON.parse(localStorage.getItem('user'));
  }

  public loadToken(): void {
    this.token = localStorage.getItem(this.tokenKey);
  }

  public getToken(): string {
    return this.token;
  }

  public isLoggedIn(): boolean {
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
