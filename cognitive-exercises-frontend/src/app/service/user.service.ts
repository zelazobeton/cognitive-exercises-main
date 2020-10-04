import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { UserDto } from '../model/user-dto';
import {CustomHttpResponse} from '../model/custom-http-response';
import {UserScoringDto} from "../model/user-scoring-dto";

@Injectable({providedIn: 'root'})
export class UserService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getUsers(): Observable<UserScoringDto[] | HttpErrorResponse> {
    return this.http.get<UserScoringDto[]>(`${this.host}/user/list`);
  }

  public updateUser(userForm: FormData): Observable<UserDto> {
    return this.http.post<UserDto>(`${this.host}/user/update`, userForm);
  }

  public resetPassword(email: string): Observable<CustomHttpResponse> {
    return this.http.post<CustomHttpResponse>(`${this.host}/user/reset-password`, email);
  }

  public deleteUser(): Observable<CustomHttpResponse> {
    return this.http.delete<CustomHttpResponse>(`${this.host}/user/delete`);
  }

  public addUsersToLocalCache(users: UserDto[]): void {
    localStorage.setItem('users', JSON.stringify(users));
  }

  public getUsersFromLocalCache(): UserDto[] {
    if (localStorage.getItem('users')) {
      return JSON.parse(localStorage.getItem('users'));
    }
    return null;
  }

  public createUserFormDate(user: UserDto): FormData {
    const formData = new FormData();
    formData.append('username', user.username);
    formData.append('email', user.email);
    return formData;
  }
}
