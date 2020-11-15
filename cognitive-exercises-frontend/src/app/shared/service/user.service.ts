import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {Observable, throwError} from 'rxjs';
import {CustomHttpResponse} from '../model/custom-http-response';
import {ChangePasswordForm} from '../model/input-forms';
import {catchError, tap} from 'rxjs/operators';
import {NotificationType} from '../notification/notification-type.enum';
import {NotificationService} from '../notification/notification.service';
import {UserDto} from '../model/user-dto';
import {UserScoreDto} from '../model/user-score-dto';

@Injectable()
export class UserService {
  private static SERVER_ERROR = 'Something went wrong, please try again';
  private static SCOREBOARD_ERROR = 'Something went wrong, scoreboard could not be loaded';
  private readonly host = environment.apiUrl;

  constructor(private http: HttpClient, private notificationService: NotificationService) {}

  public changePassword(changePasswordForm: ChangePasswordForm) {
    return this.http.post<HttpResponse<string> | HttpErrorResponse>(
      `${this.host}/user/change-password`, changePasswordForm, {observe: 'body'})
      .pipe(
        catchError(errorRes => {
          return throwError(errorRes);
        }),
        tap((response: HttpResponse<string>) => {
          this.notificationService.notify(NotificationType.SUCCESS, `Password successfully changed.`);
        })
      );
  }

  public resetPassword(email: FormData): Observable<CustomHttpResponse> {
    return this.http.post<CustomHttpResponse>(`${this.host}/user/reset-password`, email)
      .pipe(
        catchError(errorRes => {
          return throwError(errorRes);
        }),
        tap((response: CustomHttpResponse) => {
          this.notificationService.notify(NotificationType.SUCCESS, response.message);
        })
      );
  }

  public fetchUserData(): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.host}/user/data`)
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR, UserService.SERVER_ERROR);
          return throwError(errorRes);
        })
      );
  }

  public fetchScoreboard(pageNum = 0, pageSize = 10): Observable<UserScoreDto[]> {
    return this.http.get<UserScoreDto[]>(`${this.host}/portfolio/scoreboard`,
      {params: { page: pageNum.toString(), size: pageSize.toString() }})
      .pipe(
        catchError(errorRes => {
          this.notificationService.notify(NotificationType.ERROR, UserService.SCOREBOARD_ERROR);
          return throwError(errorRes);
        })
      );
  }

}